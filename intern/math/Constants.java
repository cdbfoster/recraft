/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Math Library.                                 *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package math;

public class Constants
{
	public static float PI = (float)Math.PI;
	public static float FLT_EPSILON;

	static
	{
		FLT_EPSILON = 1.0f;
		do
			FLT_EPSILON /= 2.0f;
		while ((1.0f + (FLT_EPSILON / 2.0f)) != 1.0f);
	}
}
