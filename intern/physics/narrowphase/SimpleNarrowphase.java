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
import physics.core.MotionState;
import physics.core.Narrowphase;
import physics.core.PhysicsCollision;
import collision.core.CollisionDetector;
import collision.core.CollisionPoint;

public class SimpleNarrowphase implements Narrowphase
{
	@Override
	public List<PhysicsCollision> calculateCollisions(List<BroadphasePair> pairs)
	{
		List<PhysicsCollision> collisions = new LinkedList<PhysicsCollision>();

		ListIterator<BroadphasePair> pairIterator = pairs.listIterator();
		while (pairIterator.hasNext())
		{
			BroadphasePair pair = pairIterator.next();

			CollisionPoint point = CollisionDetector.detectCollision(pair.a.GetNarrowphaseProxy(), pair.b.GetNarrowphaseProxy());

			if (point == null)
				continue;

			PhysicsCollision collision = new PhysicsCollision();
			collision.a = pair.a;
			collision.b = pair.b;
			collision.aMotion = new MotionState(pair.a.getMotionState());
			collision.bMotion = new MotionState(pair.b.getMotionState());
			collision.point = point;

			pair.a.getNotifiedObject().AddCollision(collision);
			pair.b.getNotifiedObject().AddCollision(collision.reverse());

			collisions.add(collision);
		}

		return collisions;
	}

}
