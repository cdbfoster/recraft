/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.core;

import java.util.HashMap;
import java.util.Map;

public class CollisionDetector
{
	// TODO create mapping type, probably inside CollisionAlgorithm
	private static Map<?, CollisionAlgorithm> algorithms = new HashMap();

	public static CollisionResult detectCollision(CollisionObject a, CollisionObject b)
	{
		return null;
	}

	public static void registerAlgorithm(Class<CollisionAlgorithm> algorithm)
	{

	}
}
