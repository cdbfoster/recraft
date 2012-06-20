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

package recraft.networkinterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import recraft.core.Creatable;
import recraft.core.NetworkInterface;
import recraft.core.NetworkNodeIdentifier;
import recraft.core.Packet;

public class UDPNetworkInterface implements NetworkInterface, Creatable
{
	private DatagramSocket socket;

	private LinkedList<NodePacketPair> outgoingPackets;
	private LinkedList<NodePacketPair> incomingPackets;
	private HashMap<NetworkNodeIdentifier, LinkedList<Packet>> incomingPacketsMap;

	private Thread listener;

	private Object lock;

	public static UDPNetworkInterface create() throws SocketException
	{
		return new UDPNetworkInterface();
	}

	public static UDPNetworkInterface create(Integer port) throws SocketException
	{
		return new UDPNetworkInterface(port.intValue());
	}

	public UDPNetworkInterface() throws SocketException
	{
		this.socket = new DatagramSocket();

		this.outgoingPackets = new LinkedList<NodePacketPair>();
		this.incomingPackets = new LinkedList<NodePacketPair>();
		this.incomingPacketsMap = new HashMap<NetworkNodeIdentifier, LinkedList<Packet>>();

		this.listener = new Thread(new Listener(this.socket, this.incomingPackets, this.incomingPacketsMap));
		this.listener.setDaemon(true);
		this.listener.start();

		this.lock = new Object();
	}

	public UDPNetworkInterface(int port) throws SocketException
	{
		this.socket = new DatagramSocket(port);

		this.outgoingPackets = new LinkedList<NodePacketPair>();
		this.incomingPackets = new LinkedList<NodePacketPair>();
		this.incomingPacketsMap = new HashMap<NetworkNodeIdentifier, LinkedList<Packet>>();

		this.listener = new Thread(new Listener(this.socket, this.incomingPackets, this.incomingPacketsMap));
		this.listener.setDaemon(true);
		this.listener.start();

		this.lock = new Object();
	}

	@Override
	public boolean sendPacket(NetworkNodeIdentifier node, Packet packet)
	{
		byte[] packetBytes = this.serializeObject(packet);

		if (packetBytes == null)
			return false;

		DatagramPacket datagram = new DatagramPacket(packetBytes, packetBytes.length, node.inetAddress, node.port);

		try
		{
			this.socket.send(datagram);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean enqueuePacket(NetworkNodeIdentifier node, Packet packet)
	{
		synchronized (this.lock)
		{
			if (this.socket == null)
				return false;

			this.outgoingPackets.add(new NodePacketPair(node, packet));
		}
		return true;
	}

	@Override
	public boolean sendPacketQueue()
	{
		boolean totalSuccess = true;
		synchronized (this.lock)
		{
			if (this.socket == null)
				return false;

			// Send each packet in the queue
			ListIterator packetIterator = this.outgoingPackets.listIterator();
			while (packetIterator.hasNext())
			{
				NodePacketPair pair = (NodePacketPair)packetIterator.next();
				totalSuccess &= this.sendPacket(pair.node, pair.packet);
			}

			this.outgoingPackets.clear();
		}
		return true;
	}

	@Override
	public LinkedList<NodePacketPair> getIncomingPackets()
	{
		synchronized (this.lock)
		{
			if (this.socket == null)
				return null;
		}
		return this.incomingPackets;
	}

	@Override
	public HashMap<NetworkNodeIdentifier, LinkedList<Packet>> getIncomingPacketsMap()
	{
		synchronized (this.lock)
		{
			if (this.socket == null)
				return null;
		}
		return this.incomingPacketsMap;
	}

	@Override
	public void clearIncomingPackets()
	{
		synchronized (this.lock)
		{
			if (this.socket == null)
				return;

			synchronized (this.incomingPackets)
			{
				this.incomingPackets.clear();
			}
			synchronized (this.incomingPacketsMap)
			{
				this.incomingPacketsMap.clear();
			}
		}
	}

	@Override
	public void close()
	{
		synchronized (this.lock)
		{
			if (this.socket == null)
				return;

			this.listener.interrupt();
			this.socket.close();

			try
			{
				this.listener.join();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			this.listener = null;
			this.socket = null;

			synchronized (this.incomingPackets)
			{
				this.incomingPackets.clear();
				this.incomingPackets = null;
			}
			synchronized (this.incomingPacketsMap)
			{
				this.incomingPacketsMap.clear();
				this.incomingPacketsMap = null;
			}
		}
	}

	private byte[] serializeObject(Object object)
	{
		byte[] bytes = null;

		try
		{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

			objectStream.writeObject(object);
			objectStream.flush();

			bytes = byteStream.toByteArray();
			objectStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return bytes;
	}

	private static class Listener implements Runnable
	{
		private DatagramSocket socket;
		private LinkedList<NodePacketPair> incomingPackets;
		private HashMap<NetworkNodeIdentifier, LinkedList<Packet>> incomingPacketsMap;

		public Listener(DatagramSocket socket, LinkedList<NodePacketPair> incomingPackets, HashMap<NetworkNodeIdentifier, LinkedList<Packet>> incomingPacketsMap)
		{
			this.socket = socket;
			this.incomingPackets = incomingPackets;
			this.incomingPacketsMap = incomingPacketsMap;
		}

		@Override
		public void run()
		{
			while (!Thread.interrupted())
			{
				// Construct datagram to fill
				byte[] packetBytes = new byte[10240]; // Max packet size is 10kB?  Is this too small? Too big?
				DatagramPacket datagram = new DatagramPacket(packetBytes, packetBytes.length);

				try
				{
					this.socket.receive(datagram);
				}
				catch (SocketException e)
				{
					break;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				// Collect info about the received packet.
				Packet packet = (Packet)this.deserializeObject(packetBytes);
				if (packet == null)
					continue;
				NetworkNodeIdentifier node = new NetworkNodeIdentifier(datagram.getAddress(), datagram.getPort());

				// Add the packet to the incoming queues
				synchronized (this.incomingPackets)
				{
					this.incomingPackets.add(new NodePacketPair(node, packet));
				}
				synchronized (this.incomingPacketsMap)
				{
					if (!this.incomingPacketsMap.containsKey(node))
					{
						LinkedList<Packet> list = new LinkedList<Packet>();
						list.add(packet);
						this.incomingPacketsMap.put(node, list);
					} else
						this.incomingPacketsMap.get(node).add(packet);
				}
			}
		}

		private Object deserializeObject(byte[] bytes)
		{
			Object object = null;

			try
			{
				ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(bytes));

				object = objectStream.readObject();

				objectStream.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return object;
		}
	}
}
