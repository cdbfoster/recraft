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

import recraft.core.NetworkInterface;
import recraft.networkinterface.UDPNetworkInterface;
import recraft.networknode.MinecraftServer;
import recraft.stateable.world.MinecraftWorld;



public class Launcher
{

	public static void main(String[] args) throws Exception
	{
		//*
		System.out.println("Starting server...");
		MinecraftWorld world = null;
		NetworkInterface serverNetwork = new UDPNetworkInterface(25565);
		MinecraftServer server = new MinecraftServer(serverNetwork, world);

		Thread serverThread = new Thread(server);
		serverThread.start();
		/*/
		System.out.println("Starting client...");
		NetworkInterface clientNetwork = new UDPNetworkInterface();
		MinecraftClient client = new MinecraftClient(clientNetwork);

		Thread clientThread = new Thread(client);
		clientThread.start();

		Thread.sleep(500);

		System.out.println("Attempting to join server...");
		client.join(new NetworkNodeIdentifier("192.168.1.65", 25565));
		//*/

		Thread.sleep(100000);

		//*
		server.stop();
		serverThread.join();
		serverNetwork.close();
		/*/
		client.stop();
		clientThread.join();
		clientNetwork.close();
		//*/
		/*

		ConvexPolyhedronCollisionShape aabb = new AABBCollisionShape(new Vector(-10, -10, -10), new Vector(10, 10, 10));
		CollisionObject a = new CollisionObject(aabb);
		CollisionObject b = new CollisionObject(aabb);

		a.rotate(0.0f, 0.0f, (float)Math.toRadians(45.0d));
		a.translate((float)(10.0f * Math.sqrt(2.0f)), 0, 0);
		//a.rotate((float)Math.toRadians(45.0d), 0.0f, 0.0f);
		//a.translate((float)(10.0f * Math.sqrt(2.0f)), 0, 0);
		//b.rotate(0, 0, (float)Math.toRadians(45.0d));

		//System.out.println(aabb.getGreatestPointAlongAxis(a.getTransform(), new Vector(1.0f, 0.0f, 0.0f), null));
		//*
		CollisionResult result = CollisionDetector.detectCollision(a, b);
		if (result != null)
		{
			System.out.println(result.a);
			System.out.println(result.b);
			System.out.println(result.point.localPointA);
			System.out.println(result.point.localPointB);
			System.out.println(result.point.worldNormalB);
			System.out.println(result.point.worldPointA);
			System.out.println(result.point.worldPointB);
		}
		else
		{
			System.out.println("No collision");
		}
		//*/


		/*          7
		 * 3                    6
		 *             2
		 *
		 *
		 *
		 *          5
		 * 1                    4
		 *             0
		 */
	}
}
