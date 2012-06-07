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

package recraft.network;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.ListIterator;

import recraft.core.Packet;

public class OutgoingNetworkHandler
{
	private Socket outgoingSocket;
	private Object socketLock;

	private LinkedList<Packet> outgoingQueue;

	private ObjectOutputStream outgoingStream;

	public OutgoingNetworkHandler(Socket outgoingSocket, Object socketLock)
	{
		this.outgoingSocket = outgoingSocket;
		this.socketLock = socketLock;

		this.outgoingQueue = new LinkedList<Packet>();

		try
		{
			this.outgoingStream = new ObjectOutputStream(this.outgoingSocket.getOutputStream());
			this.outgoingStream.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public OutgoingNetworkHandler(Socket outgoingSocket)
	{
		this(outgoingSocket, new Object());
	}

	public boolean enqueuePacket(Packet packet)
	{
		synchronized (this.socketLock)
		{
			if (this.outgoingSocket == null)
				return false;

			this.outgoingQueue.add(packet);
			return true;
		}
	}

	public boolean sendPackets()
	{
		synchronized (this.socketLock)
		{
			if (this.outgoingSocket == null)
				return false;

			try
			{
				ListIterator packetIterator = this.outgoingQueue.listIterator();
				while (packetIterator.hasNext())
				{
					this.outgoingStream.writeObject(packetIterator.next());
					packetIterator.remove();
				}
				this.outgoingStream.flush();
			}
			catch (SocketException e)
			{
				return false;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	public void close()
	{
		synchronized (this.socketLock)
		{
			if (this.outgoingSocket == null)
				return;

			try
			{
				this.outgoingSocket.shutdownOutput();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			this.outgoingStream = null;
			this.outgoingSocket = null;
		}
	}
}
