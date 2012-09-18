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

package recraft.packet;

import recraft.core.NetworkNodeIdentifier;
import recraft.core.Packet;
import recraft.core.PlayerDisconnect;

public class PlayerDisconnectPacket extends Packet
{
	private static final long serialVersionUID = -1277203851441881503L;

	public static int id = 4;

	private PlayerDisconnect playerDisconnect;

	public PlayerDisconnectPacket(int tick, String playerName, NetworkNodeIdentifier clientNode)
	{
		super(tick);
		this.playerDisconnect = new PlayerDisconnect(playerName, clientNode);
	}

	public PlayerDisconnectPacket(int tick, PlayerDisconnect disconnect)
	{
		super(tick);
		this.playerDisconnect = disconnect;
	}

	@Override
	public int getID() { return id; }

	@Override
	public Object open()
	{
		return this.playerDisconnect;
	}

	@Override
	public String toString()
	{
		return String.format("%s\nPlayer Disconnected: %s", Packet.formatString(id, "PlayerDisconnect", this.tick), this.playerDisconnect.playerName);
	}

}
