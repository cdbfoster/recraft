/********************************************************************************
 *                                                                              *
 *  This file is part of Recraft.                                               *
 *                                                                              *
 *  Recraft is free software: you can redistribute it and/or modify             *
 *  it under the terms of the GNU General Public License as published by        *
 *  the Free Software Foundation, either version 3 of the License, or           *
 *  (at your option) any later version.                                         *
 *                                                                              *
 *  Recraft is distributed in the hope that it will be useful,                  *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the               *
 *  GNU General Public License for more details.                                *
 *                                                                              *
 *  You should have received a copy of the GNU General Public License           *
 *  along with Recraft.  If not, see <http://www.gnu.org/licenses/>.            *
 *                                                                              *
 *  Copyright 2012 Chris Foster.                                                *
 *                                                                              *
 ********************************************************************************/

package recraft.core;

public class Timer
{
	private static int ticksPerSecond = 20;
	private static int millisecondsPerTick = 50;

	public static void setTicksPerSecond(int ticksPerSecond)
	{
		Timer.ticksPerSecond = ticksPerSecond;
		Timer.millisecondsPerTick = 1000 / Timer.ticksPerSecond;
	}

	public static int getTicksPerSecond()
	{
		return Timer.ticksPerSecond;
	}

	public static long getTimeInMilliseconds()
	{
		return System.nanoTime() / 1000000;
	}

	public static int elapsedTicks(int elapsedMilliseconds)
	{
		return (elapsedMilliseconds > 0) ? (elapsedMilliseconds / Timer.millisecondsPerTick) : 0;
	}

	public static int millisecondRemainder(int elapsedMilliseconds)
	{
		return (elapsedMilliseconds > 0) ? (elapsedMilliseconds % Timer.millisecondsPerTick) : 0;
	}
}
