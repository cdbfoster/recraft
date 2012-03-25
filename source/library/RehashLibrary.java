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

package recraft.library;

import recraft.block.rehash.*;
import recraft.core.*;
import recraft.terrainprovider.rehash.*;
import recraft.util.AutoArrayList;

public class RehashLibrary extends Library
{
	public RehashLibrary()
	{
		super();

		// Initialize Block Registry
		LibrarySet<Block> blockRegistry = new LibrarySet<Block>(new AutoArrayList<Block>(256), Block.registry);
		/*  0*/	blockRegistry.set(AirBlock.id, new AirBlock());
		/*  1*/	blockRegistry.set(StoneBlock.id, new StoneBlock());
		/*  2*/	blockRegistry.set(GrassBlock.id, new GrassBlock());
		/*  3*/	blockRegistry.set(DirtBlock.id, new DirtBlock());
		/*  7*/	blockRegistry.set(BedrockBlock.id, new BedrockBlock());

		// Initialize Terrain Provider Registry
		LibrarySet<TerrainProvider> terrainProviderRegistry = new LibrarySet<TerrainProvider>(new AutoArrayList<TerrainProvider>(2), TerrainProvider.registry);
		/*  0*/	terrainProviderRegistry.set(0, new RehashTerrainProvider()); // TODO Outfit each core interface with a new() method and standardize arguments for each type.

		// Register the sets with the library.
		this.librarySets.put(knownSetNames.get(SET_BLOCK), blockRegistry);
		this.librarySets.put(knownSetNames.get(SET_TERRAIN_PROVIDER), terrainProviderRegistry);
	}
}
