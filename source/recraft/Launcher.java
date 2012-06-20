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
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;

import math.Matrix;
import math.Ray;
import math.Vector;

import recraft.core.*;
import recraft.core.Configurator.ConfiguratorCreatable;
import recraft.core.Configurator.ConfiguratorSelect;
import recraft.core.NetworkInterface;
import recraft.core.NetworkInterface.NodePacketPair;
import recraft.networkinterface.UDPNetworkInterface;
import recraft.networknode.MinecraftServer;
import recraft.packet.InputPacket;
import recraft.packet.TestPacket;
import recraft.polyhedron.AABB;

public class Launcher
{

	public static void main(String[] args) throws Exception
	{
		//*
		// TODO Make log class, start on world/population and the control thereof (start with input device)
		AABB a = new AABB(new Vector(-5, -5, -5), new Vector(5, 5, 5));
		//a.scale(1.0f, 0.2f, 0.2f);
		a.translate(15, 15, 15);

		AABB b = new AABB(new Vector(-1, -2, -1), new Vector(1, 2, 1));

		System.out.println(a.intersectPolyhedron(new Ray(new Vector(0, 0, 15), new Vector(20f, 8.0f, 0)), b));
		/*/
		MinecraftServer server = new MinecraftServer();
		Thread serverThread = new Thread(server);
		serverThread.start();

		ConfiguratorCreatable interfaceCreator = (ConfiguratorCreatable)((ConfiguratorSelect)Configurator.get("Options.Network.Network Interface")).getValue();
		NetworkInterface clientInterface = (NetworkInterface)interfaceCreator.create(null);

		Packet packet = new InputPacket(1, new Input());
		clientInterface.enqueuePacket(new NetworkNodeIdentifier("127.0.0.1", 25565), packet);
		packet = new TestPacket(82);
		clientInterface.enqueuePacket(new NetworkNodeIdentifier("127.0.0.1", 25565), packet);

		clientInterface.sendPacketQueue();

		Thread.sleep(1000);

		clientInterface.close();
		server.stop();
		serverThread.join();
		//*/
	}
}
