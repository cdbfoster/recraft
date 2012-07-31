/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.core;

import java.util.List;

public interface Broadphase
{
	List<BroadphasePair> calculateOverlappingPairs(List<PhysicsObject> objects);

	public static class BroadphasePair
	{
		public final PhysicsObject a, b;

		public BroadphasePair(PhysicsObject a, PhysicsObject b)
		{
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean equals(Object b)
		{
			if (this == b)
				return true;
			if (b == null)
				return false;
			if (this.getClass() != b.getClass())
				return false;

			BroadphasePair c = (BroadphasePair)b;

			if ((this.a == c.a && this.b == c.b) || (this.b == c.a && this.a == c.b))
				return true;

			return false;
		}

		@Override
		public int hashCode()
		{
			int hash = 15;

			hash = 17 * hash + this.a.hashCode();
			hash = 17 * hash + this.b.hashCode();

			return hash;
		}
	}
}
