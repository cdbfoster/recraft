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
import recraft.util.IntVector3;

public class Launcher
{
	public static void main(String[] args)
	{
		ServerNetworkHandler server = new ServerNetworkHandler(25565);
		ClientNetworkHandler client = new ClientNetworkHandler("127.0.0.1", 25565);

		client.enqueuePacket(new TestPacket(1, 2, 3));

		server.close();

		System.out.println(client.sendPackets());

		server.close();

		client.close();

	}

}
