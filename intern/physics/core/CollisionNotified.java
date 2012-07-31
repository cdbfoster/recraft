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

import collision.core.CollisionResult;

public class CollisionNotified
{
	protected List<CollisionResult> collisions;

	public boolean isCollided()
	{
		return (this.collisions != null);
	}

	public void AddCollision(CollisionResult collision)
	{
		if (this.collisions == null)
			this.collisions = new LinkedList<CollisionResult>();

		this.collisions.add(collision);
	}

	public List<CollisionResult> getCollisions()
	{
		return this.collisions;
	}

	public void clearCollisions()
	{
		this.collisions = null;
	}
}
