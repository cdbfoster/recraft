/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.core;

import math.Matrix;
import math.Vector;
import collision.core.CollisionObject;

public interface PhysicsObject
{
	void setPosition(Vector position);
	Vector getPosition();

	void setEulerRotation(Vector radians);
	Vector getEulerRotation();

	void setScale(Vector scale);
	Vector getScale();

	void setTransform(Matrix transform);
	Matrix getTransform();

	void setLinearVelocity(Vector linearVelocity);
	Vector getLinearVelocity();

	void setLinearAcceleration(Vector linearAcceleration);
	Vector getLinearAcceleration();

	//void setAngularVelocity(Vector angularVelocity);
	//Vector getAngularVelocity();

	void applyForce(Vector force);
	void clearForce();

	void setMass(float mass);
	float getMass();

	CollisionNotified getNotifiedObject();

	void update();

	CollisionObject GetBroadphaseProxy();
	CollisionObject GetNarrowphaseProxy();
}
