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

/** Handles server-like network responsibilities such as accepting new connections to clients, collecting
 * packets received from clients, and broadcasting packets to connected clients.  This class is thread-safe. */
public final class ServerNetworkHandler
{
	public final LinkedList<ClientPacket> clientPackets;

	private ServerSocket serverSocket;
	private Object serverLock;

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

		this.serverLock = new Object();

		this.clients = new LinkedList<ClientNetworkHandler>();
		this.clientPackets = new LinkedList<ClientPacket>();

		this.broadcastQueue = new LinkedList<Packet>();

		this.connectionListener = new Thread(new ConnectionListener(this.serverSocket, this.clients));
		this.connectionListener.start();
	}

	public boolean collectClientPackets()
	{
		synchronized (this.serverLock)
		{
			if (this.serverSocket == null)
				return false;

			this.clientPackets.clear();
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
							this.clientPackets.add(new ClientPacket(client, (Packet)packetIterator.next()));
					}
					client.clearIncomingPackets();
				}
			}
		}
		return true;
	}

	/** Enqueue the packet to be broadcast to all clients.
	 *
	 * @return
	 * <b>true</b> - The packet was successfully enqueued.<br />
	 * <b>false</b> - The socket was previously closed.
	 */
	public boolean enqueuePacket(Packet packet)
	{
		synchronized (this.serverLock)
		{
			if (this.serverSocket == null)
				return false;

			this.broadcastQueue.add(packet);
		}
		return true;
	}

	/** Enqueue packet for only the given client.
	 *
	 * @return
	 * <b>true</b> - The packet was successfully enqueued.<br />
	 * <b>false</b> - The socket was previously closed or there was an issue with the client.
	 */
	public boolean enqueuePacket(ClientNetworkHandler client, Packet packet)
	{
		synchronized (this.serverLock)
		{
			if (this.serverSocket == null)
				return false;

			return client.enqueuePacket(packet);
		}
	}

	/** Broadcast any enqueued packets and instruct all connected ClientNetworkHandlers to send their
	 * individually enqueued packets.
	 *
	 * @return
	 * <b>boolean[]</b> - An array of booleans the size of the number of connected clients.  Each entry contains
	 * <b>true</b> or <b>false</b> depending on whether or not the corresponding ClientNetworkHandler was successful
	 * in sending its enqueued packets to the connected client.
	 */
	public boolean[] sendPackets()
	{
		synchronized (this.serverLock)
		{
			if (this.serverSocket == null)
				return null;

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

				boolean[] sent = new boolean[this.clients.size()];
				int currentClient = 0;

				ListIterator clientIterator = this.clients.listIterator();
				while (clientIterator.hasNext())
				{
					ClientNetworkHandler client = (ClientNetworkHandler)clientIterator.next();
					sent[currentClient++] = client.sendPackets();
				}

				this.broadcastQueue.clear();
				return sent;
			}
		}
	}

	/** Close down all communications.  Further use of class methods will do nothing. */
	public void close()
	{
		synchronized (this.serverLock)
		{
			if (this.serverSocket == null)
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
			this.serverSocket = null;
		}
	}

	/** Runs inside a thread to listen for and handle new clients. */
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

	/** Packet wrapper class that can point to the connected client that sent the packet. */
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
