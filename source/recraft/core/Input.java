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

import math.Constants;

public class Input implements Serializable
{
	private static final long serialVersionUID = 5286237990318153917L;

	public static enum Type {MOVE_DIRECTION, LOOK_DIRECTION, ATTACK, USE,
		INVENTORY, JUMP, RUN, CROUCH, DROP, CHAT, ESCAPE, SELECT_ITEM_ABS, SELECT_ITEM_REL}

	private short moveX, moveY;
	private short lookX, lookY;
	private byte selectIndex;
	private short active;

	public Input()
	{
		this.active = 0x0000;
	}

	public void setDirection(Input.Type type, float x, float y)
	{
		float length = (float)Math.sqrt(x * x + y * y);
		x /= length;
		y /= length;

		if (x > 1.0f)
			x = 1.0f;
		else if (x < -1.0f)
			x = -1.0f;

		if (y > 1.0f)
			y = 1.0f;
		else if (y < -1.0f)
			y = -1.0f;

		switch (type)
		{
		case MOVE_DIRECTION:
			this.moveX = (short)(x * 32767);
			this.moveY = (short)(y * 32767);
			break;
		case LOOK_DIRECTION:
			this.lookX = (short)(x * 32767);
			this.lookY = (short)(y * 32767);
		}
	}

	public float getDirectionX(Input.Type type)
	{
		float x = 0.0f;
		float y = 0.0f;

		switch (type)
		{
		case MOVE_DIRECTION:
			x = this.moveX / 32767.0f;
			y = this.moveY / 32767.0f;
			break;
		case LOOK_DIRECTION:
			x = this.lookX / 32767.0f;
			y = this.lookY / 32767.0f;
			break;
		}

		// Normalize for good measure
		float length = (float)Math.sqrt(x * x + y * y);
		return (length > Constants.FLT_EPSILON ? (x / length) : (0.0f));
	}

	public float getDirectionY(Input.Type type)
	{
		float x = 0.0f;
		float y = 0.0f;

		switch (type)
		{
		case MOVE_DIRECTION:
			x = this.moveX / 32767.0f;
			y = this.moveY / 32767.0f;
			break;
		case LOOK_DIRECTION:
			x = this.lookX / 32767.0f;
			y = this.lookY / 32767.0f;
			break;
		}

		// Normalize for good measure
		float length = (float)Math.sqrt(x * x + y * y);
		return (length > Constants.FLT_EPSILON) ? (y / length) : (0.0f);
	}

	public void setValue(Input.Type type, int value)
	{
		switch (type)
		{
		case SELECT_ITEM_ABS: // Set the last seven bits of selectIndex
			this.selectIndex = (byte)((this.selectIndex & 0x80) | (value & 0x7F));
			break;
		case SELECT_ITEM_REL: // Set the first bit of selectIndex
			if (value > 0)
				this.selectIndex |= 0x80;
			else
				this.selectIndex &= 0x7f;
			break;
		}
	}

	public int getValue(Input.Type type)
	{
		switch (type)
		{
		case SELECT_ITEM_ABS: // Return the last seven bits of selectIndex
			return this.selectIndex & 0x7F;
		case SELECT_ITEM_REL: // Use the first bit of selectIndex to determine direction
			return (this.selectIndex & 0x80) != 0 ? 1 : -1;
		}

		return 0;
	}

	public void setActive(short active)
	{
		this.active = active;
	}

	public void setActive(Input.Type type, boolean state)
	{
		if (state == true)
			this.active |= (0x0001 << type.ordinal());
		else
			this.active &= ~(0x0001 << type.ordinal());
	}

	public int getActive()
	{
		return this.active;
	}

	public boolean isActive(Input.Type type)
	{
		return (this.active & (0x0001 << type.ordinal())) != 0;
	}

	@Override
	public String toString()
	{
		String string = String.format("Move: %d, %d\nLook: %d, %d\n", this.moveX, this.moveY, this.lookX, this.lookY);

		for (int i = 0; i < 16; i++)
			if ((this.active & (0x0001 << i)) == 0)
				string += "0 ";
			else
				string += "1 ";

		return string + "\n";
	}
}
