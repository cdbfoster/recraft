/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Math Library.                                 *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package math;

import java.io.Serializable;

public class Ray implements Serializable
{
	public Vector origin;
	public Vector direction;
	float minTime, maxTime;

	public Ray()
	{
		this.origin = new Vector(0.0f, 0.0f, 0.0f);
		this.direction = new Vector(0.0f, 0.0f, 1.0f);
		this.minTime = 0.0f; this.maxTime = Float.POSITIVE_INFINITY;
	}

	public Ray(Vector origin, Vector direction)
	{
		this.origin = origin;
		this.direction = direction.normalized();
		this.minTime = 0.0f; this.maxTime = Float.POSITIVE_INFINITY;
	}

	public Ray(Vector origin, Vector direction, float minTime, float maxTime)
	{
		this.origin = origin;
		this.direction = direction.normalized();
		this.minTime = minTime;
		this.maxTime = maxTime;
	}

	public Vector at(float time)
	{
		return this.origin.add(this.direction.multiply(time));
	}

	/** Returns the time at which this ray intersects b.  Returns null if there is no intersection. */
	public Float Intersect(Ray b)
	{
		// Just setup some aliases
		Vector ao = this.origin;
		Vector ad = this.direction;
		Vector bo = b.origin;
		Vector bd = b.direction;

		Vector adXbd = ad.cross(bd);

		if (adXbd.isZero())
			return null; // Rays are parallel.  There is no intersection.

		Vector doXbd = bo.subtract(ao).cross(bd);
		if (Math.abs(adXbd.normalized().dot(doXbd.normalized()) - 1.0f) > Constants.FLT_EPSILON) // if (adXbd.dot(doXbd) != 1.0f)
			return null; // Rays are skew. There is no intersection.

		return new Float(Math.sqrt(doXbd.lengthSquared() / adXbd.lengthSquared()));
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

		Ray c = (Ray)b;

		if (!this.origin.equals(c.origin))
			return false;
		if (!this.direction.equals(c.direction))
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 17 * this.origin.hashCode() + this.direction.hashCode();
		hash = 17 * hash + new Float(this.minTime).hashCode();
		hash = 17 * hash + new Float(this.maxTime).hashCode();
		return hash;
	}

	@Override
	public Ray clone()
	{
		return new Ray(this.origin.clone(), this.direction.clone(), this.minTime, this.maxTime);
	}

	@Override
	public String toString()
	{
		return String.format("Origin: %s Direction: %s\nTime Range: [%f, %f]", this.origin.toString(), this.direction.toString(), this.minTime, this.maxTime);
	}
}
