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

package recraft.core;

import java.io.Serializable;

public class Input implements Serializable
{
	public static enum Type {MOVE_DIRECTION, LOOK_DIRECTION,
		ATTACK, USE, INVENTORY, JUMP, RUN, CROUCH, DROP, CHAT, ESCAPE}

	public float moveX, moveY;
	public float lookX, lookY;
	private int active;

	public Input()
	{
		this.active = 0x00000000;
	}

	public void setDirection(Input.Type type, float x, float y)
	{
		double length = Math.sqrt(x * x + y * y);
		x /= length;
		y /= length;

		switch (type)
		{
		case MOVE_DIRECTION:
			this.moveX = x;
			this.moveY = y;
			break;
		case LOOK_DIRECTION:
			this.lookX = x;
			this.lookY = y;
		}
	}

	public void setActive(int active)
	{
		this.active = active;
	}

	public void setActive(Input.Type type, boolean state)
	{
		if (state == true)
			this.active |= (0x00000001 << type.ordinal());
		else
			this.active &= ~(0x00000001 << type.ordinal());
	}

	public int getActive()
	{
		return this.active;
	}

	public boolean isActive(Input.Type type)
	{
		return (this.active & (0x00000001 << type.ordinal())) != 0;
	}

	@Override
	public String toString()
	{
		String string = String.format("Move: %f, %f\nLook: %f, %f\n", this.moveX, this.moveY, this.lookX, this.lookY);

		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 16; j++)
				if ((this.active & (0x00000001 << (16 * i + j))) == 0)
					string += "0 ";
				else
					string += "1 ";
			if (i == 0)
				string += "\n";
		}
		return string;
	}
}
