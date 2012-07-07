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

public interface ConvexPolyhedronCollisionShape extends CollisionShape
{
	@Override
	void getAABB(final Matrix frame, Vector aabbMin, Vector aabbMax);

	@Override
	float castRay(final Matrix frame, final Ray ray, CollisionPoint hitPoint);

	/** Projects all vertices of this shape onto axis and returns the largest value.  All calculations are done with respect to frame. */
	float getGreatestVertexAlongAxis(Matrix frame, Vector axis);

	int getEdgeCount();
	Vector getEdge(Matrix frame, int index);

	int getFaceCount();
	Vector getFaceNormal(Matrix frame, int index);

	@Override
	String getTypeName();
}
