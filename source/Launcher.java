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
		ServerNetworkHandler server = new ServerNetworkHandler(21565);
		Thread serverThread = new Thread(server);

		serverThread.start();

		/*
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		//*/

		Socket clientSocket = null;
		try
		{
			clientSocket = new Socket("127.0.0.1", 21565);
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		OutgoingNetworkHandler out = new OutgoingNetworkHandler(clientSocket);
		IncomingNetworkHandler in = new IncomingNetworkHandler(clientSocket);

		System.out.println("Here!");

		server.enqueuePacket(new TestPacket(1, 2, 3));
		server.sendPackets();

		//*
		try
		{
			Thread.sleep(1);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		//*/

		synchronized (in.incomingQueue)
		{
			ListIterator iterator = in.incomingQueue.listIterator();
			while (iterator.hasNext())
			{
				Packet packet = (Packet)iterator.next();
				System.out.println(packet.open());
			}
		}

		in.close();
		out.close();

		//*
		try
		{
			clientSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//*/

		server.close();

		System.out.println("Hi");

	}

}
