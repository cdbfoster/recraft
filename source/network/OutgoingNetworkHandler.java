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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.ListIterator;

import recraft.core.Packet;

// TODO Docstrings for this stuff.
public class OutgoingNetworkHandler
{
	private Socket outgoingSocket;
	private LinkedList<Packet> outgoingQueue;

	//private BufferedOutputStream bufferedStream;
	private ObjectOutputStream outgoingStream;

	public OutgoingNetworkHandler(Socket outgoingSocket)
	{
		this.outgoingSocket = outgoingSocket;
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

	/** Enqueue packet to be sent next time sendPackets() is called. */
	public void enqueuePacket(Packet packet)
	{
		synchronized (this.outgoingQueue)
		{
			this.outgoingQueue.add(packet);
		}
	}

	public void sendPackets()
	{
		synchronized (this.outgoingStream)
		{
			synchronized (this.outgoingQueue)
			{
				try
				{
					ListIterator iterator = this.outgoingQueue.listIterator();
					while (iterator.hasNext())
						this.outgoingStream.writeObject(iterator.next());
					this.outgoingStream.flush();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				this.outgoingQueue.clear();
			}
		}
	}

	public void close()
	{
		synchronized (this.outgoingStream)
		{
			try
			{
				this.outgoingSocket.shutdownOutput();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
