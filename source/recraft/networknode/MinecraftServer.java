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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import recraft.core.Client;
import recraft.core.ClientMap;
import recraft.core.Configurator;
import recraft.core.Configurator.ConfiguratorCreatable;
import recraft.core.Configurator.ConfiguratorIntRange;
import recraft.core.Configurator.ConfiguratorSelect;
import recraft.core.Creatable;
import recraft.core.DeltaState;
import recraft.core.NetworkInterface;
import recraft.core.NetworkInterface.NodePacketPair;
import recraft.core.NetworkNode;
import recraft.core.NetworkNodeIdentifier;
import recraft.core.PlayerJoinRequest;
import recraft.core.PlayerJoinResponse;
import recraft.core.PlayerJoinResponse.ResponseType;
import recraft.core.Timer;
import recraft.packet.InputPacket;
import recraft.packet.PlayerDisconnectPacket;
import recraft.packet.PlayerJoinPacket;
import recraft.packet.PlayerJoinRequestPacket;
import recraft.packet.PlayerJoinResponsePacket;
import recraft.stateable.world.MinecraftWorld;

public class MinecraftServer extends NetworkNode implements Creatable
{
	private MinecraftWorld world;

	private int tick;

	private Object lock;
	private boolean stopped;

	private NetworkNodeIdentifier localAddress;

	public ClientMap<MinecraftServerClient> clients;

	public static MinecraftServer create()
	{
		return new MinecraftServer();
	}

	// Deprecate
	public MinecraftServer()
	{
		super();

		ConfiguratorSelect interfaceSelect = (ConfiguratorSelect)Configurator.get("Options.Network.Network Interface");
		ConfiguratorCreatable interfaceCreator= (ConfiguratorCreatable)interfaceSelect.getValue();

		ConfiguratorIntRange portRange = (ConfiguratorIntRange)Configurator.get("Options.Network.Network Nodes.Server.Bind Port");
		Object[] parameters = {new Integer(portRange.getValue())};

		this.networkInterface = (NetworkInterface)interfaceCreator.create(parameters);
		this.lock = new Object();
		this.stopped = false;
	}

	public MinecraftServer(NetworkInterface networkInterface, MinecraftWorld world)
	{
		this.networkInterface = networkInterface;
		this.world = world;

		this.tick = 0;

		this.lock = new Object();
		this.stopped = false;

		this.localAddress = this.networkInterface.getLocalAddress();

		this.clients = new ClientMap<MinecraftServerClient>();
	}

	@Override
	public void run()
	{
		// Server should keep a list of requested data and in a separate thread, dole them out in a timed manner



		// Initialize (Use a "dummy client" to cause the server to generate the terrain around the spawn area)
			// Request a random chunk
			// If chunk is a suitable spawn location, request the chunks around it
			// Otherwise repeat

		// Begin timed loop
			// 20 ticks per second
			// Check if the server is still running by syncing this.lock and checking this.stopped
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
				oldTime = currentTime;

			// Possibly cap elapsedTicks here?

			for (int i = 0; i < elapsedTicks; i++)
			{
				if ((this.tick % Timer.getTicksPerSecond()) == 0)
					System.out.println(this.tick / Timer.getTicksPerSecond());
		// Get, interpret, and filter packets (Server filters all non player command related packets, Client filters all non delta state related packets)
			// Get the list of packets normally, can be null, and sync the list while reading
			// Clear the networkInterface's incoming list when done

				List<NodePacketPair> filteredPackets = new LinkedList<NodePacketPair>();

				List<NodePacketPair> incomingPackets = this.networkInterface.getIncomingPackets();

				if (incomingPackets != null)
				{
					synchronized (incomingPackets)
					{
						// Check for joins and handle disconnects
						this.handleClientChanges(incomingPackets);

						// Handle state requests

						ListIterator<NodePacketPair> pairIterator = incomingPackets.listIterator();
						while (pairIterator.hasNext())
						{
							NodePacketPair pair = pairIterator.next();
							System.out.println(pair);

							if (pair.packet instanceof InputPacket ||
								pair.packet instanceof PlayerJoinPacket ||
								pair.packet instanceof PlayerDisconnectPacket)
							{
								filteredPackets.add(pair);
							}
						}
					}

					this.networkInterface.clearIncomingPackets();
				}

		// Open DeltaStates
			// Call world.openDeltaState(tick);

				this.world.openDeltaState(this.tick);

		// Update the world with the filtered packets

				this.world.update(filteredPackets);

		// Close DeltaStates
			// Call state = world.closeDeltaState();

				DeltaState worldDeltaState = this.world.closeDeltaState();

		// Modify DeltaStates per client (Using tailor method and a StateRequest) and send



				this.tick++;
			}
		}
	}

	@Override
	public void stop()
	{
		synchronized (this.lock)
		{
			//this.networkInterface.close(); // This should be the responsibility of whoever allocated the interface
			this.stopped = true;
		}
	}

	protected void handleClientChanges(List<NodePacketPair> incomingPackets)
	{
		List<NodePacketPair> additionalPackets = new LinkedList<NodePacketPair>();

		ListIterator<NodePacketPair> pairIterator = incomingPackets.listIterator();
		while (pairIterator.hasNext())
		{
			NodePacketPair pair = pairIterator.next();

			MinecraftServerClient client = this.clients.get(pair.node);

			if (client == null && pair.packet instanceof PlayerJoinRequestPacket)
			{
				PlayerJoinRequest joinRequest = (PlayerJoinRequest)pair.packet.open();
				PlayerJoinResponse joinResponse = this.VerifyClient(joinRequest);

				if (joinResponse.responseType == ResponseType.ACCEPT)
				{
					MinecraftServerClient newClient = new MinecraftServerClient(this, pair.node, this.tick);
					this.clients.put(pair.node, newClient);

					NodePacketPair joinPacketPair = new NodePacketPair(this.localAddress, new PlayerJoinPacket(this.tick, joinRequest.playerName, pair.node));
					additionalPackets.add(joinPacketPair);
				}

				PlayerJoinResponsePacket joinResponsePacket = new PlayerJoinResponsePacket(this.tick, joinResponse);
				this.networkInterface.sendPacket(pair.node, joinResponsePacket);
			}

			if (client != null)
			{
				// set last activity to this tick
			}

			// if client != null and packet is a player disconnect, remove client from map
		}

		// for each client, if last activity was a while ago, remove client from map and send player disconnect packet to additionalPackets

		pairIterator = additionalPackets.listIterator();
		while (pairIterator.hasNext())
			incomingPackets.add(pairIterator.next());
	}

	protected PlayerJoinResponse VerifyClient(PlayerJoinRequest joinRequest)
	{
		return new PlayerJoinResponse(ResponseType.ACCEPT, "Welcome!");
	}

	private static class MinecraftServerClient extends Client
	{
		public MinecraftServer server;

		public NetworkNodeIdentifier nodeIdentifier;

		public int loginTick;
		public int lastActivityTick;

		public MinecraftServerClient(MinecraftServer server, NetworkNodeIdentifier nodeIdentifier, int loginTick)
		{
			this.server = server;
			this.nodeIdentifier = nodeIdentifier;
			this.loginTick = loginTick;
			this.lastActivityTick = this.loginTick;
		}

		public void disconnect()
		{

		}
	}
}
