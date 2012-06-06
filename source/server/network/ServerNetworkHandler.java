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

import recraft.client.network.ClientNetworkHandler;
import recraft.core.Packet;

// XXX Do we need some interface for exposing the list of clients?

public final class ServerNetworkHandler
{
	public final LinkedList<ClientPacket> clientPackets;

	private ServerSocket serverSocket;

	private LinkedList<ClientNetworkHandler> clients;

	private LinkedList<Packet> broadcastQueue;

	private Thread connectionListener;

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

		this.clients = new LinkedList<ClientNetworkHandler>();
		this.clientPackets = new LinkedList<ClientPacket>();

		this.broadcastQueue = new LinkedList<Packet>();

		this.connectionListener = new Thread(new ConnectionListener(this.serverSocket, this.clients));
		this.connectionListener.start();
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
				ClientNetworkHandler client = (ClientNetworkHandler)clientIterator.next();
				synchronized (client.incomingPackets)
				{
					ListIterator packetIterator = client.incomingPackets.listIterator();
					while (packetIterator.hasNext())
						synchronized (this.clientPackets)
						{
							this.clientPackets.add(new ClientPacket(client, (Packet)packetIterator.next()));
						}
				}
				client.clearIncomingPackets();
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
	public void enqueuePacket(ClientNetworkHandler client, Packet packet)
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
						ClientNetworkHandler client = (ClientNetworkHandler)clientIterator.next();
						client.enqueuePacket(packet);
					}
				}

				ListIterator clientIterator = this.clients.listIterator();
				while (clientIterator.hasNext())
				{
					ClientNetworkHandler client = (ClientNetworkHandler)clientIterator.next();
					client.sendPackets();
				}
			}

			this.broadcastQueue.clear();
		}
	}

	public void close()
	{
		if (this.connectionListener == null)
			return;

		synchronized (this.clients)
		{
			ListIterator clientIterator = this.clients.listIterator();
			while (clientIterator.hasNext())
			{
				ClientNetworkHandler client = (ClientNetworkHandler)clientIterator.next();
				client.close();
				clientIterator.remove();
			}

			try
			{
				this.connectionListener.interrupt();
				this.serverSocket.close();
				this.connectionListener.join();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		this.connectionListener = null;
	}

	private static final class ConnectionListener implements Runnable
	{
		private ServerSocket serverSocket;
		private LinkedList<ClientNetworkHandler> clients;

		public ConnectionListener(ServerSocket serverSocket, LinkedList<ClientNetworkHandler> clients)
		{
			this.serverSocket = serverSocket;
			this.clients = clients;
		}

		@Override
		public void run()
		{
			while (!Thread.interrupted())
			{
				Socket clientSocket = null;
				try
				{
					clientSocket = this.serverSocket.accept();
				}
				catch (SocketException e)
				{
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				if (clientSocket != null)
					synchronized (this.clients)
					{
						this.clients.add(new ClientNetworkHandler(clientSocket));
					}
			}
		}

	}

	public static final class ClientPacket extends Packet
	{
		public final ClientNetworkHandler owner;

		private Packet child;

		public ClientPacket(ClientNetworkHandler owner, Packet child)
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
