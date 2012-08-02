/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.core;

import java.util.LinkedList;
import java.util.List;


public class CollisionNotified
{
	protected List<PhysicsCollision> collisions;

	public boolean isCollided()
	{
		return (this.collisions != null);
	}

	public void AddCollision(PhysicsCollision collision)
	{
		if (this.collisions == null)
			this.collisions = new LinkedList<PhysicsCollision>();

		this.collisions.add(collision);
	}

	public List<PhysicsCollision> getCollisions()
	{
		return this.collisions;
	}

	public void clearCollisions()
	{
		this.collisions = null;
	}
}
