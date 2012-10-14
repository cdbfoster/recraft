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
import recraft.core.StateRequest;

public class StateRequestPacket extends Packet
{
	private static final long serialVersionUID = -2853743029750278750L;

	public static int id = 6;

	private StateRequest request;

	public StateRequestPacket(StateRequest request)
	{
		this.request = request;
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
		return Packet.formatString(id, "StateRequest");
	}

}
