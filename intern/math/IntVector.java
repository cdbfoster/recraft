/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Math Library.                                 *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package math;

import java.io.Serializable;

public class IntVector implements Serializable
{
	public int x;
	public int y;
	public int z;

	public IntVector(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public IntVector(IntVector b)
	{
		this.x = b.x;
		this.y = b.y;
		this.z = b.z;
	}

	public IntVector()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public IntVector add(IntVector b)
	{
		return new IntVector(this.x + b.x, this.y + b.y, this.z + b.z);
	}

	public IntVector add(int b)
	{
		return new IntVector(this.x + b, this.y + b, this.z + b);
	}

	public IntVector subtract(IntVector b)
	{
		return new IntVector(this.x - b.x, this.y - b.y, this.z - b.z);
	}

	public IntVector subtract(int b)
	{
		return new IntVector(this.x - b, this.y - b, this.z - b);
	}

	public IntVector multiply(IntVector b)
	{
		return new IntVector(this.x * b.x, this.y * b.y, this.z * b.z);
	}

	public IntVector multiply(int b)
	{
		return new IntVector(this.x * b, this.y * b, this.z * b);
	}

	public IntVector divide(IntVector b)
	{
		return new IntVector(this.x / b.x, this.y / b.y, this.z / b.z);
	}

	public IntVector divide(int b)
	{
		return new IntVector(this.x / b, this.y / b, this.z / b);
	}

	/** Performs modulus division (%) between each element of the two vectors. */
	public IntVector mod(IntVector b)
	{
		return new IntVector(this.x % b.x, this.y % b.y, this.z % b.z);
	}

	/** Performs modulus division (%) of each element of the vector and b. */
	public IntVector mod(int b)
	{
		return new IntVector(this.x % b, this.y % b, this.z % b);
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

		IntVector c = (IntVector)b;

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
		return String.format("( %d, %d, %d )", this.x, this.y, this.z);
	}
}
