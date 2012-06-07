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

package recraft.client.network;

import java.net.Socket;
import java.util.LinkedList;

import recraft.core.Packet;
import recraft.network.IncomingNetworkHandler;
import recraft.network.OutgoingNetworkHandler;

/** Handles sending and receiving of packets through a given Socket or hostName/portNumber pair.
 * Packets are enqueued and then sent with enqueuePacket() and sendPackets() respectively.
 * Received packets are contained in incomingPackets, which can be cleared with clearIncomingPackets().
 * This class is thread-safe. */
public final class ClientNetworkHandler
{
	/** Alias for incomingNetworkHandler.incomingQueue. */
	public final LinkedList<Packet> incomingPackets;

	private Socket clientSocket;
	private Object socketLock;

	private OutgoingNetworkHandler outgoingNetworkHandler;
	private IncomingNetworkHandler incomingNetworkHandler;

	public ClientNetworkHandler(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
		this.socketLock = new Object();

		this.outgoingNetworkHandler = new OutgoingNetworkHandler(this.clientSocket, this.socketLock);
		this.incomingNetworkHandler = new IncomingNetworkHandler(this.clientSocket, this.socketLock);

		this.incomingPackets = this.incomingNetworkHandler.incomingQueue;
	}

	public ClientNetworkHandler(String hostName, int portNumber)
	{
		Socket clientSocket = null;
		try
		{
			clientSocket = new Socket(hostName, portNumber);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		this.clientSocket = clientSocket;

		this.socketLock = new Object();
		this.outgoingNetworkHandler = new OutgoingNetworkHandler(this.clientSocket, this.socketLock);
		this.incomingNetworkHandler = new IncomingNetworkHandler(this.clientSocket, this.socketLock);

		this.incomingPackets = this.incomingNetworkHandler.incomingQueue;
	}

	/** Enqueue packet to be sent next time sendPackets() is called.
	 *
	 * @return
	 * <b>true</b> - The packet was successfully enqueued.<br />
	 * <b>false</b> - The socket was previously closed.
	 */
	public boolean enqueuePacket(Packet packet)
	{
		synchronized (this.socketLock)
		{
			if (this.clientSocket == null)
				return false;

			return this.outgoingNetworkHandler.enqueuePacket(packet);
		}
	}

	/** Send all enqueued packets.
	 *
	 * @return
	 * <b>true</b> - All packets were sent successfully.  Empties outgoingQueue. <br />
	 * <b>false</b> - The local socket is closed or disconnected from the remote socket.
	 * Unsent packets remain in the outgoingNetworkHandler's queue.
	 */
	public boolean sendPackets()
	{
		synchronized (this.socketLock)
		{
			if (this.clientSocket == null)
				return false;

			return this.outgoingNetworkHandler.sendPackets();
		}
	}

	/** Clears all received packets in incomingPackets. */
	public void clearIncomingPackets()
	{
		synchronized (this.incomingPackets)
		{
			this.incomingPackets.clear();
		}
	}

	public boolean isIntact()
	{
		return this.incomingNetworkHandler.isIntact();
	}

	/** Close down the socket and network handlers.  Further use of class methods will do nothing. */
	public void close()
	{
		synchronized (this.socketLock)
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
}
