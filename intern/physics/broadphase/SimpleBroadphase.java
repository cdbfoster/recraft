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

import math.Vector;
import physics.core.Broadphase;
import physics.core.PhysicsObject;

public class SimpleBroadphase implements Broadphase
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

				if (this.checkAABBs(a, b))
					overlappingPairs.add(new BroadphasePair(a, b));
			}
		}

		return null;
	}

	protected boolean checkAABBs(PhysicsObject a, PhysicsObject b)
	{
		Vector aMin = new Vector();
		Vector aMax = new Vector();
		a.GetBroadphaseProxy().getAABB(aMin, aMax);

		Vector bMin = new Vector();
		Vector bMax = new Vector();
		b.GetBroadphaseProxy().getAABB(bMin, bMax);

		if (aMin.x > bMax.x || bMin.x > aMax.x)
			return false;
		if (aMin.y > bMax.y || bMin.y > aMax.y)
			return false;
		if (aMin.z > bMax.z || bMin.z > aMax.z)
			return false;

		return true;
	}
}
