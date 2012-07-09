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

public class BoxCollisionShape extends ConvexPolyhedronCollisionShape
{
	private static final long serialVersionUID = 1557807861501912846L;

	public final Vector minPoint, maxPoint;

	protected Object cacheLock;
	protected Matrix cachedFrame, cachedFrameInverse;

	public BoxCollisionShape(Vector minPoint, Vector maxPoint)
	{
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;

		this.cacheLock = new Object();
		this.cachedFrame = new Matrix();
		this.cachedFrameInverse = new Matrix();
	}

	@Override
	public void getAABB(final Matrix frame, Vector aabbMin, Vector aabbMax)
	{
		Vector[] vertices = new Vector[] {
			new Vector(this.maxPoint),
			new Vector(this.maxPoint.x, this.maxPoint.y, this.minPoint.z),
			new Vector(this.maxPoint.x, this.minPoint.y, this.maxPoint.z),
			new Vector(this.maxPoint.x, this.minPoint.y, this.minPoint.z),
			new Vector(this.minPoint.x, this.maxPoint.y, this.maxPoint.z),
			new Vector(this.minPoint.x, this.maxPoint.y, this.minPoint.z),
			new Vector(this.minPoint.x, this.minPoint.y, this.maxPoint.z),
			new Vector(this.minPoint)
		};

		aabbMin.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		aabbMax.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

		for (int i = 0; i < vertices.length; i++)
		{
			Vector transformedVertex = frame.multiply(vertices[i]);

			if (transformedVertex.x < aabbMin.x)
				aabbMin.x = transformedVertex.x;
			if (transformedVertex.y < aabbMin.y)
				aabbMin.y = transformedVertex.y;
			if (transformedVertex.z < aabbMin.z)
				aabbMin.z = transformedVertex.z;

			if (transformedVertex.x > aabbMax.x)
				aabbMax.x = transformedVertex.x;
			if (transformedVertex.y > aabbMax.y)
				aabbMax.y = transformedVertex.y;
			if (transformedVertex.z > aabbMax.z)
				aabbMax.z = transformedVertex.z;
		}
	}

	@Override
	public float castRay(final Matrix frame, final Ray ray, CollisionPoint hitPoint)
	{
		synchronized (this.cacheLock)
		{
			this.cacheFrameMatrix(frame);

			Ray r = this.cachedFrameInverse.multiply(ray);

			// Negative x face
			if (r.direction.x > 0.0f)
			{
				float t = (this.minPoint.x - r.origin.x) / r.direction.x;
				Vector point = r.at(t);

				if (t >= r.minTime && t <= r.maxTime)
					if (point.y >= this.minPoint.y && point.y <= this.maxPoint.y)
						if (point.z >= this.minPoint.z && point.z <= this.maxPoint.z)
						{
							hitPoint.localPointA = new Vector(point);
							hitPoint.localPointB = new Vector(point);
							hitPoint.worldPointA = this.cachedFrame.multiply(point);
							hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
							hitPoint.worldNormalB = this.cachedFrame.as3x3().multiply(new Vector(-1.0f, 0.0f, 0.0f));
							return this.cachedFrame.as3x3().multiply(point.subtract(r.origin)).length();
						}
			}

			// Negative y face
			if (r.direction.y > 0.0f)
			{
				float t = (this.minPoint.y - r.origin.y) / r.direction.y;
				Vector point = r.at(t);

				if (t >= r.minTime && t <= r.maxTime)
					if (point.x >= this.minPoint.x && point.x <= this.maxPoint.x)
						if (point.z >= this.minPoint.z && point.z <= this.maxPoint.z)
						{
							hitPoint.localPointA = new Vector(point);
							hitPoint.localPointB = new Vector(point);
							hitPoint.worldPointA = this.cachedFrame.multiply(point);
							hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
							hitPoint.worldNormalB = this.cachedFrame.as3x3().multiply(new Vector(0.0f, -1.0f, 0.0f));
							return this.cachedFrame.as3x3().multiply(point.subtract(r.origin)).length();
						}
			}

			// Negative z face
			if (r.direction.z > 0.0f)
			{
				float t = (this.minPoint.z - r.origin.z) / r.direction.z;
				Vector point = r.at(t);

				if (t >= r.minTime && t <= r.maxTime)
					if (point.x >= this.minPoint.x && point.x <= this.maxPoint.x)
						if (point.y >= this.minPoint.y && point.y <= this.maxPoint.y)
						{
							hitPoint.localPointA = new Vector(point);
							hitPoint.localPointB = new Vector(point);
							hitPoint.worldPointA = this.cachedFrame.multiply(point);
							hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
							hitPoint.worldNormalB = this.cachedFrame.as3x3().multiply(new Vector(0.0f, 0.0f, -1.0f));
							return this.cachedFrame.as3x3().multiply(point.subtract(r.origin)).length();
						}
			}

			// Positive x face
			if (r.direction.x < 0.0f)
			{
				float t = (this.maxPoint.x - r.origin.x) / r.direction.x;
				Vector point = r.at(t);

				if (t >= r.minTime && t <= r.maxTime)
					if (point.y >= this.minPoint.y && point.y <= this.maxPoint.y)
						if (point.z >= this.minPoint.z && point.z <= this.maxPoint.z)
						{
							hitPoint.localPointA = new Vector(point);
							hitPoint.localPointB = new Vector(point);
							hitPoint.worldPointA = this.cachedFrame.multiply(point);
							hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
							hitPoint.worldNormalB = this.cachedFrame.as3x3().multiply(new Vector(1.0f, 0.0f, 0.0f));
							return this.cachedFrame.as3x3().multiply(point.subtract(r.origin)).length();
						}
			}

			// Positive y face
			if (r.direction.y < 0.0f)
			{
				float t = (this.maxPoint.y - r.origin.y) / r.direction.y;
				Vector point = r.at(t);

				if (t >= r.minTime && t <= r.maxTime)
					if (point.x >= this.minPoint.x && point.x <= this.maxPoint.x)
						if (point.z >= this.minPoint.z && point.z <= this.maxPoint.z)
						{
							hitPoint.localPointA = new Vector(point);
							hitPoint.localPointB = new Vector(point);
							hitPoint.worldPointA = this.cachedFrame.multiply(point);
							hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
							hitPoint.worldNormalB = this.cachedFrame.as3x3().multiply(new Vector(0.0f, 1.0f, 0.0f));
							return this.cachedFrame.as3x3().multiply(point.subtract(r.origin)).length();
						}
			}

			// Positive z face
			if (r.direction.z < 0.0f)
			{
				float t = (this.maxPoint.z - r.origin.z) / r.direction.z;
				Vector point = r.at(t);

				if (t >= r.minTime && t <= r.maxTime)
					if (point.x >= this.minPoint.x && point.x <= this.maxPoint.x)
						if (point.y >= this.minPoint.y && point.y <= this.maxPoint.y)
						{
							hitPoint.localPointA = new Vector(point);
							hitPoint.localPointB = new Vector(point);
							hitPoint.worldPointA = this.cachedFrame.multiply(point);
							hitPoint.worldPointB = new Vector(hitPoint.worldPointA);
							hitPoint.worldNormalB = this.cachedFrame.as3x3().multiply(new Vector(0.0f, 0.0f, 1.0f));
							return this.cachedFrame.as3x3().multiply(point.subtract(r.origin)).length();
						}
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
	public float getGreatestPointAlongAxis(final Matrix frame, final Vector axis, Vector point)
	{
		synchronized (this.cacheLock)
		{
			this.cacheFrameMatrix(frame);

			Vector transformedAxis = this.cachedFrameInverse.as3x3().multiply(axis);

			Vector[] vertices = new Vector[] {
				new Vector(this.maxPoint),
				new Vector(this.maxPoint.x, this.maxPoint.y, this.minPoint.z),
				new Vector(this.maxPoint.x, this.minPoint.y, this.maxPoint.z),
				new Vector(this.maxPoint.x, this.minPoint.y, this.minPoint.z),
				new Vector(this.minPoint.x, this.maxPoint.y, this.maxPoint.z),
				new Vector(this.minPoint.x, this.maxPoint.y, this.minPoint.z),
				new Vector(this.minPoint.x, this.minPoint.y, this.maxPoint.z),
				new Vector(this.minPoint)
			};

			float greatestProjection = Float.NEGATIVE_INFINITY;
			Vector greatestVertex = new Vector();

			for (int i = 0; i < vertices.length; i++)
			{
				float projection = transformedAxis.dot(vertices[i]);
				if (projection > greatestProjection)
				{
					greatestProjection = projection;
					greatestVertex = vertices[i];
				}
			}

			if (point == null)
				point = new Vector();

			point.set(this.cachedFrame.multiply(greatestVertex));
		}
		return axis.dot(point);
	}

	@Override
	public Vector getLocalPoint(Matrix frame, Vector point)
	{
		synchronized (this.cacheLock)
		{
			this.cacheFrameMatrix(frame);
			return this.cachedFrameInverse.multiply(point);
		}
	}

	@Override
	public int getEdgeCount() {
		return 12;
	}

	@Override
	public Vector getEdge(Matrix frame, int index)
	{
		Vector edge = null;

		if (index >= 0 && index <=3)
			edge = new Vector(1.0f, 0.0f, 0.0f);
		else if (index >= 4 && index <= 7)
			edge = new Vector(0.0f, 1.0f, 0.0f);
		else if (index >= 8 && index <=11)
			edge = new Vector(0.0f, 0.0f, 1.0f);

		if (edge == null)
			return null;

		return frame.as3x3().multiply(edge);
	}

	@Override
	public int getFaceCount() {
		return 6;
	}

	@Override
	public Vector getFaceNormal(Matrix frame, int index)
	{
		Vector normal = null;

		switch (index)
		{
		case 0:
			normal = new Vector(1.0f, 0.0f, 0.0f);
			break;
		case 1:
			normal = new Vector(0.0f, 1.0f, 0.0f);
			break;
		case 2:
			normal = new Vector(0.0f, 0.0f, 1.0f);
			break;
		case 3:
			normal = new Vector(-1.0f, 0.0f, 0.0f);
			break;
		case 4:
			normal = new Vector(0.0f, -1.0f, 0.0f);
			break;
		case 5:
			normal = new Vector(0.0f, 0.0f, -1.0f);
		}

		if (normal == null)
			return null;

		return frame.as3x3().multiply(normal);
	}

	@Override
	public String getTypeName() {
		return "Box";
	}

	protected void cacheFrameMatrix(Matrix frame)
	{
		if (frame.equals(this.cachedFrame))
			return;

		this.cachedFrame = new Matrix(frame);
		this.cachedFrameInverse = this.cachedFrame.inverted();
	}
}
