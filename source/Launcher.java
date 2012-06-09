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

package recraft;

import java.io.*;
import java.net.*;
import java.util.*;

import recraft.core.*;
import recraft.core.NetworkInterface;
import recraft.core.NetworkInterface.NodePacketPair;
import recraft.networkinterface.UDPNetworkInterface;
import recraft.packet.TestPacket;
import recraft.util.IntVector3;

public class Launcher
{
	public static void main(String[] args) throws Exception
	{
		NetworkInterface server = new UDPNetworkInterface(25565);
		NetworkInterface client = new UDPNetworkInterface();

		Packet packet = new TestPacket(1, 2, 3);

		client.enqueuePacket(new NetworkNodeIdentifier("127.0.0.1", 25565), packet);

		packet = new TestPacket(2, 6, 3);

		client.enqueuePacket(new NetworkNodeIdentifier("127.0.0.1", 25565), packet);

		client.sendPacketQueue();

		Thread.sleep(1000);

		LinkedList incoming = server.getIncomingPackets();

		System.out.println(incoming.size());

		ListIterator iterator = incoming.listIterator();
		while (iterator.hasNext())
		{
			NodePacketPair pair = (NodePacketPair)iterator.next();
			Packet pack = pair.packet;
			System.out.println((IntVector3)pack.open());
		}

		client.close();

	}
}
