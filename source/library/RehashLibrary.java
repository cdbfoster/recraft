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
import recraft.util.AutoArrayList;

public class RehashLibrary implements Library
{
	public static final AutoArrayList<Biome> biomeRegistry = new AutoArrayList<Biome>(10);
	public static final AutoArrayList<Block> blockRegistry = new AutoArrayList<Block>(256);
	public static final AutoArrayList<Renderer> rendererRegistry = new AutoArrayList<Renderer>(1);
	public static final AutoArrayList<Terrain> terrainRegistry = new AutoArrayList<Terrain>(1);
	public static final AutoArrayList<TerrainChunk> terrainChunkRegistry = new AutoArrayList<TerrainChunk>(1);
	public static final AutoArrayList<TerrainProvider> terrainProviderRegistry = new AutoArrayList<TerrainProvider>(2);
	public static final AutoArrayList<World> worldRegistry = new AutoArrayList<World>(1);

	@Override
	public void registerBiomes(boolean overwriteExisting)
	{
		conditionalWrite(biomeRegistry, Biome.registry, overwriteExisting);
	}

	@Override
	public void registerBiome(int id, boolean overwriteExisting)
	{
		conditionalWrite(biomeRegistry, id, Biome.registry, overwriteExisting);
	}

	@Override
	public void registerBlocks(boolean overwriteExisting)
	{
		conditionalWrite(blockRegistry, Block.registry, overwriteExisting);
	}

	@Override
	public void registerBlock(int id, boolean overwriteExisting)
	{
		conditionalWrite(blockRegistry, id, Block.registry, overwriteExisting);
	}

	@Override
	public void registerRenderers(boolean overwriteExisting)
	{
		conditionalWrite(rendererRegistry, Renderer.registry, overwriteExisting);
	}

	@Override
	public void registerRenderer(int id, boolean overwriteExisting)
	{
		conditionalWrite(rendererRegistry, id, Renderer.registry, overwriteExisting);
	}

	@Override
	public void registerTerrains(boolean overwriteExisting)
	{
		conditionalWrite(terrainRegistry, Terrain.registry, overwriteExisting);
	}

	@Override
	public void registerTerrain(int id, boolean overwriteExisting)
	{
		conditionalWrite(terrainRegistry, id, Terrain.registry, overwriteExisting);
	}

	@Override
	public void registerTerrainChunks(boolean overwriteExisting)
	{
		conditionalWrite(terrainChunkRegistry, TerrainChunk.registry, overwriteExisting);
	}

	@Override
	public void registerTerrainChunk(int id, boolean overwriteExisting)
	{
		conditionalWrite(terrainChunkRegistry, id, TerrainChunk.registry, overwriteExisting);
	}

	@Override
	public void registerTerrainProviders(boolean overwriteExisting)
	{
		conditionalWrite(terrainProviderRegistry, TerrainProvider.registry, overwriteExisting);
	}

	@Override
	public void registerTerrainProvider(int id, boolean overwriteExisting)
	{
		conditionalWrite(terrainProviderRegistry, id, TerrainProvider.registry, overwriteExisting);
	}

	@Override
	public void registerWorlds(boolean overwriteExisting)
	{
		conditionalWrite(worldRegistry, World.registry, overwriteExisting);
	}

	@Override
	public void registerWorld(int id, boolean overwriteExisting)
	{
		conditionalWrite(worldRegistry, id, World.registry, overwriteExisting);
	}

	private boolean targetExists(AutoArrayList list, int index)
	{
		return (list.get(index) != null);
	}

	private void conditionalWrite(AutoArrayList source, AutoArrayList destination, boolean overwriteExisting)
	{
		int size = source.size();
		for (int index = 0; index < size; index++)
		{
			if (overwriteExisting)
				destination.set(index, source.get(index));
			else
				if (!targetExists(destination, index))
					destination.set(index, source.get(index));
		}
	}

	private void conditionalWrite(AutoArrayList source, int sourceIndex, AutoArrayList destination, boolean overwriteExisting)
	{
		if (overwriteExisting)
			destination.set(sourceIndex, source.get(sourceIndex));
		else
			if (!targetExists(destination, sourceIndex))
				destination.set(sourceIndex, source.get(sourceIndex));
	}

	static
	{
		// Initialize Biome List

		// Initialize Block List
		for (int index = 0; index < 256; index++)
			blockRegistry.add(null);

		/*  0*/	blockRegistry.set(AirBlock.id, new AirBlock());
		/*  1*/	blockRegistry.set(StoneBlock.id, new StoneBlock());
		/*  2*/	blockRegistry.set(GrassBlock.id, new GrassBlock());
		/*  3*/	blockRegistry.set(DirtBlock.id, new DirtBlock());
		/*  7*/	blockRegistry.set(BedrockBlock.id, new BedrockBlock());

		// Initialize Renderer List

		// Initialize Terrain List

		// Initialize TerrainChunk List

		// Initialize TerrainProvider List

		// Initialize World List
	}
}
