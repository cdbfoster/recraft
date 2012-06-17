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

import math.Vector;

public class Intersection
{
	public Vector point;
	public Vector normal;
	public Polyhedron object;
	public float time;

	public Intersection()
	{
		this.point = new Vector();
		this.normal = new Vector(0.0f, 0.0f, 1.0f);
		this.object = null;
		this.time = Float.POSITIVE_INFINITY;
	}

	public Intersection(Vector point, Vector normal, Polyhedron object, float time)
	{
		this.point = point;
		this.normal = normal;
		this.object = object;
		this.time = time;
	}
}
