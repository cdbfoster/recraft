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

public class AABBCollisionShape implements ConvexPolyhedronCollisionShape
{
	private static final long serialVersionUID = 6776087204294921398L;

	public final Vector aabbMin, aabbMax;

	private Matrix cachedFrame, cachedFrameNoRotation, cachedFrameNoRotationInverse;

	public AABBCollisionShape(Vector aabbMin, Vector aabbMax)
	{
		this.aabbMin = aabbMin;
		this.aabbMax = aabbMax;

		this.cachedFrame = new Matrix();
		this.cachedFrameNoRotation = new Matrix();
		this.cachedFrameNoRotationInverse = new Matrix();
	}

	@Override
	public void getAABB(final Matrix frame, Vector aabbMin, Vector aabbMax)
	{
		this.cacheFrameMatrix(frame);

		aabbMin.set(this.cachedFrameNoRotation.multiply(this.aabbMin));
		aabbMax.set(this.cachedFrameNoRotation.multiply(this.aabbMax));
	}

	@Override
	public float castRay(final Matrix frame, final Ray ray, CollisionPoint hitPoint)
	{
		this.cacheFrameMatrix(frame);

		Ray r = this.cachedFrameNoRotationInverse.multiply(ray);

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
						hitPoint.worldPointA = this.cachedFrameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = this.cachedFrameNoRotation.as3x3().multiply(new Vector(-1.0f, 0.0f, 0.0f));
						return this.cachedFrameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
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
						hitPoint.worldPointA = this.cachedFrameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = this.cachedFrameNoRotation.as3x3().multiply(new Vector(0.0f, -1.0f, 0.0f));
						return this.cachedFrameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
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
						hitPoint.worldPointA = this.cachedFrameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = this.cachedFrameNoRotation.as3x3().multiply(new Vector(0.0f, 0.0f, -1.0f));
						return this.cachedFrameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
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
						hitPoint.worldPointA = this.cachedFrameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = this.cachedFrameNoRotation.as3x3().multiply(new Vector(1.0f, 0.0f, 0.0f));
						return this.cachedFrameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
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
						hitPoint.worldPointA = this.cachedFrameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = this.cachedFrameNoRotation.as3x3().multiply(new Vector(0.0f, 1.0f, 0.0f));
						return this.cachedFrameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
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
						hitPoint.worldPointA = this.cachedFrameNoRotation.multiply(point);
						hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
						hitPoint.worldNormalB = this.cachedFrameNoRotation.as3x3().multiply(new Vector(0.0f, 0.0f, 1.0f));
						return this.cachedFrameNoRotation.as3x3().multiply(point.subtract(r.origin)).length();
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
	public float getGreatestVertexAlongAxis(Matrix frame, Vector axis)
	{
		this.cacheFrameMatrix(frame);

		Vector transformedAxis = this.cachedFrameNoRotationInverse.as3x3().multiply(axis);

		float[] projections = new float[] {
			transformedAxis.dot(this.aabbMax),
			transformedAxis.dot(new Vector(this.aabbMax.x, this.aabbMax.y, this.aabbMin.z)),
			transformedAxis.dot(new Vector(this.aabbMax.x, this.aabbMin.y, this.aabbMax.z)),
			transformedAxis.dot(new Vector(this.aabbMax.x, this.aabbMin.y, this.aabbMin.z)),
			transformedAxis.dot(new Vector(this.aabbMin.x, this.aabbMax.y, this.aabbMax.z)),
			transformedAxis.dot(new Vector(this.aabbMin.x, this.aabbMax.y, this.aabbMin.z)),
			transformedAxis.dot(new Vector(this.aabbMin.x, this.aabbMin.y, this.aabbMax.z)),
			transformedAxis.dot(this.aabbMin)
		};

		float greatest = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < projections.length; i++)
			if (projections[i] > greatest)
				greatest = projections[i];

		return this.cachedFrameNoRotation.as3x3().multiply(transformedAxis.multiply(greatest)).length();
	}

	@Override
	public int getEdgeCount()
	{
		return 12;
	}

	@Override
	public Vector getEdge(Matrix frame, int index)
	{
		if (index >= 0 && index <=3)
			return new Vector(1.0f, 0.0f, 0.0f);
		else if (index >= 4 && index <= 7)
			return new Vector(0.0f, 1.0f, 0.0f);
		else if (index >= 8 && index <=11)
			return new Vector(0.0f, 0.0f, 1.0f);

		return null;
	}

	@Override
	public int getFaceCount()
	{
		return 6;
	}

	@Override
	public Vector getFaceNormal(Matrix frame, int index)
	{
		switch (index)
		{
		case 0:
			return new Vector(1.0f, 0.0f, 0.0f);
		case 1:
			return new Vector(0.0f, 1.0f, 0.0f);
		case 2:
			return new Vector(0.0f, 0.0f, 1.0f);
		case 3:
			return new Vector(-1.0f, 0.0f, 0.0f);
		case 4:
			return new Vector(0.0f, -1.0f, 0.0f);
		case 5:
			return new Vector(0.0f, 0.0f, -1.0f);
		}
		return null;
	}

	@Override
	public String getTypeName()
	{
		return "Axis-Aligned Bounding Box";
	}

	private void cacheFrameMatrix(Matrix frame)
	{
		if (this.cachedFrame.equals(frame))
			return;

		this.cachedFrame = new Matrix(frame);
		this.cachedFrameNoRotation = frame.multiply(frame.rotationPart().inverted());
		this.cachedFrameNoRotationInverse = this.cachedFrameNoRotation.inverted();
	}
}
