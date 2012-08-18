/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.core;

import math.Vector;
import collision.core.CollisionObject;

public interface PhysicsObject
{
	MotionState getMotionState();

	void applyForce(Vector force);

	void setMass(float mass);
	float getMass();

	CollisionNotified getNotifiedObject();

	void updateMotionState();
	void updateCollisionObjects();

	CollisionObject GetBroadphaseProxy();
	CollisionObject GetNarrowphaseProxy();
}
