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
	public final Vector lowCorner;
	public final Vector highCorner;

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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector slideRay(Ray ray)
	{
		Intersection intersection = this.castRay(ray);
		if (intersection == null)
			return null;

		return intersection.normal.cross(ray.direction.cross(intersection.normal).normalized());
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
		// TODO Auto-generated method stub
		return null;
	}

}
