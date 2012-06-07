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

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ListIterator;

import recraft.client.network.ClientNetworkHandler;
import recraft.core.Packet;
import recraft.network.IncomingNetworkHandler;
import recraft.network.OutgoingNetworkHandler;
import recraft.packet.TestPacket;
import recraft.server.network.ServerNetworkHandler;
import recraft.server.network.ServerNetworkHandler.ClientPacket;
import recraft.util.IntVector3;

public class Launcher
{
	public static void main(String[] args)
	{
		// Server starts
		ServerNetworkHandler server = new ServerNetworkHandler(25565);
		// Client1 and Client2 connect
		ClientNetworkHandler client1 = new ClientNetworkHandler("127.0.0.1", 25565);
		ClientNetworkHandler client2 = new ClientNetworkHandler("127.0.0.1", 25565);

		// Client1 sends authentication info
		System.out.println(client1.enqueuePacket(new TestPacket(1, 2, 3)));
		System.out.println(client1.sendPackets());

		// Client2 sends authentication info
		System.out.println(client2.enqueuePacket(new TestPacket(2, 3, 4)));
		System.out.println(client2.sendPackets());

		System.out.println();

		int a = 0;
		for (a = 0; a < 100; a++)
		{
			// Server collects all received packets
			server.collectClientPackets();

			// Server inspects newcomer authentication info
			synchronized (server.newcomerPackets)
			{
				ListIterator iter = server.newcomerPackets.listIterator();
				while (iter.hasNext())
				{
					ClientPacket clientPacket = (ClientPacket)iter.next();

					// Check to make sure received packet is an authentication packet
					if (!(clientPacket.getId() == TestPacket.id))
						// Otherwise, ignore it
						continue;

					// Does the info check out?
					IntVector3 info = (IntVector3)clientPacket.open();
					if (info.equals(new IntVector3(1, 2, 3)))
					{
						// Info checks out, graduate the client
						System.out.println(server.graduateClient(clientPacket.owner));
					}
					else
					{
						// Kill client. (Probably too harsh)
						System.out.println("false");
						clientPacket.owner.close();
					}
				}
			}

			server.purgeClients();
			if ((a % 20) == 0)
				System.out.format("%d %d\n", server.getClientCount(), server.getNewcomerCount());

			try
			{
				Thread.sleep(1);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		client1.close();
		client2.close();

		server.purgeClients();

		System.out.format("%d %d\n", server.getClientCount(), server.getNewcomerCount());

		server.close();
	}

}
