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

package recraft.util;

public class IntVector3
{
	public int x;
	public int y;
	public int z;

	public IntVector3(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public IntVector3()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public IntVector3 add(IntVector3 b)
	{
		return new IntVector3(this.x + b.x, this.y + b.y, this.z + b.z);
	}

	public IntVector3 add(int b)
	{
		return new IntVector3(this.x + b, this.y + b, this.z + b);
	}

	public IntVector3 subtract(IntVector3 b)
	{
		return new IntVector3(this.x - b.x, this.y - b.y, this.z - b.z);
	}

	public IntVector3 subtract(int b)
	{
		return new IntVector3(this.x - b, this.y - b, this.z - b);
	}

	public IntVector3 multiply(IntVector3 b)
	{
		return new IntVector3(this.x * b.x, this.y * b.y, this.z * b.z);
	}

	public IntVector3 multiply(int b)
	{
		return new IntVector3(this.x * b, this.y * b, this.z * b);
	}

	public IntVector3 divide(IntVector3 b)
	{
		return new IntVector3(this.x / b.x, this.y / b.y, this.z / b.z);
	}

	public IntVector3 divide(int b)
	{
		return new IntVector3(this.x / b, this.y / b, this.z / b);
	}

	@Override
	public boolean equals(Object b)
	{
		if (this == b)
			return true;
		if (b == null)
			return false;
		if (this.getClass() != b.getClass())
			return false;

		IntVector3 c = (IntVector3)b;

		if (this.x != c.x)
			return false;
		if (this.y != c.y)
			return false;
		if (this.z != c.z)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 15; // The starting value is arbitrary.

		// 17 is an odd prime.
		hash = 17 * hash + this.x;
		hash = 17 * hash + this.y;
		hash = 17 * hash + this.z;

		return hash;
	}

	@Override
	public String toString()
	{
		return (new StringBuilder().append("( ").append(this.x).append(", ").append(this.y).append(", ").append(this.z).append(" )").toString());
	}
}
