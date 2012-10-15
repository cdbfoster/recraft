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

import java.util.List;
import java.util.ListIterator;

import recraft.core.NetworkInterface;
import recraft.core.NetworkInterface.NodePacketPair;
import recraft.core.NetworkNode;
import recraft.core.NetworkNodeIdentifier;
import recraft.packet.PingPacket;
import recraft.packet.PlayerJoinRequestPacket;

public class MinecraftClient extends NetworkNode
{
	protected Object lock;
	protected boolean stopped;

	public MinecraftClient(NetworkInterface networkInterface)
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

			List<NodePacketPair> incomingPackets = this.networkInterface.getIncomingPackets();

			synchronized (incomingPackets)
			{
				ListIterator<NodePacketPair> iter = incomingPackets.listIterator();
				while (iter.hasNext())
				{
					NodePacketPair pair = iter.next();

					if (pair.packet instanceof PingPacket)
					{
						this.networkInterface.sendPacket(pair.node, new PingPacket());
					}
				}
			}

			this.networkInterface.clearIncomingPackets();
		}

	}

	public void join(NetworkNodeIdentifier server)
	{
		this.networkInterface.sendPacket(server, new PlayerJoinRequestPacket("cdbfoster"));
	}

	@Override
	public void stop()
	{
		synchronized (this.lock)
		{
			this.stopped = true;
		}
	}
}
