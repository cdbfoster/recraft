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

import java.util.ArrayList;

import recraft.util.AutoArrayList;
import recraft.util.IntVector3;

/** Used by terrain generators to implement climate/region specific features.  Terrain generators can use
 * a Biome's TerrainProvider capabilities to "sample" a region of a biome for contribution into the final
 * terrain.*/
public interface Biome extends TerrainProvider
{
	static final AutoArrayList<Biome> registry = new AutoArrayList<Biome>(50);

	/** Returns the biome's ID number. */
	int getID();

	ArrayList getSpawnableCreatures(); // TODO Specify <Entity> at the end of ArrayList whenever we figure out what to do about mobs and stuff.

	@Override
	TerrainChunk provideTerrain(IntVector3 origin, IntVector3 size);

	// TODO This seems icky.  Is this where this stuff should go?
	float getTemperature(IntVector3 location);

	// TODO This seems icky.  Is this where this stuff should go?
	float getHumidity(IntVector3 location);

	/** Returns a biome of the same type. */
	Biome newBiome();
}
