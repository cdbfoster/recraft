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

package recraft.terrainprovider.rehash.generator;

import recraft.block.rehash.*;
import recraft.core.Properties;
import recraft.core.TerrainChunk;
import recraft.core.TerrainProvider;
import recraft.terrainchunk.rehash.RehashTerrainChunk;
import recraft.util.IntVector3;

public class RehashFlatWorldTerrainGenerator implements TerrainProvider
{
	public RehashFlatWorldTerrainGenerator(Properties settings)
	{

	}

	@Override
	public TerrainChunk provideTerrain(IntVector3 origin, IntVector3 size)
	{
		if (origin == null)
			origin = new IntVector3(0, 0, 0);
		if (size == null)
			size = new IntVector3(16, 256, 16);

		TerrainChunk chunk = new RehashTerrainChunk(size);

		for (int x = 0; x < size.x; x++)
			for (int z = 0; z < size.z; z++)
			{
				IntVector3 pos = new IntVector3(x, 0 - origin.y, z);

				chunk.setBlock(new IntVector3(pos.x, pos.y + 3, pos.z), new GrassBlock());
				chunk.setBlock(new IntVector3(pos.x, pos.y + 2, pos.z), new DirtBlock());
				chunk.setBlock(new IntVector3(pos.x, pos.y + 1, pos.z), new DirtBlock());
				chunk.setBlock(pos, new BedrockBlock());
			}

		return chunk;
	}

	@Override
	public TerrainProvider newTerrainProvider(Properties settings)
	{
		return new RehashFlatWorldTerrainGenerator(settings);
	}

}
