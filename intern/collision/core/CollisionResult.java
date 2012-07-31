/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.core;

import java.io.Serializable;

public class CollisionResult implements Serializable
{
	private static final long serialVersionUID = -4188813862977422853L;

	public CollisionObject a, b;
	public CollisionPoint point;

	public CollisionResult reverse()
	{
		CollisionResult result = new CollisionResult();

		result.a = this.b;
		result.b = this.a;
		result.point = this.point.reverse();

		return result;
	}
}
