/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.algorithm;

import math.Constants;
import math.Matrix;
import math.Vector;
import collision.core.CollisionAlgorithm;
import collision.core.CollisionObject;
import collision.core.CollisionPoint;
import collision.shape.ConvexPolyhedronCollisionShape;

public class ConvexPolyhedronConvexPolyhedronCollisionAlgorithm implements CollisionAlgorithm
{
	@Override
	public CollisionPoint detectCollision(CollisionObject a, CollisionObject b)
	{
		ConvexPolyhedronCollisionShape shapeA = (ConvexPolyhedronCollisionShape)a.getShape();
		ConvexPolyhedronCollisionShape shapeB = (ConvexPolyhedronCollisionShape)b.getShape();

		// Object space to World space transforms
		Matrix transformA = a.getTransform();
		Matrix transformB = b.getTransform();

		Vector aabbCentersOffset = null;
		{
			Vector aabbMinA = new Vector();
			Vector aabbMaxA = new Vector();
			shapeA.getAABB(transformA, aabbMinA, aabbMaxA);

			Vector aabbMinB = new Vector();
			Vector aabbMaxB = new Vector();
			shapeB.getAABB(transformB, aabbMinB, aabbMaxB);

			aabbCentersOffset = aabbMinB.add(aabbMaxB).divide(2.0f).subtract(aabbMinA.add(aabbMaxA).divide(2.0f));
		}

		float leastOverlap = Float.POSITIVE_INFINITY;
		Vector leastOverlapNormal = new Vector();
		Vector leastOverlapPointA = new Vector();
		Vector leastOverlapPointB = new Vector();

		int shapeAFaceCount = shapeA.getFaceCount();
		int shapeBFaceCount = shapeB.getFaceCount();

		// Use all face normals as separating axes
		for (int i = 0; i < shapeAFaceCount + shapeBFaceCount; i++)
		{
			Vector axis = null;
			if (i < shapeAFaceCount)
				axis = shapeA.getFaceNormal(transformA, i);
			else
				axis = shapeB.getFaceNormal(transformB, i - shapeAFaceCount);

			int axisSign = (axis.dot(aabbCentersOffset) >= 0.0f ? 1 : -1);

			Vector extentPointA = new Vector();
			Vector extentPointB = new Vector();

			float extentA = shapeA.getGreatestPointAlongAxis(transformA, axis.multiply(axisSign), extentPointA);
			float extentB = -shapeB.getGreatestPointAlongAxis(transformB, axis.multiply(-axisSign), extentPointB);

			float overlap = extentA - extentB;

			if (overlap < 0)
				return null;

			if (overlap < leastOverlap)
			{
				leastOverlap = overlap;
				leastOverlapNormal = axis.multiply(-axisSign);
				leastOverlapPointA = extentPointA;
				leastOverlapPointB = extentPointB;
			}
		}

		for (int iA = 0; iA < shapeA.getEdgeCount(); iA++)
		{
			Vector edgeA = shapeA.getEdge(transformA, iA);

			for (int iB = 0; iB < shapeB.getEdgeCount(); iB++)
			{
				Vector edgeB = shapeB.getEdge(transformB, iB);

				Vector axis = edgeA.cross(edgeB);

				if (axis.isZero(16.0f * Constants.FLT_EPSILON))
					continue;

				axis.normalize();
				int axisSign = (axis.dot(aabbCentersOffset) >= 0.0f ? 1 : -1);

				Vector extentPointA = new Vector();
				Vector extentPointB = new Vector();

				float extentA = shapeA.getGreatestPointAlongAxis(transformA, axis.multiply(axisSign), extentPointA);
				float extentB = -shapeB.getGreatestPointAlongAxis(transformB, axis.multiply(-axisSign), extentPointB);

				float overlap = extentA - extentB;

				if (overlap < 0)
					return null;

				if (overlap < leastOverlap)
				{
					leastOverlap = overlap;
					leastOverlapNormal = axis.multiply(-axisSign);
					leastOverlapPointA = extentPointA;
					leastOverlapPointB = extentPointB;
				}
			}
		}

		CollisionPoint result = new CollisionPoint();
		result.localPointA = shapeA.getLocalPoint(transformA, leastOverlapPointA);
		result.localPointB = shapeB.getLocalPoint(transformB, leastOverlapPointB);
		result.worldNormalB = leastOverlapNormal;
		result.worldPointA = leastOverlapPointA;
		result.worldPointB = leastOverlapPointB;

		return result;
	}

	@Override
	public CollisionAlgorithmDescriptor getDescriptor()
	{
		return new CollisionAlgorithmDescriptor(ConvexPolyhedronCollisionShape.class, ConvexPolyhedronCollisionShape.class);
	}

}
