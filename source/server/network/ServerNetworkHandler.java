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
	public final LinkedList<ClientPacket> newcomerPackets;

	private ServerSocket serverSocket;
	private Object serverLock;

	private LinkedList<ClientNetworkHandler> clients;
	private LinkedList<ClientNetworkHandler> newcomers;

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
		this.newcomers = new LinkedList<ClientNetworkHandler>();
		this.newcomerPackets = new LinkedList<ClientPacket>();

		this.broadcastQueue = new LinkedList<Packet>();

		this.connectionListener = new Thread(new ConnectionListener(this.serverSocket, this.newcomers));
		this.connectionListener.start();
	}

	/** Collects received packets from all connected clients and newcomers and dumps them in clientPackets
	 * and newcomerPackets respectively.
	 *
	 * @return
	 * <b>boolean</b> - Whether or not the server has been shut down.
	 */
	public boolean collectClientPackets()
	{
		synchronized (this.serverLock)
		{
			if (this.serverSocket == null)
				return false;

			this.clientPackets.clear();
			this.newcomerPackets.clear();

			// Get packets from all clients
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

			// Get all newcomer packets
			synchronized (this.newcomers)
			{
				ListIterator newcomerIterator = this.newcomers.listIterator();
				while (newcomerIterator.hasNext())
				{
					ClientNetworkHandler newcomer = (ClientNetworkHandler)newcomerIterator.next();
					synchronized (newcomer.incomingPackets)
					{
						ListIterator packetIterator = newcomer.incomingPackets.listIterator();
						while (packetIterator.hasNext())
							this.newcomerPackets.add(new ClientPacket(newcomer, (Packet)packetIterator.next()));
					}
					newcomer.clearIncomingPackets();
				}
			}
		}
		return true;
	}

	/** Enqueue the packet to be broadcast to all clients.
	 *
	 * @return
	 * <b>true</b> - The packet was successfully enqueued.<br />
	 * <b>false</b> - The server was previously closed.
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
	 * <b>false</b> - The server was previously closed or there was an issue with the client.
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

	public int getClientCount()
	{
		synchronized (this.serverLock)
		{
			return this.clients.size();
		}
	}

	public int getNewcomerCount()
	{
		synchronized (this.newcomers)
		{
			return this.newcomers.size();
		}
	}

	/** If a client is trusted and should receive server broadcasts, this function can be used to move the client
	 * from the server's newcomer pool to the client pool.
	 *
	 * @return
	 * <b>boolean</b> - Whether or not the graduation succeeded.
	 */
	public boolean graduateClient(ClientNetworkHandler client)
	{
		if (client == null)
			return false;

		// Find and remove the client from the newcomers list
		boolean foundMatch = false;
		synchronized (this.newcomers)
		{
			ListIterator newcomerIterator = this.newcomers.listIterator();
			while (newcomerIterator.hasNext())
			{
				ClientNetworkHandler newcomer = (ClientNetworkHandler)newcomerIterator.next();
				if (client == newcomer)
				{
					newcomerIterator.remove();
					foundMatch = true;
					break;
				}
			}
		}

		if (!foundMatch)
			return false;

		// Add the client to the client list
		synchronized (this.serverLock)
		{
			this.clients.add(client);
		}
		return true;
	}

	/** Purge disconnected clients from the server */
	public void purgeClients()
	{
		synchronized (this.serverLock)
		{
			if (this.serverSocket == null)
				return;

			// Purge dead clients
			ListIterator clientIterator = this.clients.listIterator();
			while (clientIterator.hasNext())
			{
				ClientNetworkHandler client = (ClientNetworkHandler)clientIterator.next();
				if (!client.isIntact())
				{
					client.close();
					clientIterator.remove();
				}
			}

			// Purge dead newcomers
			synchronized (this.newcomers)
			{
				ListIterator newcomerIterator = this.newcomers.listIterator();
				while (newcomerIterator.hasNext())
				{
					ClientNetworkHandler newcomer = (ClientNetworkHandler)newcomerIterator.next();
					if (!newcomer.isIntact())
					{
						newcomer.close();
						newcomerIterator.remove();
					}
				}
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

			// Close clients
			ListIterator clientIterator = this.clients.listIterator();
			while (clientIterator.hasNext())
			{
				ClientNetworkHandler client = (ClientNetworkHandler)clientIterator.next();
				client.close();
				clientIterator.remove();
			}

			// Close newcomers
			synchronized (this.newcomers)
			{
				ListIterator newcomerIterator = this.newcomers.listIterator();
				while (newcomerIterator.hasNext())
				{
					ClientNetworkHandler newcomer = (ClientNetworkHandler)newcomerIterator.next();
					newcomer.close();
					newcomerIterator.remove();
				}
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

			this.connectionListener = null;
			this.serverSocket = null;
		}
	}

	/** Runs inside a thread to listen for and handle new clients. */
	private static final class ConnectionListener implements Runnable
	{
		private ServerSocket serverSocket;
		private LinkedList<ClientNetworkHandler> newcomers;

		public ConnectionListener(ServerSocket serverSocket, LinkedList<ClientNetworkHandler> newcomers)
		{
			this.serverSocket = serverSocket;
			this.newcomers = newcomers;
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
					break;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				if (clientSocket != null)
					synchronized (this.newcomers)
					{
						this.newcomers.add(new ClientNetworkHandler(clientSocket));
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
