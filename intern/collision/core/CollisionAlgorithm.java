/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.core;

public interface CollisionAlgorithm
{
	CollisionPoint detectCollision(CollisionObject a, CollisionObject b);

	CollisionAlgorithmDescriptor getDescriptor();

	public static class CollisionAlgorithmDescriptor
	{
		public final Class<? extends CollisionShape> a, b;

		public CollisionAlgorithmDescriptor(Class<? extends CollisionShape> a, Class<? extends CollisionShape> b)
		{
			this.a = a;
			this.b = b;
		}

		public boolean isDerivedFrom(CollisionAlgorithmDescriptor other)
		{
			return (other.a.isAssignableFrom(this.a) && other.b.isAssignableFrom(this.b) ||
					other.a.isAssignableFrom(this.b) && other.b.isAssignableFrom(this.a));
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

			CollisionAlgorithmDescriptor c = (CollisionAlgorithmDescriptor)b;

			if (this.a.equals(c.a) && this.b.equals(c.b) || this.a.equals(c.b) && this.b.equals(c.a))
				return true;

			return false;
		}

		@Override
		public int hashCode()
		{
			int hash = 15; // Arbitrary
			hash = 17 * hash + (this.a.hashCode() + this.b.hashCode()); // 17 is odd prime

			return hash;
		}
	}
}
