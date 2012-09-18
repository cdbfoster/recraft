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

import recraft.core.Packet;
import recraft.core.PlayerJoinRequest;

public class PlayerJoinRequestPacket extends Packet
{
	private static final long serialVersionUID = 9082131761673250765L;

	public static int id = 1;

	private PlayerJoinRequest request;

	public PlayerJoinRequestPacket(int tick, String playerName)
	{
		super(tick);
		this.request = new PlayerJoinRequest(playerName);
	}

	public PlayerJoinRequestPacket(int tick, PlayerJoinRequest joinRequest)
	{
		super(tick);
		this.request = joinRequest;
	}

	@Override
	public int getID() { return id; }

	@Override
	public Object open()
	{
		return this.request;
	}

	@Override
	public String toString()
	{
		return String.format("%s\nPlayer: %s", Packet.formatString(id, "PlayerJoinRequest", this.tick), this.request.playerName);
	}
}
