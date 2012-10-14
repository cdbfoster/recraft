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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import recraft.core.Creatable;
import recraft.core.NetworkInterface;
import recraft.core.NetworkNodeIdentifier;
import recraft.core.Packet;

public class UDPNetworkInterface implements NetworkInterface, Creatable
{
	private static final int bufferSize = 64 * 1024; // 64kB

	private DatagramSocket socket;

	private List<NodePacketPair> outgoingPackets;
	private List<NodePacketPair> incomingPackets;

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
		this.socket.setSendBufferSize(UDPNetworkInterface.bufferSize);
		this.socket.setReceiveBufferSize(UDPNetworkInterface.bufferSize);

		this.outgoingPackets = new LinkedList<NodePacketPair>();
		this.incomingPackets = new LinkedList<NodePacketPair>();

		this.listener = new Thread(new Listener(this.socket, this.incomingPackets));
		this.listener.setDaemon(true);
		this.listener.start();

		this.lock = new Object();
	}

	public UDPNetworkInterface(int port) throws SocketException
	{
		this.socket = new DatagramSocket(port);
		this.socket.setSendBufferSize(UDPNetworkInterface.bufferSize);
		this.socket.setReceiveBufferSize(UDPNetworkInterface.bufferSize);

		this.outgoingPackets = new LinkedList<NodePacketPair>();
		this.incomingPackets = new LinkedList<NodePacketPair>();

		this.listener = new Thread(new Listener(this.socket, this.incomingPackets));
		this.listener.setDaemon(true);
		this.listener.start();

		this.lock = new Object();
	}

	@Override
	public boolean sendPacket(NetworkNodeIdentifier node, Packet packet)
	{
		boolean result;
		synchronized (this.lock)
		{
			if (this.socket == null)
				return false;

			result = this.internalSendPacket(node, packet);
		}
		return result;
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
			ListIterator<NodePacketPair> packetIterator = this.outgoingPackets.listIterator();
			while (packetIterator.hasNext())
			{
				NodePacketPair pair = packetIterator.next();
				totalSuccess &= this.internalSendPacket(pair.node, pair.packet);
			}

			this.outgoingPackets.clear();
		}
		return totalSuccess;
	}

	@Override
	public List<NodePacketPair> getIncomingPackets()
	{
		synchronized (this.lock)
		{
			if (this.socket == null)
				return null;
		}
		return this.incomingPackets;
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
		}
	}

	@Override
	public NetworkNodeIdentifier getLocalAddress()
	{
		NetworkNodeIdentifier local = null;

		try
		{
			local = new NetworkNodeIdentifier("127.0.0.1", this.socket.getLocalPort());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return local;
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
		}
	}

	private boolean internalSendPacket(NetworkNodeIdentifier node, Packet packet)
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
			int decompressedLength = bytes.length;
			objectStream.close();

			Deflater deflater = new Deflater(Deflater.BEST_SPEED);
			deflater.setInput(bytes, 0, decompressedLength);
			deflater.finish();
			int compressedLength = deflater.deflate(bytes);
			deflater.end();

			ByteArrayOutputStream finalByteStream = new ByteArrayOutputStream();
			DataOutputStream finalDataStream = new DataOutputStream(finalByteStream);

			finalDataStream.writeInt(compressedLength);
			finalDataStream.writeInt(decompressedLength);
			finalDataStream.write(bytes, 0, compressedLength);
			finalDataStream.flush();

			bytes = finalByteStream.toByteArray();
			finalDataStream.close();
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
		private List<NodePacketPair> incomingPackets;

		public Listener(DatagramSocket socket, List<NodePacketPair> incomingPackets)
		{
			this.socket = socket;
			this.incomingPackets = incomingPackets;
		}

		@Override
		public void run()
		{
			while (!Thread.interrupted())
			{
				// Construct datagram to fill
				byte[] packetBytes = new byte[16 * 1024]; // Max packet size is 16kB
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

				// Add the packet to the incoming queue
				synchronized (this.incomingPackets)
				{
					this.incomingPackets.add(new NodePacketPair(node, packet));
				}
			}
		}

		private Object deserializeObject(byte[] bytes)
		{
			Object object = null;

			try
			{
				DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(bytes));

				int compressedLength = dataStream.readInt();
				int decompressedLength = dataStream.readInt();
				byte[] compressedBytes = new byte[compressedLength];
				dataStream.read(compressedBytes);
				dataStream.close();

				Inflater inflater = new Inflater();
				inflater.setInput(compressedBytes, 0, compressedLength);
				byte[] decompressedBytes = new byte[decompressedLength];
				inflater.inflate(decompressedBytes);
				inflater.end();

				ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(decompressedBytes));

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
