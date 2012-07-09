/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import collision.algorithm.ConvexPolyhedronConvexPolyhedronCollisionAlgorithm;
import collision.core.CollisionAlgorithm.CollisionAlgorithmDescriptor;

public class CollisionDetector
{
	// TODO create mapping type, probably inside CollisionAlgorithm
	private static Map<CollisionAlgorithmDescriptor, CollisionAlgorithm> algorithms = new HashMap<CollisionAlgorithmDescriptor, CollisionAlgorithm>();

	public static CollisionResult detectCollision(CollisionObject a, CollisionObject b)
	{
		CollisionAlgorithm algorithm = findSuitableAlgorithm(a, b);

		if (algorithm == null)
			return null;

		return algorithm.detectCollision(a, b);
	}

	public static synchronized void registerAlgorithm(CollisionAlgorithm algorithm)
	{
		algorithms.put(algorithm.getDescriptor(), algorithm);
	}

	private static synchronized CollisionAlgorithm findSuitableAlgorithm(CollisionObject a, CollisionObject b)
	{
		// Look for an exact match first
		CollisionAlgorithmDescriptor algorithmDescriptor = new CollisionAlgorithmDescriptor(a.getShape().getClass(), b.getShape().getClass());
		CollisionAlgorithm algorithm = algorithms.get(algorithmDescriptor);

		if (algorithm == null)
		{
			// No exact match was found, look for a match with superclasses
			Iterator<CollisionAlgorithm> algorithmIterator = algorithms.values().iterator();
			while (algorithmIterator.hasNext())
			{
				CollisionAlgorithm currentAlgorithm = algorithmIterator.next();
				CollisionAlgorithmDescriptor currentAlgorithmDescriptor = currentAlgorithm.getDescriptor();

				if (algorithmDescriptor.isDerivedFrom(currentAlgorithmDescriptor))
				{
					algorithm = currentAlgorithm;
					break;
				}
			}
		}

		return algorithm;
	}

	static
	{
		registerAlgorithm(new ConvexPolyhedronConvexPolyhedronCollisionAlgorithm());
	}
}
