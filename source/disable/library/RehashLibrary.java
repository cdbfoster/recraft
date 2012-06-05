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
import recraft.terrain.rehash.*;
import recraft.terrainprovider.rehash.*;
import recraft.util.AutoArrayList;

public class RehashLibrary extends Library
{
	public RehashLibrary()
	{
		super();

		// Initialize Block Set
		LibrarySet<Block> blockSet = new LibrarySet<Block>(new AutoArrayList<Block>(256), Block.registry);
		/*  0*/	blockSet.set(AirBlock.id, new AirBlock());
		/*  1*/	blockSet.set(StoneBlock.id, new StoneBlock());
		/*  2*/	blockSet.set(GrassBlock.id, new GrassBlock());
		/*  3*/	blockSet.set(DirtBlock.id, new DirtBlock());
		/*  7*/	blockSet.set(BedrockBlock.id, new BedrockBlock());

		// Initialize Terrain Set
		LibrarySet<Terrain> terrainSet = new LibrarySet<Terrain>(new AutoArrayList<Terrain>(1), Terrain.registry);
		/*  0*/ terrainSet.set(0, new RehashTerrain());

		// Initialize Terrain Provider Set
		LibrarySet<TerrainProvider> terrainProviderSet = new LibrarySet<TerrainProvider>(new AutoArrayList<TerrainProvider>(2), TerrainProvider.registry);
		/*  0*/	terrainProviderSet.set(0, new RehashFlatWorldTerrainProvider());

		// Add the sets to the library.
		this.librarySets.put(knownSetNames.get(SET_BLOCK), blockSet);
		this.librarySets.put(knownSetNames.get(SET_TERRAIN), terrainSet);
		this.librarySets.put(knownSetNames.get(SET_TERRAIN_PROVIDER), terrainProviderSet);
	}
}
