/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.core;

public interface PhysicsWorld
{
	void addPhysicsObject(PhysicsObject object);
	void removePhysicsObject(PhysicsObject object);

	void clearWorld();

	void stepWorld(float timeStep);
}
