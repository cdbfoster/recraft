/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.core;

public class CollisionException extends Exception
{
	private static final long serialVersionUID = -5223131016851365554L;

	public CollisionException()
	{
		super();
	}

	public CollisionException(String message)
	{
		super(message);
	}
}
