/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.core;

import java.io.Serializable;

import math.Matrix;
import math.Ray;
import math.Vector;

public interface CollisionShape extends Serializable
{
	/** Returns the extents of the AABB with respect to frame. */
	void getAABB(final Matrix frame, Vector aabbMin, Vector aabbMax);

	boolean isPolyhedral();
	boolean isConvex();
	boolean isConcave();

	/** Casts ray at this shape and returns hit information in hitpoint.  Function returns the time along the ray to the collision.
	 * All returns are given with respect to frame. */
	float castRay(final Matrix frame, final Matrix frameInverse, final Ray ray, CollisionPoint hitPoint);

	String getTypeName();
}
