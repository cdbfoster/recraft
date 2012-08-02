/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.core;

import java.io.Serializable;

import collision.core.CollisionPoint;

public class PhysicsCollision implements Serializable
{
	private static final long serialVersionUID = -4188813862977422853L;

	public PhysicsObject a, b;
	public CollisionPoint point;

	public PhysicsCollision reverse()
	{
		PhysicsCollision result = new PhysicsCollision();

		result.a = this.b;
		result.b = this.a;
		result.point = this.point.reverse();

		return result;
	}
}
