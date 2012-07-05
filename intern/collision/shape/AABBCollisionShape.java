/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.shape;

import math.Matrix;
import math.Ray;
import math.Vector;
import collision.core.CollisionPoint;
import collision.core.CollisionShape;

public class AABBCollisionShape implements CollisionShape
{
	private static final long serialVersionUID = 6776087204294921398L;

	public final Vector aabbMin, aabbMax;

	public AABBCollisionShape(Vector aabbMin, Vector aabbMax)
	{
		this.aabbMin = aabbMin;
		this.aabbMax = aabbMax;
	}

	@Override
	public void getAABB(final Matrix frame, Vector aabbMin, Vector aabbMax)
	{
		Matrix frameNoRotation = frame.multiply(frame.rotationPart().inverted());
		aabbMin.set(frameNoRotation.multiply(this.aabbMin));
		aabbMax.set(frameNoRotation.multiply(this.aabbMax));
	}

	@Override
	public float castRay(final Matrix frame, final Ray ray, CollisionPoint hitPoint)
	{
		Matrix frameNoRotation = frame.multiply(frame.rotationPart().inverted());
		Matrix frameNoRotationInverse = frameNoRotation.inverted();

		System.out.println(frameNoRotation);
		System.out.println(frameNoRotationInverse);

		Ray r = frameNoRotationInverse.multiply(ray);

		System.out.println(r);

		// Negative x face
		if (r.direction.x > 0.0f)
		{
			float t = (this.aabbMin.x - r.origin.x) / r.direction.x;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.y >= this.aabbMin.y && point.y <= this.aabbMax.y)
					if (point.z >= this.aabbMin.z && point.z <= this.aabbMax.z)
					{
						hitPoint.localPointA = new Vector(point);
						hitPoint.localPointB = new Vector(point);
						hitPoint.worldPointA = frameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = frameNoRotation.as3x3().multiply(new Vector(-1.0f, 0.0f, 0.0f));
						return frameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
					}
		}

		// Negative y face
		if (r.direction.y > 0.0f)
		{
			float t = (this.aabbMin.y - r.origin.y) / r.direction.y;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.aabbMin.x && point.x <= this.aabbMax.x)
					if (point.z >= this.aabbMin.z && point.z <= this.aabbMax.z)
					{
						hitPoint.localPointA = new Vector(point);
						hitPoint.localPointB = new Vector(point);
						hitPoint.worldPointA = frameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = frameNoRotation.as3x3().multiply(new Vector(0.0f, -1.0f, 0.0f));
						return frameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
					}
		}

		// Negative z face
		if (r.direction.z > 0.0f)
		{
			float t = (this.aabbMin.z - r.origin.z) / r.direction.z;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.aabbMin.x && point.x <= this.aabbMax.x)
					if (point.y >= this.aabbMin.y && point.y <= this.aabbMax.y)
					{
						hitPoint.localPointA = new Vector(point);
						hitPoint.localPointB = new Vector(point);
						hitPoint.worldPointA = frameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = frameNoRotation.as3x3().multiply(new Vector(0.0f, 0.0f, -1.0f));
						return frameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
					}
		}

		// Positive x face
		if (r.direction.x < 0.0f)
		{
			float t = (this.aabbMax.x - r.origin.x) / r.direction.x;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.y >= this.aabbMin.y && point.y <= this.aabbMax.y)
					if (point.z >= this.aabbMin.z && point.z <= this.aabbMax.z)
					{
						hitPoint.localPointA = new Vector(point);
						hitPoint.localPointB = new Vector(point);
						hitPoint.worldPointA = frameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = frameNoRotation.as3x3().multiply(new Vector(1.0f, 0.0f, 0.0f));
						return frameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
					}
		}

		// Positive y face
		if (r.direction.y < 0.0f)
		{
			float t = (this.aabbMax.y - r.origin.y) / r.direction.y;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.aabbMin.x && point.x <= this.aabbMax.x)
					if (point.z >= this.aabbMin.z && point.z <= this.aabbMax.z)
					{
						hitPoint.localPointA = new Vector(point);
						hitPoint.localPointB = new Vector(point);
						hitPoint.worldPointA = frameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = frameNoRotation.as3x3().multiply(new Vector(0.0f, 1.0f, 0.0f));
						return frameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
					}
		}

		// Positive z face
		if (r.direction.z < 0.0f)
		{
			float t = (this.aabbMax.z - r.origin.z) / r.direction.z;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.aabbMin.x && point.x <= this.aabbMax.x)
					if (point.y >= this.aabbMin.y && point.y <= this.aabbMax.y)
					{
						hitPoint.localPointA = new Vector(point);
						hitPoint.localPointB = new Vector(point);
						hitPoint.worldPointA = frameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = frameNoRotation.as3x3().multiply(new Vector(0.0f, 0.0f, 1.0f));
						return frameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
					}
		}

		hitPoint.localPointA = new Vector();
		hitPoint.localPointB = new Vector();
		hitPoint.worldPointA = new Vector();
		hitPoint.worldPointB = new Vector();
		hitPoint.worldNormalB = new Vector(0.0f, 0.0f, 0.0f);
		return Float.POSITIVE_INFINITY;
	}

	@Override
	public String getTypeName()
	{
		return "Axis-Aligned Bounding Box";
	}
}
