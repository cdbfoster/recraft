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

package recraft.core;

import java.util.List;
import java.util.Map;

/** Handles sending and receiving of Packets to and from specified network nodes. */
public interface NetworkInterface extends Creatable
{
	// Create the following methods in implementing classes:
	// public static ImplementingClass create();
	// public static ImplementingClass create(Integer port);

	/** Send a packet to the specified network node. */
	boolean sendPacket(NetworkNodeIdentifier node, Packet packet);

	/** Add a packet to the outgoing packet queue */
	boolean enqueuePacket(NetworkNodeIdentifier node, Packet packet);
	/** Send all packets in and clear the outgoing packet queue */
	boolean sendPacketQueue();

	/** Returns the incoming packet queue in the form of a list of NodePacketPairs.  Synchronize access to the returned list.  Returns null if the interface has been closed. */
	List<NodePacketPair> getIncomingPackets();
	/** Returns the incoming packet queue in the form of a HashMap of network nodes to lists of packets received by each unique node.  Synchronize access to the returned map.  Returns null if the interface has been closed. */
	Map<NetworkNodeIdentifier, List<Packet>> getIncomingPacketsMap();
	/** Clear the incoming packet queue */
	void clearIncomingPackets();

	/** Returns local address and port number the interface is listening on */
	NetworkNodeIdentifier getLocalAddress();

	/** Free all resources used by and prevent further communication through this network interface. */
	void close();

	public static class NodePacketPair
	{
		public final NetworkNodeIdentifier node;
		public final Packet packet;
		public final long time;

		public NodePacketPair(NetworkNodeIdentifier node, Packet packet)
		{
			this.node = node;
			this.packet = packet;
			this.time = Timer.getTimeInMilliseconds();
		}

		public NodePacketPair(NetworkNodeIdentifier node, Packet packet, long time)
		{
			this.node = node;
			this.packet = packet;
			this.time = time;
		}

		@Override
		public String toString()
		{
			return String.format("Packet received from: %s\n%s", this.node, this.packet);
		}
	}
}
