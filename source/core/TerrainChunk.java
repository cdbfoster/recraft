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

import recraft.util.IntVector3;

/** The TerrainChunk interface abstracts the storage of Blocks and Biomes and is intended for use as
 * a transfer medium between Terrain objects and TerrainProvider objects.  All given locations should be
 * expected to be local to the TerrainChunk's origin.*/
public interface TerrainChunk extends IDless
{
	/** Instructs the Terrain object to represent the given block at the given location.  TerrainChunk objects
	 * should be aware of Block's requiresConstantUpdate method. */
	boolean setBlock(IntVector3 location, Block block);

	/** Returns the Block at the given location in the TerrainChunk object.  For safety reasons, any modifications
	 * should be made to a clone of the returned Block (with Block's cloneBlock method) and then set back into
	 * the TerrainChunk object with its setBlock method. */
	Block getBlock(IntVector3 location);

	/** Instructs the TerrainChunk object to represent the given biome at the given location. */
	boolean setBiome(IntVector3 location, Biome biome);

	/** Returns the Biome at the given location in the TerrainChunk object.  Be aware that multiple
	 * locations in the TerrainChunk object may share this Biome object. */
	Biome getBiome(IntVector3 location);

	/** Returns the TerrainChunk's origin. */
	IntVector3 getOrigin();

	/** Returns true if the specified location is represented by the TerrainChunk object. */
	boolean containsLocation(IntVector3 location);

	/** Returns the size of the TerrainChunk on the X axis. */
	int getSizeX();
	/** Returns the size of the TerrainChunk on the Y axis. */
	int getSizeY();
	/** Returns the size of the TerrainChunk on the Z axis. */
	int getSizeZ();
}
