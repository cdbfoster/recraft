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
import physics.core.PhysicsObject;
import collision.core.CollisionObject;

public class RigidBodyPhysicsObject extends CollisionNotified implements PhysicsObject
{
	protected CollisionObject broadphaseProxy;
	protected CollisionObject narrowphaseProxy;

	private Vector position;
	private Vector eulerRotation;
	private Vector scale;

	private Vector linearVelocity;
	//private Vector angularVelocity;

	private Vector linearAcceleration;

	private float mass;

	@Override
	public void setPosition(Vector position)
	{
		this.position = new Vector(position);
		this.broadphaseProxy.translate(this.position.x, this.position.y, this.position.z);
		this.narrowphaseProxy.translate(this.position.x, this.position.y, this.position.z);
	}

	@Override
	public Vector getPosition()
	{
		return new Vector(this.position);
	}

	@Override
	public void setEulerRotation(Vector radians)
	{
		this.eulerRotation = new Vector(radians);
		this.broadphaseProxy.rotate(this.eulerRotation.x, this.eulerRotation.y, this.eulerRotation.z);
		this.narrowphaseProxy.rotate(this.eulerRotation.x, this.eulerRotation.y, this.eulerRotation.z);
	}

	@Override
	public Vector getEulerRotation()
	{
		return new Vector(this.eulerRotation);
	}

	@Override
	public void setScale(Vector scale)
	{
		this.scale = new Vector(scale);
		this.broadphaseProxy.scale(this.scale.x, this.scale.y, this.scale.z);
		this.narrowphaseProxy.scale(this.scale.x, this.scale.y, this.scale.z);
	}

	@Override
	public Vector getScale()
	{
		return new Vector(this.scale);
	}

	@Override
	public void setTransform(Matrix transform)
	{
		this.broadphaseProxy.setTransform(transform);
		this.narrowphaseProxy.setTransform(transform);
		this.update();
	}

	@Override
	public Matrix getTransform()
	{
		return this.narrowphaseProxy.getTransform();
	}

	@Override
	public void setLinearVelocity(Vector linearVelocity)
	{
		this.linearVelocity = new Vector(linearVelocity);
	}

	@Override
	public Vector getLinearVelocity()
	{
		return new Vector(this.linearVelocity);
	}

	@Override
	public void setLinearAcceleration(Vector linearAcceleration)
	{
		this.linearAcceleration = new Vector(linearAcceleration);
	}

	@Override
	public Vector getLinearAcceleration()
	{
		return new Vector(this.linearAcceleration);
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
	public void update()
	{
		// TODO Extract transformation components out of collision object transform matrix
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
