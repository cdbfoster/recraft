/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.object;

import math.Matrix;
import math.Vector;
import physics.core.CollisionNotified;
import physics.core.MotionState;
import physics.core.PhysicsObject;
import collision.core.CollisionObject;

public class RigidBodyPhysicsObject extends CollisionNotified implements PhysicsObject
{
	protected CollisionObject broadphaseProxy;
	protected CollisionObject narrowphaseProxy;

	protected MotionState motionState;

	private float mass;

	@Override
	public MotionState getMotionState()
	{
		return this.motionState;
	}

	@Override
	public void applyForce(Vector force)
	{
		// TODO Apply force
	}

	@Override
	public void clearForce()
	{
		// TODO Clear all force
	}

	@Override
	public void setMass(float mass)
	{
		this.mass = mass;
	}

	@Override
	public float getMass()
	{
		return mass;
	}

	@Override
	public CollisionNotified getNotifiedObject()
	{
		return this;
	}

	@Override
	public void updateMotionState()
	{
		this.motionState.setTransform(this.narrowphaseProxy.getTransform());
	}

	@Override
	public void updateCollisionObjects()
	{
		Matrix transform = this.motionState.getTransform();
		this.broadphaseProxy.setTransform(transform);
		this.narrowphaseProxy.setTransform(transform);
	}

	@Override
	public CollisionObject GetBroadphaseProxy()
	{
		return this.broadphaseProxy;
	}

	@Override
	public CollisionObject GetNarrowphaseProxy()
	{
		return this.narrowphaseProxy;
	}

}
