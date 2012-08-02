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

public interface Narrowphase
{
	List<PhysicsCollision> calculateCollisions(List<BroadphasePair> pairs);
}
