/********************************************************************************
 *                                                                              *
 *  This file is part of Recraft.                                               *
 *                                                                              *
 *  Recraft is free software: you can redistribute it and/or modify             *
 *  it under the terms of the GNU General Public License as published by        *
 *  the Free Software Foundation, either version 3 of the License, or           *
 *  (at your option) any later version.                                         *
 *                                                                              *
 *  Recraft is distributed in the hope that it will be useful,                  *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the               *
 *  GNU General Public License for more details.                                *
 *                                                                              *
 *  You should have received a copy of the GNU General Public License           *
 *  along with Recraft.  If not, see <http://www.gnu.org/licenses/>.            *
 *                                                                              *
 *  Copyright 2012 Chris Foster.                                                *
 *                                                                              *
 ********************************************************************************/

package recraft.server.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.ListIterator;

import recraft.core.Packet;
import recraft.network.IncomingNetworkHandler;
import recraft.network.OutgoingNetworkHandler;

// XXX Do we need some interface for exposing the list of clients?

public final class ServerNetworkHandler implements Runnable
{
	public final LinkedList<ClientPacket> clientPackets;

	// function to close
	private ServerSocket serverSocket;
	private LinkedList<Client> clients;

	private LinkedList<Packet> broadcastQueue;

	public ServerNetworkHandler(int portNumber)
	{
		try
		{
			this.serverSocket = new ServerSocket(portNumber);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		this.clients = new LinkedList<Client>();
		this.clientPackets = new LinkedList<ClientPacket>();

		this.broadcastQueue = new LinkedList<Packet>();
	}

	public void collectClientPackets()
	{
		synchronized (this.clientPackets)
		{
			this.clientPackets.clear();
		}
		synchronized (this.clients)
		{
			ListIterator clientIterator = this.clients.listIterator();
			while (clientIterator.hasNext())
			{
				Client client = (Client)clientIterator.next();
				synchronized (client.incomingPackets)
				{
					ListIterator packetIterator = client.incomingPackets.listIterator();
					while (packetIterator.hasNext())
						synchronized (this.clientPackets)
						{
							this.clientPackets.add(new ClientPacket(client, (Packet)packetIterator.next()));
						}
				}
				client.clearPackets();
			}
		}
	}

	/** Enqueue the packet to be broadcast to all clients. */
	public void enqueuePacket(Packet packet)
	{
		synchronized (this.broadcastQueue)
		{
			this.broadcastQueue.add(packet);
		}
	}

	/** Enqueue the packet for only the given client. */
	public void enqueuePacket(Client client, Packet packet)
	{
		client.enqueuePacket(packet);
	}

	public void sendPackets()
	{
		synchronized (this.broadcastQueue)
		{
			synchronized (this.clients)
			{
				ListIterator packetIterator = this.broadcastQueue.listIterator();
				while (packetIterator.hasNext())
				{
					Packet packet = (Packet)packetIterator.next();

					ListIterator clientIterator = this.clients.listIterator();
					while (clientIterator.hasNext())
					{
						Client client = (Client)clientIterator.next();
						client.enqueuePacket(packet);
					}
				}

				ListIterator clientIterator = this.clients.listIterator();
				while (clientIterator.hasNext())
				{
					Client client = (Client)clientIterator.next();
					client.sendPackets();
				}
			}

			this.broadcastQueue.clear();
		}
	}

	public void close()
	{
		synchronized (this.clients)
		{
			ListIterator clientIterator = this.clients.listIterator();
			while (clientIterator.hasNext())
			{
				Client client = (Client)clientIterator.next();
				client.close();
				clientIterator.remove();
			}

			try
			{
				this.serverSocket.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() // TODO Probably turn this into a private class
	{
		while (true)
		{
			Socket clientSocket = null;
			try
			{
				clientSocket = this.serverSocket.accept();
			}
			catch (SocketException e)
			{
				break;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (clientSocket != null)
				synchronized (this.clients)
				{
					this.clients.add(new Client(clientSocket));
				}
		}
	}

	public static final class Client
	{
		public final Socket clientSocket;
		public final OutgoingNetworkHandler outgoingNetworkHandler;
		public final IncomingNetworkHandler incomingNetworkHandler;

		/** Alias for incomingNetworkHandler.incomingQueue. */
		public final LinkedList<Packet> incomingPackets;

		public Client(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
			this.outgoingNetworkHandler = new OutgoingNetworkHandler(clientSocket);
			this.incomingNetworkHandler = new IncomingNetworkHandler(clientSocket);

			this.incomingPackets = this.incomingNetworkHandler.incomingQueue;
		}

		/** Alias for outgoingNetworkHandler.enqueuePacket. */
		public void enqueuePacket(Packet packet)
		{
			this.outgoingNetworkHandler.enqueuePacket(packet);
		}

		/** Alias for outgoingNetworkHandler.sendPackets. */
		public void sendPackets()
		{
			this.outgoingNetworkHandler.sendPackets();
		}

		public void clearPackets()
		{
			synchronized (this.incomingPackets) // This works to lock incomingNetworkHandler.incomingQueue, right?
			{
				this.incomingPackets.clear();
			}
		}

		public void close()
		{
			this.incomingNetworkHandler.close();
			this.outgoingNetworkHandler.close();
			try
			{
				this.clientSocket.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static final class ClientPacket extends Packet
	{
		public final Client owner;

		private Packet child;

		public ClientPacket(Client owner, Packet child)
		{
			this.owner = owner;
			this.child = child;
		}

		@Override
		public int getId() { return this.child.getId(); }

		@Override
		public Object open() { return this.child.open(); }
	}

}
