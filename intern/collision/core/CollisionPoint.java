/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.core;

import java.io.Serializable;

import math.Vector;

public class CollisionPoint implements Serializable
{
	private static final long serialVersionUID = 42487783103184857L;

	public Vector localPointA, localPointB;
	public Vector worldPointA, worldPointB;
	public Vector worldNormalB;
}
