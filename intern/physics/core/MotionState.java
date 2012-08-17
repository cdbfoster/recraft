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

public class MotionState
{
	private Vector position;
	private Vector eulerRotation;
	private Vector scale;

	private Matrix transform;

	private Vector linearVelocity;
	private Vector linearAcceleration;

	//private Vector angularVelocity;

	public MotionState()
	{
		this.position = new Vector(0.0f, 0.0f, 0.0f);
		this.eulerRotation = new Vector(0.0f, 0.0f, 0.0f);
		this.scale = new Vector(1.0f, 1.0f, 1.0f);

		this.transform = new Matrix();

		this.linearVelocity = new Vector(0.0f, 0.0f, 0.0f);
		this.linearAcceleration = new Vector(0.0f, 0.0f, 0.0f);
	}

	public void setPosition(Vector position)
	{
		this.position = new Vector(position);
		this.updateTransformMatrix();
	}
	public Vector getPosition()
	{
		return new Vector(this.position);
	}

	public void setEulerRotation(Vector radians)
	{
		this.eulerRotation = new Vector(radians);
		this.updateTransformMatrix();
	}
	public Vector getEulerRotation()
	{
		return new Vector(this.eulerRotation);
	}

	public void setScale(Vector scale)
	{
		this.scale = new Vector(scale);
		this.updateTransformMatrix();
	}
	public Vector getScale()
	{
		return new Vector(this.scale);
	}

	public void setTransform(Matrix transform)
	{
		this.transform = new Matrix(transform);
		this.updateTransformComponents();
	}
	public Matrix getTransform()
	{
		return new Matrix(this.transform);
	}

	public void setLinearVelocity(Vector linearVelocity)
	{
		this.linearVelocity = new Vector(linearVelocity);
	}
	public Vector getLinearVelocity()
	{
		return new Vector(this.linearVelocity);
	}

	public void setLinearAcceleration(Vector linearAcceleration)
	{
		this.linearAcceleration = new Vector(linearAcceleration);
	}
	public Vector getLinearAcceleration()
	{
		return new Vector(this.linearAcceleration);
	}

	//void setAngularVelocity(Vector angularVelocity);
	//Vector getAngularVelocity();

	private void updateTransformMatrix()
	{
		this.transform = Matrix.constructMatrix(this.position, this.eulerRotation, this.scale);
	}

	private void updateTransformComponents()
	{
		this.transform.deconstructMatrix(this.position, this.eulerRotation, this.scale);
	}
}
