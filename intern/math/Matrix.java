/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Math Library.                                 *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package math;

import java.io.Serializable;

// Many of the methods in this class were written using techniques found in Blender's source code (http://www.blender.org).
public class Matrix implements Serializable
{
	private static final long serialVersionUID = -8921412792080681557L;

	public float[][] m;

	private void setIdentity()
	{
		this.m[0][0] = 1.0f; this.m[0][1] = 0.0f; this.m[0][2] = 0.0f; this.m[0][3] = 0.0f;
		this.m[1][0] = 0.0f; this.m[1][1] = 1.0f; this.m[1][2] = 0.0f; this.m[1][3] = 0.0f;
		this.m[2][0] = 0.0f; this.m[2][1] = 0.0f; this.m[2][2] = 1.0f; this.m[2][3] = 0.0f;
		this.m[3][0] = 0.0f; this.m[3][1] = 0.0f; this.m[3][2] = 0.0f; this.m[3][3] = 1.0f;
	}

	public Matrix()
	{
		this.m = new float[4][4];
		this.setIdentity();
	}

	public Matrix(float _00, float _01, float _02, float _03,
				  float _10, float _11, float _12, float _13,
				  float _20, float _21, float _22, float _23,
				  float _30, float _31, float _32, float _33)
	{
		this();
		this.m[0][0] = _00; this.m[0][1] = _01; this.m[0][2] = _02; this.m[0][3] = _03;
		this.m[1][0] = _10; this.m[1][1] = _11; this.m[1][2] = _12; this.m[1][3] = _13;
		this.m[2][0] = _20; this.m[2][1] = _21; this.m[2][2] = _22; this.m[2][3] = _23;
		this.m[3][0] = _30; this.m[3][1] = _31; this.m[3][2] = _32; this.m[3][3] = _33;
	}

	public Matrix(Matrix b)
	{
		this.m = b.m.clone();

		for (int row = 0; row < b.m.length; row++)
			this.m[row] = b.m[row].clone();
	}

	public Matrix transpose()
	{
		this.m = this.transposed().m;
		return this;
	}

	public Matrix transposed()
	{
		return new Matrix(this.m[0][0], this.m[1][0], this.m[2][0], this.m[3][0],
						  this.m[0][1], this.m[1][1], this.m[2][1], this.m[3][1],
						  this.m[0][2], this.m[1][2], this.m[2][2], this.m[3][2],
						  this.m[0][3], this.m[1][3], this.m[2][3], this.m[3][3]);
	}

	public Matrix invert()
	{
		this.m = this.inverted().m;
		return this;
	}

	public Matrix inverted()
	{
		Matrix result = new Matrix();
		Matrix temp = this.clone();

		for (int firstIndex = 0; firstIndex < 4; firstIndex++)
		{
			float max = Math.abs(this.m[firstIndex][firstIndex]);
			int maxRow = firstIndex; // This will contain the row index for the current column that contains the maximum

			for (int row = firstIndex + 1; row < 4; row++)
				if (Math.abs(temp.m[row][firstIndex]) > max)
				{
					max = Math.abs(temp.m[row][firstIndex]);
					maxRow = row;
				}

			if (max < Constants.FLT_EPSILON)
				// The matrix is uninvertible
				return new Matrix();

			if (maxRow != firstIndex)
				for (int index = 0; index < 4; index++)
				{
					float t = temp.m[maxRow][index];
					temp.m[maxRow][index] = temp.m[firstIndex][index];
					temp.m[firstIndex][index] = t;

					t = result.m[maxRow][index];
					result.m[maxRow][index] = result.m[firstIndex][index];
					result.m[firstIndex][index] = t;
				}

			float signedMax = temp.m[firstIndex][firstIndex];
			for (int index = 0; index < 4; index++)
			{
				temp.m[firstIndex][index] /= signedMax;
				result.m[firstIndex][index] /= signedMax;
			}

			for (int row = 0; row < 4; row++)
				if (row != firstIndex)
				{
					float rowValue = temp.m[row][firstIndex];
					for (int index = 0; index < 4; index++)
					{
						temp.m[row][index] -= temp.m[firstIndex][index] * rowValue;
						result.m[row][index] -= result.m[firstIndex][index] * rowValue;
					}
				}
		}
		return result;
	}

	public Matrix normalize()
	{
		Vector x = this.xAxis().normalized();
		Vector y = this.yAxis().normalized();
		Vector z = this.zAxis().normalized();

		this.m[0][0] = x.x; this.m[0][1] = y.x; this.m[0][2] = z.x;
		this.m[1][0] = x.y; this.m[1][1] = y.y; this.m[1][2] = z.y;
		this.m[2][0] = x.z; this.m[2][1] = z.y; this.m[2][2] = z.z;

		return this;
	}

	public Matrix normalized()
	{
		Vector x = this.xAxis().normalized();
		Vector y = this.yAxis().normalized();
		Vector z = this.zAxis().normalized();

		return new Matrix(x.x, x.y, x.z, this.m[0][3],
						  y.x, y.y, y.z, this.m[1][3],
						  z.x, z.y, z.z, this.m[2][3],
						  this.m[3][0], this.m[3][1], this.m[3][2], this.m[3][3]);
	}

	public Vector xAxis()
	{
		return new Vector(this.m[0][0], this.m[0][1], this.m[0][2]);
	}

	public Vector yAxis()
	{
		return new Vector(this.m[1][0], this.m[1][1], this.m[1][2]);
	}

	public Vector zAxis()
	{
		return new Vector(this.m[2][0], this.m[2][1], this.m[2][2]);
	}

	public void setAxis(int axis, Vector value)
	{
		if (axis < 0 || axis >= 3)
			return;
		for (int i = 0; i < 3; i++)
			this.m[axis][i] = value.get(i);
	}

	public Vector scalePart()
	{
		Matrix scaleMatrix = this.as3x3().multiply(this.rotationPart().inverted());
		return new Vector(scaleMatrix.m[0][0], scaleMatrix.m[1][1], scaleMatrix.m[2][2]);
	}

	public Matrix rotationPart()
	{
		Matrix normalizedMatrix = this.normalized().as3x3();
		if (normalizedMatrix.isNegative())
		{
			normalizedMatrix.setAxis(0, normalizedMatrix.xAxis().multiply(-1.0f));
			normalizedMatrix.setAxis(1, normalizedMatrix.yAxis().multiply(-1.0f));
			normalizedMatrix.setAxis(2, normalizedMatrix.zAxis().multiply(-1.0f));
		}

		return normalizedMatrix;
	}

	public Vector rotationPartAsEuler()
	{
		Matrix matrix = this.rotationPart();

		Vector euler1 = new Vector();
		Vector euler2 = new Vector();

		float CosY = (float)Math.sqrt(matrix.m[0][0] * matrix.m[0][0] + matrix.m[1][0] * matrix.m[1][0]);

		if (CosY > 16.0f * Constants.FLT_EPSILON)
		{
			euler1.x = (float)Math.atan2(matrix.m[2][1], matrix.m[2][2]);
			euler1.y = (float)Math.atan2(-matrix.m[2][0], CosY);
			euler1.z = (float)Math.atan2(matrix.m[1][0], matrix.m[0][0]);

			euler2.x = (float)Math.atan2(-matrix.m[2][1], -matrix.m[2][2]);
			euler2.y = (float)Math.atan2(-matrix.m[2][0], -CosY);
			euler2.z = (float)Math.atan2(-matrix.m[1][0], -matrix.m[0][0]);
		}
		else
		{
			euler1.x = (float)Math.atan2(-matrix.m[1][2], matrix.m[1][1]);
			euler1.y = (float)Math.atan2(-matrix.m[2][0], CosY);
			euler1.z = 0.0f;

			euler2.set(euler1);
		}

		// Return the one with lower values in it
		if (Math.abs(euler1.x) + Math.abs(euler1.y) + Math.abs(euler1.z) > Math.abs(euler2.x) + Math.abs(euler2.y) + Math.abs(euler2.z))
			return euler2;
		else
			return euler1;
	}

	public Vector translationPart()
	{
		return new Vector(this.m[0][3], this.m[1][3], this.m[2][3]);
	}

	public Matrix as3x3()
	{
		return new Matrix(this.m[0][0], this.m[0][1], this.m[0][2], 0.0f,
						  this.m[1][0], this.m[1][1], this.m[1][2], 0.0f,
						  this.m[2][0], this.m[2][1], this.m[2][2], 0.0f,
						  0.0f, 0.0f, 0.0f, 1.0f);
	}

	public Matrix multiply(Matrix b)
	{
		Matrix result = new Matrix();

		result.m[0][0] = this.m[0][0] * b.m[0][0] + this.m[0][1] * b.m[1][0] + this.m[0][2] * b.m[2][0] + this.m[0][3] * b.m[3][0];
		result.m[1][0] = this.m[1][0] * b.m[0][0] + this.m[1][1] * b.m[1][0] + this.m[1][2] * b.m[2][0] + this.m[1][3] * b.m[3][0];
		result.m[2][0] = this.m[2][0] * b.m[0][0] + this.m[2][1] * b.m[1][0] + this.m[2][2] * b.m[2][0] + this.m[2][3] * b.m[3][0];
		result.m[3][0] = this.m[3][0] * b.m[0][0] + this.m[3][1] * b.m[1][0] + this.m[3][2] * b.m[2][0] + this.m[3][3] * b.m[3][0];

		result.m[0][1] = this.m[0][0] * b.m[0][1] + this.m[0][1] * b.m[1][1] + this.m[0][2] * b.m[2][1] + this.m[0][3] * b.m[3][1];
		result.m[1][1] = this.m[1][0] * b.m[0][1] + this.m[1][1] * b.m[1][1] + this.m[1][2] * b.m[2][1] + this.m[1][3] * b.m[3][1];
		result.m[2][1] = this.m[2][0] * b.m[0][1] + this.m[2][1] * b.m[1][1] + this.m[2][2] * b.m[2][1] + this.m[2][3] * b.m[3][1];
		result.m[3][1] = this.m[3][0] * b.m[0][1] + this.m[3][1] * b.m[1][1] + this.m[3][2] * b.m[2][1] + this.m[3][3] * b.m[3][1];

		result.m[0][2] = this.m[0][0] * b.m[0][2] + this.m[0][1] * b.m[1][2] + this.m[0][2] * b.m[2][2] + this.m[0][3] * b.m[3][2];
		result.m[1][2] = this.m[1][0] * b.m[0][2] + this.m[1][1] * b.m[1][2] + this.m[1][2] * b.m[2][2] + this.m[1][3] * b.m[3][2];
		result.m[2][2] = this.m[2][0] * b.m[0][2] + this.m[2][1] * b.m[1][2] + this.m[2][2] * b.m[2][2] + this.m[2][3] * b.m[3][2];
		result.m[3][2] = this.m[3][0] * b.m[0][2] + this.m[3][1] * b.m[1][2] + this.m[3][2] * b.m[2][2] + this.m[3][3] * b.m[3][2];

		result.m[0][3] = this.m[0][0] * b.m[0][3] + this.m[0][1] * b.m[1][3] + this.m[0][2] * b.m[2][3] + this.m[0][3] * b.m[3][3];
		result.m[1][3] = this.m[1][0] * b.m[0][3] + this.m[1][1] * b.m[1][3] + this.m[1][2] * b.m[2][3] + this.m[1][3] * b.m[3][3];
		result.m[2][3] = this.m[2][0] * b.m[0][3] + this.m[2][1] * b.m[1][3] + this.m[2][2] * b.m[2][3] + this.m[2][3] * b.m[3][3];
		result.m[3][3] = this.m[3][0] * b.m[0][3] + this.m[3][1] * b.m[1][3] + this.m[3][2] * b.m[2][3] + this.m[3][3] * b.m[3][3];

		return result;
	}

	public Ray multiply(Ray b)
	{
		float maxTime = b.maxTime;
		if (maxTime < Float.POSITIVE_INFINITY)
		{
			Vector range = b.at(maxTime).subtract(b.origin);
			maxTime = this.as3x3().multiply(range).length();
		}

		float minTime = b.minTime;
		if (minTime < Float.POSITIVE_INFINITY)
		{
			Vector range = b.at(minTime).subtract(b.origin);
			minTime = this.as3x3().multiply(range).length();
		}

		return new Ray(this.multiply(b.origin), this.as3x3().multiply(b.direction), minTime, maxTime);
	}

	public Vector multiply(Vector b)
	{
		return new Vector(this.m[0][0] * b.x + this.m[0][1] * b.y + this.m[0][2] * b.z + this.m[0][3] * 1.0f,
						  this.m[1][0] * b.x + this.m[1][1] * b.y + this.m[1][2] * b.z + this.m[1][3] * 1.0f,
						  this.m[2][0] * b.x + this.m[2][1] * b.y + this.m[2][2] * b.z + this.m[2][3] * 1.0f);
	}

	public static Matrix scale(float scaleX, float scaleY, float scaleZ)
	{
		return new Matrix(scaleX, 0.0f, 0.0f, 0.0f,
						  0.0f, scaleY, 0.0f, 0.0f,
						  0.0f, 0.0f, scaleZ, 0.0f,
						  0.0f, 0.0f, 0.0f, 1.0f);
	}

	public static Matrix scale(Vector scaleXYZ)
	{
		return scale(scaleXYZ.x, scaleXYZ.y, scaleXYZ.z);
	}

	public static Matrix rotate(float radiansX, float radiansY, float radiansZ)
	{
		/*
		 Creates the following rotation matrix:
			cos(Y) * cos(Z)		sin(X) * sin(Y) * cos(Z) - cos(X) * sin(Z)		cos(X) * sin(Y) * cos(Z) + sin(X) * sin(Z)		0
			cos(Y) * sin(Z)		cos(X) * cos(Z) + sin(X) * sin(Y) * sin(Z)		cos(X) * sin(Y) * sin(Z) - sin(X) * cos(Z)		0
			-sin(Y)				sin(X) * cos(Y)									cos(X) * cos(Y)									0
			0					0												0												1
		 Rotation order: XYZ
		*/

		double SinX = Math.sin(radiansX);
		double SinY = Math.sin(radiansY);
		double SinZ = Math.sin(radiansZ);
		double CosX = Math.cos(radiansX);
		double CosY = Math.cos(radiansY);
		double CosZ = Math.cos(radiansZ);
		double SinXSinZ = SinX * SinZ;
		double SinXCosZ = SinX * CosZ;
		double CosXSinZ = CosX * SinZ;
		double CosXCosZ = CosX * CosZ;

		return new Matrix((float)(CosY * CosZ), (float)(SinY * SinXCosZ - CosXSinZ), (float)(SinY * CosXCosZ + SinXSinZ), 0.0f,
						  (float)(CosY * SinZ), (float)(SinY * SinXSinZ + CosXCosZ), (float)(SinY * CosXSinZ - SinXCosZ), 0.0f,
						  (float)(-SinY),       (float)(SinX * CosY),                (float)(CosX * CosY),                0.0f,
						  0.0f,                 0.0f,                                0.0f,                                1.0f);
	}

	public static Matrix rotate(Vector radiansXYZ)
	{
		return rotate(radiansXYZ.x, radiansXYZ.y, radiansXYZ.z);
	}

	public static Matrix translate(float translationX, float translationY, float translationZ)
	{
		return new Matrix(1.0f, 0.0f, 0.0f, translationX,
						  0.0f, 1.0f, 0.0f, translationY,
						  0.0f, 0.0f, 1.0f, translationZ,
						  0.0f, 0.0f, 0.0f, 1.0f);
	}

	public static Matrix translate(Vector translationXYZ)
	{
		return translate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
	}

	private boolean isNegative()
	{
		Vector z = this.xAxis().cross(this.yAxis());
		return z.dot(this.zAxis()) < 0.0f;
	}

	@Override
	public boolean equals(Object b)
	{
		if (this == b)
			return true;
		if (b == null)
			return false;
		if (this.getClass() != b.getClass())
			return false;

		Matrix c = (Matrix)b;

		for (int row = 0; row < 4; row++)
			for (int column = 0; column < 4; column++)
				if (Math.abs(this.m[row][column] - c.m[row][column]) > Constants.FLT_EPSILON)
					return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 15;
		for (int row = 0; row < 4; row++)
			for (int column = 0; column < 4; column++)
				hash = 17 * hash + new Float(this.m[row][column]).hashCode();
		return hash;
	}

	@Override
	public Matrix clone()
	{
		return new Matrix(this);
	}

	@Override
	public String toString()
	{
		return String.format("[ %f, %f, %f, %f ]\n[ %f, %f, %f, %f ]\n[ %f, %f, %f, %f ]\n[ %f, %f, %f, %f ]",
				this.m[0][0], this.m[0][1], this.m[0][2], this.m[0][3],
				this.m[1][0], this.m[1][1], this.m[1][2], this.m[1][3],
				this.m[2][0], this.m[2][1], this.m[2][2], this.m[2][3],
				this.m[3][0], this.m[3][1], this.m[3][2], this.m[3][3]);
	}

}
