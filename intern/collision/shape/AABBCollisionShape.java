/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Collision Library.                            *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package collision.shape;

import math.Matrix;
import math.Vector;

public class AABBCollisionShape extends BoxCollisionShape
{
	private static final long serialVersionUID = 6776087204294921398L;

	protected Matrix cachedSeedFrame;

	public AABBCollisionShape(Vector minPoint, Vector maxPoint)
	{
		super(minPoint, maxPoint);

		this.cachedSeedFrame = new Matrix();
	}

	@Override
	public void getAABB(final Matrix frame, Vector aabbMin, Vector aabbMax)
	{
		synchronized (this.cacheLock)
		{
			this.cacheFrameMatrix(frame);

			aabbMin.set(this.cachedFrame.multiply(this.minPoint));
			aabbMax.set(this.cachedFrame.multiply(this.maxPoint));
		}
	}

	@Override
	public Vector getEdge(Matrix frame, int index)
	{
		if (index >= 0 && index <=3)
			return new Vector(1.0f, 0.0f, 0.0f);
		else if (index >= 4 && index <= 7)
			return new Vector(0.0f, 1.0f, 0.0f);
		else if (index >= 8 && index <=11)
			return new Vector(0.0f, 0.0f, 1.0f);

		return null;
	}

	@Override
	public Vector getFaceNormal(Matrix frame, int index)
	{
		switch (index)
		{
		case 0:
			return new Vector(1.0f, 0.0f, 0.0f);
		case 1:
			return new Vector(0.0f, 1.0f, 0.0f);
		case 2:
			return new Vector(0.0f, 0.0f, 1.0f);
		case 3:
			return new Vector(-1.0f, 0.0f, 0.0f);
		case 4:
			return new Vector(0.0f, -1.0f, 0.0f);
		case 5:
			return new Vector(0.0f, 0.0f, -1.0f);
		}
		return null;
	}

	@Override
	public String getTypeName()
	{
		return "Axis-Aligned Bounding Box";
	}

	@Override
	protected void cacheFrameMatrix(Matrix frame)
	{
		if (this.cachedSeedFrame.equals(frame))
			return;

		this.cachedSeedFrame = new Matrix(frame);
		this.cachedFrame = frame.multiply(frame.rotationPart().inverted());
		this.cachedFrameInverse = this.cachedFrame.inverted();
	}
}
