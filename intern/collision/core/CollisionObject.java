/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.core;

import java.io.Serializable;

import math.Matrix;
import math.Vector;

public class CollisionObject implements Serializable
{
	private static final long serialVersionUID = 2785545827041090126L;

	protected CollisionShape shape;

	protected Matrix transform;

	protected Vector linearVelocity;
	//protected Vector angularVelocity;

	public CollisionObject(CollisionShape shape)
	{
		this.shape = shape;

		this.transform = new Matrix();

		this.linearVelocity = new Vector();
		//this.angularVelocity = new Vector();
	}

	public CollisionObject(CollisionShape shape, Matrix transform)
	{
		this(shape);
		this.transform = new Matrix(transform);
	}

	public synchronized void rotate(float radiansX, float radiansY, float radiansZ)
	{
		Matrix matrix = Matrix.rotate(radiansX, radiansY, radiansZ);
		this.transform = matrix.multiply(this.transform);
	}

	public synchronized void scale(float scaleX, float scaleY, float scaleZ)
	{
		Matrix matrix = Matrix.scale(scaleX, scaleY, scaleZ);
		this.transform = matrix.multiply(this.transform);
	}

	public synchronized void translate(float translationX, float translationY, float translationZ)
	{
		Matrix matrix = Matrix.translate(translationX, translationY, translationZ);
		this.transform = matrix.multiply(this.transform);
	}

	public synchronized void setTransform(Matrix transform)
	{
		this.transform = new Matrix(transform);
	}

	public synchronized Matrix getTransform()
	{
		return new Matrix(this.transform);
	}

	public void getAABB(Vector aabbMin, Vector aabbMax)
	{
		this.shape.getAABB(this.transform, aabbMin, aabbMax);
	}

	public CollisionShape getShape()
	{
		return this.shape;
	}
}
