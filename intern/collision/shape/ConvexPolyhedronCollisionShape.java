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

public abstract class ConvexPolyhedronCollisionShape implements CollisionShape
{
	private static final long serialVersionUID = -5063244428973689839L;

	@Override
	public abstract void getAABB(final Matrix frame, Vector aabbMin, Vector aabbMax);

	@Override
	public abstract float castRay(final Matrix frame, final Ray ray, CollisionPoint hitPoint);

	/** Projects all vertices of this shape onto axis and returns the largest value.  The point at the largest value is returned in point.
	 * All calculations are done with respect to frame. */
	public abstract float getGreatestPointAlongAxis(final Matrix frame, final Vector axis, Vector point);

	/** Transforms the world space coordinate, point, in the local space of this shape as determined by frame. */
	public abstract Vector getLocalPoint(Matrix frame, Vector point);

	public abstract int getEdgeCount();
	public abstract Vector getEdge(Matrix frame, int index);

	public abstract int getFaceCount();
	public abstract Vector getFaceNormal(Matrix frame, int index);

	@Override
	public abstract String getTypeName();
}
