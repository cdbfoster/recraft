/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.core;

import java.util.List;

import physics.core.Broadphase.BroadphasePair;
import collision.core.CollisionResult;

public interface Narrowphase
{
	List<CollisionResult> calculateCollisions(List<BroadphasePair> pairs);
}
