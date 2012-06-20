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
import java.util.ListIterator;

import recraft.core.Configurator;
import recraft.core.Configurator.*;
import recraft.core.Creatable;
import recraft.core.NetworkInterface;
import recraft.core.NetworkInterface.NodePacketPair;
import recraft.core.NetworkNode;

public class MinecraftServer extends NetworkNode implements Creatable
{
	private Object lock;
	private boolean stopped;

	public static MinecraftServer create()
	{
		return new MinecraftServer();
	}

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

	public MinecraftServer(NetworkInterface networkInterface)
	{
		this.networkInterface = networkInterface;
		this.lock = new Object();
		this.stopped = false;
	}

	@Override
	public void run()
	{
		while (true)
		{
			synchronized (this.lock)
			{
				if (this.stopped)
					break;
			}

			// Get the packets we've received
			LinkedList<NodePacketPair> incomingPackets = this.networkInterface.getIncomingPackets();

			if (incomingPackets == null)
				continue;

			// Try to read them
			synchronized (incomingPackets)
			{
				ListIterator<NodePacketPair> packetIterator = incomingPackets.listIterator();
				while (packetIterator.hasNext())
				{
					NodePacketPair pair = (NodePacketPair)packetIterator.next();

					// Simply display info about received packets
					System.out.println(String.format("Packet received from %s", pair.node));
					System.out.println(pair.packet);
					System.out.println();
				}
			}

			// Clear the queue in preparation for the next cycle
			this.networkInterface.clearIncomingPackets();
		}
	}

	@Override
	public void stop()
	{
		synchronized (this.lock)
		{
			this.networkInterface.close();
			this.stopped = true;
		}
	}
}
