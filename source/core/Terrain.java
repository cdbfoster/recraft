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

import java.util.HashMap;

import recraft.util.IntVector3;

public interface Terrain
{
	// For possibly optimized mass edits (Chunks will implement TerrainChunk, for example)
	boolean setTerrainChunk(IntVector3 offset, TerrainChunk terrainChunk);
	TerrainChunk getTerrainChunk(IntVector3 origin, IntVector3 size);

	// Individual block control
	boolean setBlock(IntVector3 location, Block block);
	Block getBlock(IntVector3 location);

	boolean updateBlock(IntVector3 location);
	boolean updateBlockNeighbors(IntVector3 location);

	void scheduleUpdateBlock(IntVector3 location, int ticksFromNow);
	void unscheduleUpdateBlock(IntVector3 location);

	// Unneeded?
	//void scheduleConstantUpdateBlock(IntCoordinate location);
	//void unscheduleConstantUpdateBlock(IntCoordinate location);

	// TODO Biomes, Fix these: Do not work with IDs, work with objects that can tell you IDs.  And do not return bytes.
	byte setBiome(IntVector3 location, byte biomeID);
	byte getBiome(IntVector3 location);

	boolean regionExists(IntVector3 origin, IntVector3 size);

	// Accepts a World parameter that the Terrain may determine which parts to keep in memory.
	void update(World world);
}
