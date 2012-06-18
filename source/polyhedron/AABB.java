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

package recraft.polyhedron;

import math.Ray;
import math.Vector;
import recraft.core.Intersection;
import recraft.core.Polyhedron;

public class AABB extends Polyhedron
{
	private Vector lowCorner;
	private Vector highCorner;

	public AABB(Vector lowCorner, Vector highCorner)
	{
		super();
		this.lowCorner = lowCorner;
		this.highCorner = highCorner;
	}

	// Can't rotate an axis aligned bounding box
	@Override
	public void rotate(float radiansX, float radiansY, float radiansZ) { }

	@Override
	public boolean intersectRay(Ray ray)
	{
		// Bring the ray into the space of the polyhedron
		Ray r = this.transformInverse.multiply(ray);

		// if r.origin is in the AABB, there is an intersection
		if (r.origin.x >= this.lowCorner.x && r.origin.x <= this.highCorner.x)
			if (r.origin.y >= this.lowCorner.y && r.origin.y <= this.highCorner.y)
				if (r.origin.z >= this.lowCorner.z && r.origin.z <= this.highCorner.z)
					return true;

		// Negative x face
		if (r.direction.x > 0.0f)
		{
			float t = (this.lowCorner.x - r.origin.x) / r.direction.x;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.y >= this.lowCorner.y && point.y <= this.highCorner.y)
					if (point.z >= this.lowCorner.z && point.z <= this.highCorner.z)
						return true;
		}

		// Negative y face
		if (r.direction.y > 0.0f)
		{
			float t = (this.lowCorner.y - r.origin.y) / r.direction.y;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.lowCorner.x && point.x <= this.highCorner.x)
					if (point.z >= this.lowCorner.z && point.z <= this.highCorner.z)
						return true;
		}

		// Negative z face
		if (r.direction.z > 0.0f)
		{
			float t = (this.lowCorner.z - r.origin.z) / r.direction.z;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.lowCorner.x && point.x <= this.highCorner.x)
					if (point.y >= this.lowCorner.y && point.y <= this.highCorner.y)
						return true;
		}

		// Positive x face
		if (r.direction.x < 0.0f)
		{
			float t = -(r.origin.x - this.highCorner.x) / r.direction.x;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.y >= this.lowCorner.y && point.y <= this.highCorner.y)
					if (point.z >= this.lowCorner.z && point.z <= this.highCorner.z)
						return true;
		}

		// Positive y face
		if (r.direction.y < 0.0f)
		{
			float t = -(r.origin.y - this.highCorner.y) / r.direction.y;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.lowCorner.x && point.x <= this.highCorner.x)
					if (point.z >= this.lowCorner.z && point.z <= this.highCorner.z)
						return true;
		}

		// Positive z face
		if (r.direction.z < 0.0f)
		{
			float t = -(r.origin.z - this.highCorner.z) / r.direction.z;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.lowCorner.x && point.x <= this.highCorner.x)
					if (point.y >= this.lowCorner.y && point.y <= this.highCorner.y)
						return true;
		}

		return false;
	}

	@Override
	public boolean intersectSphere(Ray ray, float radius)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersectPolyhedron(Ray ray, Polyhedron polyhedron)
	{
		// Optimize this case
		if (polyhedron instanceof AABB)
			return this.intersectAABB(ray, (AABB)polyhedron);

		return false;
	}

	@Override
	public Intersection castRay(Ray ray)
	{
		// Bring the ray into the space of the polyhedron
		Ray r = this.transformInverse.multiply(ray);

		// Negative x face
		if (r.direction.x > 0.0f)
		{
			float t = (this.lowCorner.x - r.origin.x) / r.direction.x;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.y >= this.lowCorner.y && point.y <= this.highCorner.y)
					if (point.z >= this.lowCorner.z && point.z <= this.highCorner.z)
						return new Intersection(this.transform.multiply(point), this.transform.as3x3().multiply(new Vector(-1.0f, 0.0f, 0.0f)).normalized(), this, t);
		}

		// Negative y face
		if (r.direction.y > 0.0f)
		{
			float t = (this.lowCorner.y - r.origin.y) / r.direction.y;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.lowCorner.x && point.x <= this.highCorner.x)
					if (point.z >= this.lowCorner.z && point.z <= this.highCorner.z)
						return new Intersection(this.transform.multiply(point), this.transform.as3x3().multiply(new Vector(0.0f, -1.0f, 0.0f)).normalized(), this, t);
		}

		// Negative z face
		if (r.direction.z > 0.0f)
		{
			float t = (this.lowCorner.z - r.origin.z) / r.direction.z;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.lowCorner.x && point.x <= this.highCorner.x)
					if (point.y >= this.lowCorner.y && point.y <= this.highCorner.y)
						return new Intersection(this.transform.multiply(point), this.transform.as3x3().multiply(new Vector(0.0f, 0.0f, -1.0f)).normalized(), this, t);
		}

		// Positive x face
		if (r.direction.x < 0.0f)
		{
			float t = -(r.origin.x - this.highCorner.x) / r.direction.x;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.y >= this.lowCorner.y && point.y <= this.highCorner.y)
					if (point.z >= this.lowCorner.z && point.z <= this.highCorner.z)
						return new Intersection(this.transform.multiply(point), this.transform.as3x3().multiply(new Vector(1.0f, 0.0f, 0.0f)).normalized(), this, t);
		}

		// Positive y face
		if (r.direction.y < 0.0f)
		{
			float t = -(r.origin.y - this.highCorner.y) / r.direction.y;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.lowCorner.x && point.x <= this.highCorner.x)
					if (point.z >= this.lowCorner.z && point.z <= this.highCorner.z)
						return new Intersection(this.transform.multiply(point), this.transform.as3x3().multiply(new Vector(0.0f, 1.0f, 0.0f)).normalized(), this, t);
		}

		// Positive z face
		if (r.direction.z < 0.0f)
		{
			float t = -(r.origin.z - this.highCorner.z) / r.direction.z;
			Vector point = r.at(t);

			if (t >= r.minTime && t <= r.maxTime)
				if (point.x >= this.lowCorner.x && point.x <= this.highCorner.x)
					if (point.y >= this.lowCorner.y && point.y <= this.highCorner.y)
						return new Intersection(this.transform.multiply(point), this.transform.as3x3().multiply(new Vector(0.0f, 0.0f, 1.0f)).normalized(), this, t);
		}

		return null;
	}

	@Override
	public Intersection castSphere(Ray ray, float radius)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Intersection castPolyhedron(Ray ray, Polyhedron polyhedron)
	{
		// Optimize this case
		if (polyhedron instanceof AABB)
			return this.castAABB(ray, (AABB)polyhedron);

		return null;
	}

	@Override
	public Vector slideRay(Ray ray)
	{
		Intersection intersection = this.castRay(ray);
		if (intersection == null)
			return null;

		Vector directionXNormal = ray.direction.cross(intersection.normal);
		if (directionXNormal.isZero())
			return new Vector();
		return intersection.normal.cross(directionXNormal.normalized());
	}

	@Override
	public Vector slideSphere(Ray ray, float radius)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector slidePolyhedron(Ray ray, Polyhedron polyhedron)
	{
		Intersection intersection = this.castPolyhedron(ray, polyhedron);
		if (intersection == null)
			return null;

		Vector directionXNormal = ray.direction.cross(intersection.normal);
		if (directionXNormal.isZero())
			return new Vector();
		return intersection.normal.cross(directionXNormal.normalized());
	}

	public Vector getLowCorner()
	{
		return this.transform.multiply(lowCorner);
	}

	public Vector getHighCorner()
	{
		return this.transform.multiply(highCorner);
	}

	private boolean overlapAABB(Vector otherLowCorner, Vector otherHighCorner)
	{
		Vector thisExtent = this.highCorner.subtract(this.lowCorner).divide(2.0f);
		Vector thisCenter = thisExtent.add(this.lowCorner);
		Vector otherExtent = otherHighCorner.subtract(otherLowCorner).divide(2.0f);
		Vector otherCenter = otherExtent.add(otherLowCorner);

		Vector centerOffset = otherCenter.subtract(thisCenter);

		if ((Math.abs(centerOffset.x) <= thisExtent.x + otherExtent.x) &&
			(Math.abs(centerOffset.y) <= thisExtent.y + otherExtent.y) &&
			(Math.abs(centerOffset.z) <= thisExtent.z + otherExtent.z))
			return true;

		return false;
	}

	private boolean intersectAABB(Ray ray, AABB aabb)
	{
		// Bring the other AABB data into the space of this AABB
		Ray r = this.transformInverse.multiply(ray);
		Vector otherLowCorner = this.transformInverse.multiply(ray.origin.add(aabb.getLowCorner()));
		Vector otherHighCorner = this.transformInverse.multiply(ray.origin.add(aabb.getHighCorner()));

		if (this.overlapAABB(otherLowCorner, otherHighCorner))
			return true;

		// The time per axis at which aabb starts intersecting this AABB
		Vector t0 = new Vector(0.0f, 0.0f, 0.0f);
		// The time per axis at which aabb stops intersecting this AABB
		Vector t1 = new Vector(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

		for (int axis = 0; axis < 3; axis++)
		{
			float thisMin = this.lowCorner.get(axis);
			float otherMin = otherLowCorner.get(axis);

			float thisMax = this.highCorner.get(axis);
			float otherMax = otherHighCorner.get(axis);

			float rDirection = r.direction.get(axis);

			if (thisMax < otherMin && rDirection < 0.0f)
				t0.set(axis, (thisMax - otherMin) / rDirection);
			else if (thisMin > otherMax && rDirection > 0.0f)
				t0.set(axis, (thisMin - otherMax) / rDirection);

			if (thisMin < otherMax && rDirection < 0.0f)
				t1.set(axis, (thisMin - otherMax) / rDirection);
			else if (thisMax > otherMin && rDirection > 0.0f)
				t1.set(axis, (thisMax - otherMin) / rDirection);
		}

		float time0 = Math.max(t0.x, Math.max(t0.y, t0.z));
		float time1 = Math.min(t1.x, Math.min(t1.y, t1.z));

		return !(time0 > time1);
	}

	private Intersection castAABB(Ray ray, AABB aabb)
	{
		// Bring the other AABB data into the space of this AABB
		Ray r = this.transformInverse.multiply(ray);
		Vector otherLowCorner = this.transformInverse.multiply(ray.origin.add(aabb.getLowCorner()));
		Vector otherHighCorner = this.transformInverse.multiply(ray.origin.add(aabb.getHighCorner()));

		if (this.overlapAABB(otherLowCorner, otherHighCorner))
			return new Intersection(new Vector(0.0f, 0.0f, 0.0f), ray.direction.multiply(-1.0f), this, 0.0f);

		// The time per axis at which aabb starts intersecting this AABB
		Vector t0 = new Vector(0.0f, 0.0f, 0.0f);
		// The time per axis at which aabb stops intersecting this AABB
		Vector t1 = new Vector(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

		for (int axis = 0; axis < 3; axis++)
		{
			float thisMin = this.lowCorner.get(axis);
			float otherMin = otherLowCorner.get(axis);

			float thisMax = this.highCorner.get(axis);
			float otherMax = otherHighCorner.get(axis);

			float rDirection = r.direction.get(axis);

			if (thisMax < otherMin && rDirection < 0.0f)
				t0.set(axis, (thisMax - otherMin) / rDirection);
			else if (thisMin > otherMax && rDirection > 0.0f)
				t0.set(axis, (thisMin - otherMax) / rDirection);

			if (thisMin < otherMax && rDirection < 0.0f)
				t1.set(axis, (thisMin - otherMax) / rDirection);
			else if (thisMax > otherMin && rDirection > 0.0f)
				t1.set(axis, (thisMax - otherMin) / rDirection);
		}

		float time0 = Math.max(t0.x, Math.max(t0.y, t0.z));
		float time1 = Math.min(t1.x, Math.min(t1.y, t1.z));

		if (time0 > time1)
			return null;

		// Get the index of the colliding axis
		int maxAxis = 0;
		float directionSign = 1.0f;
		float currentMax = 0.0f;
		for (int axis = 0; axis < 3; axis++)
		{
			if (t0.get(axis) > currentMax)
			{
				maxAxis = axis;
				directionSign = (r.direction.get(axis) > 0.0f ? -1.0f : 1.0f);
				currentMax = t0.get(axis);
			}
		}

		otherLowCorner = otherLowCorner.add(r.direction.multiply(time0));
		otherHighCorner = otherHighCorner.add(r.direction.multiply(time0));

		Vector intersectionLowCorner = new Vector(Math.max(this.lowCorner.x, otherLowCorner.x), Math.max(this.lowCorner.y, otherLowCorner.y), Math.max(this.lowCorner.z, otherLowCorner.z));
		Vector intersectionHighCorner = new Vector(Math.min(this.highCorner.x, otherHighCorner.x), Math.min(this.highCorner.y, otherHighCorner.y), Math.min(this.highCorner.z, otherHighCorner.z));

		// The center of the colliding area
		Vector point = intersectionHighCorner.subtract(intersectionLowCorner).divide(2.0f).add(intersectionLowCorner);
		point.set(maxAxis, this.highCorner.multiply(directionSign).get(maxAxis));

		Vector mask = new Vector(0.0f, 0.0f, 0.0f);
		mask.set(maxAxis, 1.0f);
		Vector normal = mask.multiply(directionSign);

		return new Intersection(this.transform.multiply(point), this.transform.as3x3().multiply(normal).normalized(), this, time0);
	}

}
