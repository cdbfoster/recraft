/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.narrowphase;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import physics.core.Broadphase.BroadphasePair;
import physics.core.Narrowphase;
import collision.core.CollisionDetector;
import collision.core.CollisionResult;

public class SimpleNarrowphase implements Narrowphase
{
	@Override
	public List<CollisionResult> calculateCollisions(List<BroadphasePair> pairs)
	{
		List<CollisionResult> collisions = new LinkedList<CollisionResult>();

		ListIterator<BroadphasePair> pairIterator = pairs.listIterator();
		while (pairIterator.hasNext())
		{
			BroadphasePair pair = pairIterator.next();

			CollisionResult result = CollisionDetector.detectCollision(pair.a.GetNarrowphaseProxy(), pair.b.GetNarrowphaseProxy());

			if (result == null)
				continue;

			pair.a.getNotifiedObject().AddCollision(result);
			pair.b.getNotifiedObject().AddCollision(result.reverse());

			collisions.add(result);
		}

		return collisions;
	}

}
