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
	@Override
	public abstract void getAABB(final Matrix frame, Vector aabbMin, Vector aabbMax);

	@Override
	public abstract float castRay(final Matrix frame, final Ray ray, CollisionPoint hitPoint);

	/** Projects all vertices of this shape onto axis and returns the largest value.  All calculations are done with respect to frame. */
	public abstract float getGreatestVertexAlongAxis(Matrix frame, Vector axis);

	public abstract int getEdgeCount();
	public abstract Vector getEdge(Matrix frame, int index);

	public abstract int getFaceCount();
	public abstract Vector getFaceNormal(Matrix frame, int index);

	@Override
	public abstract String getTypeName();
}
