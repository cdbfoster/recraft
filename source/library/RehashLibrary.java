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

	@Override
	public void registerBiomes(boolean overwriteExisting)
	{
		this.conditionalWrite(biomeRegistry, Biome.registry, overwriteExisting);
	}

	@Override
	public void registerBiome(int id, boolean overwriteExisting)
	{
		this.conditionalWrite(biomeRegistry, id, Biome.registry, overwriteExisting);
	}

	@Override
	public void registerBlocks(boolean overwriteExisting)
	{
		this.conditionalWrite(blockRegistry, Block.registry, overwriteExisting);
	}

	@Override
	public void registerBlock(int id, boolean overwriteExisting)
	{
		this.conditionalWrite(blockRegistry, id, Block.registry, overwriteExisting);
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
				if (!this.targetExists(destination, index))
					destination.set(index, source.get(index));
		}
	}

	private void conditionalWrite(AutoArrayList source, int sourceIndex, AutoArrayList destination, boolean overwriteExisting)
	{
		if (overwriteExisting)
			destination.set(sourceIndex, source.get(sourceIndex));
		else
			if (!this.targetExists(destination, sourceIndex))
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

		// Initialize other stuff like items, maybe mobs.. etc.
	}
}
