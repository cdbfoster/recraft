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

import recraft.util.AutoArrayList;
import recraft.util.IntVector3;

/** The Terrain interface abstracts the storage of Blocks and Biomes. All terrain-related queries should be
 * made through this interface. This interface also handles Block updating through scheduling and its update
 * method. */
public interface Terrain
{
	/** Instructs the Terrain object to represent the given block at the given location.  Terrain objects
	 * should be aware of Block's requiresConstantUpdate method. */
	boolean setBlock(IntVector3 location, Block block);

	/** Returns the Block at the given location in the Terrain object.  For safety reasons, any modifications
	 * should be made to a clone of the returned Block (with Block's cloneBlock method) and then set back into
	 * the Terrain object with its setBlock method. */
	Block getBlock(IntVector3 location);

	/** Sets an entire region of Blocks/Biomes defined by the given terrainChunk at a given offset.  Useful
	 * for possibly optimized mass edits if the Terrain object maintains similar TerrainChunk objects internally. */
	boolean setTerrainChunk(IntVector3 offset, TerrainChunk terrainChunk);

	/** Fills and returns a TerrainChunk object with data from the specified region of the Terrain object. */
	TerrainChunk getTerrainChunk(IntVector3 origin, IntVector3 size);

	/** Updates the Block at the specified location in the Terrain object. */
	boolean updateBlock(IntVector3 location);

	/** Updates the Blocks at the neighboring locations of the Block at the specified location in the Terrain
	 * object. */
	boolean updateBlockNeighbors(IntVector3 location);

	/** Schedules a Block update at the specified location in the Terrain object, at a certain number of update
	 * ticks from the present. */
	void scheduleUpdateBlock(IntVector3 location, int ticksFromNow);

	/** Unschedules a previously scheduled update for the Block at location in the Terrain object.  Safe to call
	 * even was no update scheduled. */
	void unscheduleUpdateBlock(IntVector3 location);

	/** Instructs the Terrain object to represent the given biome at the given location. */
	boolean setBiome(IntVector3 location, Biome biome);

	/** Returns the Biome at the given location in the Terrain object.  Be aware that multiple locations in the
	 * Terrain object may share this Biome object. */
	Biome getBiome(IntVector3 location);

	/** Returns true if the entire specified region is represented by the Terrain object -- otherwise false. */
	boolean regionExists(IntVector3 origin, IntVector3 size);

	/** Updates any Blocks requiring an update.  The Terrain object may also use the given world to determine
	 * which parts of itself to keep in memory (For example: Based on proximity to players, etc.). */
	void update(World world);
}
