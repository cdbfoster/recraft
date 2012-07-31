/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.broadphase;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import physics.core.Broadphase;
import physics.core.PhysicsObject;

public class DummyBroadphase implements Broadphase
{
	@Override
	public List<BroadphasePair> calculateOverlappingPairs(List<PhysicsObject> objects)
	{
		List<BroadphasePair> overlappingPairs = new LinkedList<BroadphasePair>();

		ListIterator<PhysicsObject> objectIterator = objects.listIterator();
		while (objectIterator.hasNext())
		{
			PhysicsObject a = objectIterator.next();

			ListIterator<PhysicsObject> otherIterator = objects.listIterator();
			while (otherIterator.hasNext())
			{
				PhysicsObject b = otherIterator.next();

				if (a == b)
					break;

				overlappingPairs.add(new BroadphasePair(a, b));
			}
		}

		return null;
	}
}
