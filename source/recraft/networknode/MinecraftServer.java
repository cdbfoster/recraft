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

package recraft.networknode;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import recraft.core.Client;
import recraft.core.ClientMap;
import recraft.core.DeltaState;
import recraft.core.NetworkInterface;
import recraft.core.NetworkInterface.NodePacketPair;
import recraft.core.NetworkNode;
import recraft.core.NetworkNodeIdentifier;
import recraft.core.Stateable;
import recraft.core.Timer;
import recraft.packet.PingPacket;
import recraft.packet.PlayerJoinRequestPacket;

public class MinecraftServer extends NetworkNode
{
	protected static final int discardStateAge = 1000; // 1s
	protected static final int clientLatencyTestInterval = 5000; // 5s
	protected static final int clientStagingLatencyTestInterval = 500; // 0.5s

	protected Stateable world;

	protected int tick;

	protected Object lock;
	protected boolean stopped;

	protected NetworkNodeIdentifier localAddress;

	protected LinkedList<DeltaState> reverseWorldStates;
	protected List<NodePacketPair> clientInputPackets;

	protected ClientMap<MinecraftServerClient> clients;
	protected ClientMap<MinecraftServerClient> clientStaging;

	protected List<NodePacketPair> filteredPackets;
	protected List<NodePacketPair> requestQueue;

	public MinecraftServer(NetworkInterface networkInterface, Stateable world)
	{
		this.networkInterface = networkInterface;
		this.world = world;

		this.tick = 0;

		this.lock = new Object();
		this.stopped = false;

		this.localAddress = this.networkInterface.getLocalAddress();

		this.reverseWorldStates = new LinkedList<DeltaState>();
		this.clientInputPackets = new LinkedList<NodePacketPair>();

		this.clients = new ClientMap<MinecraftServerClient>();
		this.clientStaging = new ClientMap<MinecraftServerClient>();

		this.filteredPackets = new LinkedList<NodePacketPair>();
		this.requestQueue = new LinkedList<NodePacketPair>();
	}

	@Override
	public void run()
	{
		// Initialize (Use a "dummy client" to cause the server to generate the terrain around the spawn area)
			// Request a random chunk
			// If chunk is a suitable spawn location, request the chunks around it
			// Otherwise repeat

		long oldTime = Timer.getTimeInMilliseconds();
		while (true)
		{
			synchronized (this.lock)
			{
				if (this.stopped)
					break;
			}

			long currentTime = Timer.getTimeInMilliseconds();
			int elapsedTime = (int)(currentTime - oldTime);
			int elapsedTicks = Timer.elapsedTicks(elapsedTime);

			if (elapsedTicks > 0)
				oldTime = currentTime - Timer.millisecondRemainder(elapsedTime);

			// Possibly cap elapsedTicks here?
			if (elapsedTicks > 1)
				System.out.println(String.format("Server is losing time! %d ticks to do!", elapsedTicks));

			for (int i = 0; i < elapsedTicks; i++)
			{
				if ((this.tick % Timer.getTicksPerSecond()) == 0)
				{
					//System.out.println(this.tick / Timer.getTicksPerSecond());
					Collection<MinecraftServerClient> clientList = this.clientStaging.getClientList();
					Iterator<MinecraftServerClient> clientIterator = clientList.iterator();
					while (clientIterator.hasNext())
					{
						MinecraftServerClient client = clientIterator.next();
						System.out.println(client.packetLatency);
					}

				}

				this.sendClientLatencyTests();

				List<NodePacketPair> receivedPackets = this.receivePackets();

				ListIterator<NodePacketPair> packetIterator = receivedPackets.listIterator();
				while (packetIterator.hasNext())
				{
					NodePacketPair pair = packetIterator.next();

					this.filterClientLatencyTestResponse(pair);
					this.filterJoin(pair);
					this.filterLeave(pair);
					this.filterInput(pair);
					this.filterStateRequest(pair);
				}

				// Actuation:
				// Handle requests

				// Handle inputs and world updates

				this.tick++;

			}
		}
	}

	@Override
	public void stop()
	{
		synchronized (this.lock)
		{
			this.stopped = true;
		}
	}

	protected void sendClientLatencyTests()
	{
		Collection<MinecraftServerClient> clientList = this.clients.getClientList();

		Iterator<MinecraftServerClient> clientIterator = clientList.iterator();
		while (clientIterator.hasNext())
		{
			MinecraftServerClient client = clientIterator.next();
			client.sendPacketLatencyTest(MinecraftServer.clientLatencyTestInterval);
		}

		Collection<MinecraftServerClient> clientStagingList = this.clientStaging.getClientList();

		Iterator<MinecraftServerClient> clientStagingIterator = clientStagingList.iterator();
		while (clientStagingIterator.hasNext())
		{
			MinecraftServerClient client = clientStagingIterator.next();
			client.sendPacketLatencyTest(MinecraftServer.clientStagingLatencyTestInterval);
		}
	}

	protected List<NodePacketPair> receivePackets()
	{
		List<NodePacketPair> incomingPackets = this.networkInterface.getIncomingPackets();
		List<NodePacketPair> receivedPackets = new LinkedList<NodePacketPair>();

		synchronized (incomingPackets)
		{
			long currentTime = Timer.getTimeInMilliseconds();
			ListIterator<NodePacketPair> packetIterator = incomingPackets.listIterator();
			while (packetIterator.hasNext())
			{
				NodePacketPair pair = packetIterator.next();

				if (Math.abs(currentTime - pair.time) < MinecraftServer.discardStateAge)
					receivedPackets.add(pair);
			}
		}

		this.networkInterface.clearIncomingPackets();

		return receivedPackets;
	}

	protected void filterClientLatencyTestResponse(NodePacketPair pair)
	{
		if (!(pair.packet instanceof PingPacket))
			return;

		MinecraftServerClient client = this.clients.get(pair.node);

		if (client != null)
			client.calculatePacketLatency(pair);
		else
		{
			client = this.clientStaging.get(pair.node);

			if (client != null)
				client.calculatePacketLatency(pair);
		}
	}

	protected void filterJoin(NodePacketPair pair)
	{
		if (!(pair.packet instanceof PlayerJoinRequestPacket))
			return;

		if (this.clientStaging.get(pair.node) != null)
			return;

		System.out.println(pair.packet);
		this.clientStaging.put(pair.node, new MinecraftServerClient(this, pair.node, this.tick));
	}

	protected void filterLeave(NodePacketPair pair)
	{

	}

	protected void filterInput(NodePacketPair pair)
	{

	}

	protected void filterStateRequest(NodePacketPair pair)
	{

	}

	protected static class MinecraftServerClient extends Client
	{
		protected MinecraftServer server;
		protected NetworkNodeIdentifier clientIdentifier;

		public int loginTick;
		public int lastActivityTick;

		public boolean accepted;

		public int requestsHandledThisTick;

		public int packetLatency;
		protected long lastLatencyTest;
		protected boolean sentLatencyTest;

		public MinecraftServerClient(MinecraftServer server, NetworkNodeIdentifier clientIdentifier, int loginTick)
		{
			this.server = server;
			this.clientIdentifier = clientIdentifier;

			this.loginTick = loginTick;
			this.lastActivityTick = this.loginTick;

			this.accepted = false;

			this.requestsHandledThisTick = 0;

			this.packetLatency = 0;
			this.lastLatencyTest = 0;
			this.sentLatencyTest = false;
		}

		public void sendPacketLatencyTest(int testInterval)
		{
			long time = Timer.getTimeInMilliseconds();
			if (Math.abs(time - this.lastLatencyTest) > testInterval)
			{
				if (this.sentLatencyTest)
					// Ping was lost en route or latency was greater than latencyTestInterval
					this.sentLatencyTest = false;

				this.lastLatencyTest = time;
				this.server.networkInterface.sendPacket(this.clientIdentifier, new PingPacket());
				this.sentLatencyTest = true;
			}
		}

		/** Called when the server picks up a ping response from a client */
		public void calculatePacketLatency(NodePacketPair receivedPing)
		{
			if (this.sentLatencyTest)
				this.sentLatencyTest = false;
				this.packetLatency = (int)(Math.abs(receivedPing.time - this.lastLatencyTest) / 2);
		}
	}
}
