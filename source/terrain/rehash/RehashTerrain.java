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

package recraft.terrain.rehash;

import java.lang.Math;
import java.util.HashMap;

import recraft.core.Biome;
import recraft.core.Block;
import recraft.core.Properties;
import recraft.core.Terrain;
import recraft.core.TerrainChunk;
import recraft.core.World;
import recraft.terrainchunk.rehash.RehashTerrainChunk;
import recraft.util.IntVector3;

public class RehashTerrain implements Terrain
{
	private final HashMap<IntVector3, TerrainChunk> chunks = new HashMap<IntVector3, TerrainChunk>();

	public RehashTerrain()
	{

	}

	public RehashTerrain(Properties settings)
	{

	}

	@Override
	public boolean setBlock(IntVector3 location, Block block)
	{
		if (location == null || block == null)
			return false;

		IntVector3 chunkOrigin = this.getContainingChunkOrigin(location);
		TerrainChunk chunk = this.chunks.get(chunkOrigin);

		if (chunk == null)
		{
			chunk = new RehashTerrainChunk(RehashTerrainChunk.defaultSize);
			this.chunks.put(chunkOrigin, chunk);
		}

		return chunk.setBlock(location.subtract(chunkOrigin), block);
	}

	@Override
	public Block getBlock(IntVector3 location)
	{
		if (location == null)
			return null;

		IntVector3 chunkOrigin = this.getContainingChunkOrigin(location);
		TerrainChunk chunk = this.chunks.get(chunkOrigin);

		if (chunk == null)
			return null;

		return chunk.getBlock(location.subtract(chunkOrigin));
	}

	@Override
	// Optimized for RehashTerrainChunks with a size of RehashTerrainChunk.defaultSize.
	public boolean setTerrainChunk(IntVector3 offset, TerrainChunk terrainChunk)
	{
		if (terrainChunk == null)
			return false;
		if (offset == null)
			offset = new IntVector3(0, 0, 0);

		IntVector3 terrainChunkSize = terrainChunk.getSize();

		// Determine if the chunk is aligned with the boundaries of the other chunks in the terrain.
		boolean chunkAligned = offset.mod(RehashTerrainChunk.defaultSize).equals(new IntVector3(0, 0, 0));
		chunkAligned &= terrainChunkSize.equals(RehashTerrainChunk.defaultSize);

		// If terrainChunk is nicely formatted, simply insert it into the terrain.
		if (terrainChunk instanceof RehashTerrainChunk && chunkAligned)
			this.chunks.put(offset, terrainChunk); // Overwrite the chunk at offset, if any.

		// Otherwise...
		// Determine the number of chunks crossed by terrainChunk along each axis.
		int chunkSpanX = 1 + (int)Math.floor((double)(offset.x + terrainChunkSize.x - 1) / RehashTerrainChunk.defaultSize.x) - (int)Math.floor((double)offset.x / RehashTerrainChunk.defaultSize.x);
		int chunkSpanY = 1 + (int)Math.floor((double)(offset.y + terrainChunkSize.y - 1) / RehashTerrainChunk.defaultSize.y) - (int)Math.floor((double)offset.y / RehashTerrainChunk.defaultSize.y);
		int chunkSpanZ = 1 + (int)Math.floor((double)(offset.z + terrainChunkSize.z - 1) / RehashTerrainChunk.defaultSize.z) - (int)Math.floor((double)offset.z / RehashTerrainChunk.defaultSize.z);

		// The origin of the lowest chunk crossed by terrainChunk.
		IntVector3 startOrigin = this.getContainingChunkOrigin(offset);

		// For each chunk crossed by terrainChunk...
		for (int chunkX = 0; chunkX < chunkSpanX; chunkX++)
			for (int chunkY = 0; chunkY < chunkSpanY; chunkY++)
				for (int chunkZ = 0; chunkZ < chunkSpanZ; chunkZ++)
				{
					IntVector3 currentChunkOrigin = new IntVector3(chunkX, chunkY, chunkZ).multiply(RehashTerrainChunk.defaultSize).add(startOrigin);
					TerrainChunk currentChunk = this.chunks.get(currentChunkOrigin);

					// If the chunk doesn't exist, create it and put it into the terrain.
					if (currentChunk == null)
					{
						currentChunk = new RehashTerrainChunk(RehashTerrainChunk.defaultSize);
						this.chunks.put(currentChunkOrigin, currentChunk);
					}

					// Low corner of the region of terrainChunk that crosses currentChunk.
					IntVector3 lowCorner = new IntVector3();
					lowCorner.x = (offset.x > currentChunkOrigin.x) ? offset.x : currentChunkOrigin.x;
					lowCorner.y = (offset.y > currentChunkOrigin.y) ? offset.y : currentChunkOrigin.y;
					lowCorner.z = (offset.z > currentChunkOrigin.z) ? offset.z : currentChunkOrigin.z;

					IntVector3 currentChunkHighCorner = currentChunkOrigin.add(RehashTerrainChunk.defaultSize);

					// High corner of the region of terrainChunk that crosses currentChunk.
					IntVector3 highCorner = offset.add(terrainChunkSize);
					highCorner.x = (highCorner.x < currentChunkHighCorner.x) ? highCorner.x : currentChunkHighCorner.x;
					highCorner.y = (highCorner.y < currentChunkHighCorner.y) ? highCorner.y : currentChunkHighCorner.y;
					highCorner.z = (highCorner.z < currentChunkHighCorner.z) ? highCorner.z : currentChunkHighCorner.z;

					// Copy the overlapping region of terrainChunk into currentChunk.
					for (int x = lowCorner.x; x < highCorner.x; x++)
						for (int y = lowCorner.y; y < highCorner.y; y++)
							for (int z = lowCorner.z; z < highCorner.z; z++)
							{
								IntVector3 location = new IntVector3(x, y, z).subtract(offset);
								Block block = terrainChunk.getBlock(location);
								Biome biome = terrainChunk.getBiome(location);

								location = new IntVector3(x, y, z).subtract(currentChunkOrigin);
								currentChunk.setBlock(location, block);
								currentChunk.setBiome(location, biome);
							}
				}

		return true;
	}

	@Override
	// Optimized for RehashTerrainChunks with a size of RehashTerrainChunk.defaultSize.
	public TerrainChunk getTerrainChunk(IntVector3 origin, IntVector3 size)
	{
		if (!this.regionExists(origin, size))
			return null;

		// Determine if the requested chunk is aligned with the boundaries of the other chunks in the terrain.
		boolean chunkAligned = origin.mod(RehashTerrainChunk.defaultSize).equals(new IntVector3(0, 0, 0));
		chunkAligned &= size.equals(RehashTerrainChunk.defaultSize);

		// If so, simply return the chunk at origin.
		if (chunkAligned)
			return this.chunks.get(origin);

		// Otherwise...
		TerrainChunk newChunk = new RehashTerrainChunk(size);

		// Determine the number of chunks crossed by the requested chunk along each axis.
		int chunkSpanX = 1 + (int)Math.floor((double)(origin.x + size.x - 1) / RehashTerrainChunk.defaultSize.x) - (int)Math.floor((double)origin.x / RehashTerrainChunk.defaultSize.x);
		int chunkSpanY = 1 + (int)Math.floor((double)(origin.y + size.y - 1) / RehashTerrainChunk.defaultSize.y) - (int)Math.floor((double)origin.y / RehashTerrainChunk.defaultSize.y);
		int chunkSpanZ = 1 + (int)Math.floor((double)(origin.z + size.z - 1) / RehashTerrainChunk.defaultSize.z) - (int)Math.floor((double)origin.z / RehashTerrainChunk.defaultSize.z);

		// The origin of the lowest chunk crossed by the requested chunk.
		IntVector3 startOrigin = this.getContainingChunkOrigin(origin);

		// For each chunk crossed by the requested chunk...
		for (int chunkX = 0; chunkX < chunkSpanX; chunkX++)
			for (int chunkY = 0; chunkY < chunkSpanY; chunkY++)
				for (int chunkZ = 0; chunkZ < chunkSpanZ; chunkZ++)
				{
					IntVector3 currentChunkOrigin = new IntVector3(chunkX, chunkY, chunkZ).multiply(RehashTerrainChunk.defaultSize).add(startOrigin);
					TerrainChunk currentChunk = this.chunks.get(currentChunkOrigin);

					// Shouldn't happen, but just in case.
					if (currentChunk == null)
						return null;

					// Low corner of the region of the requested chunk that crosses currentChunk.
					IntVector3 lowCorner = new IntVector3();
					lowCorner.x = (origin.x > currentChunkOrigin.x) ? origin.x : currentChunkOrigin.x;
					lowCorner.y = (origin.y > currentChunkOrigin.y) ? origin.y : currentChunkOrigin.y;
					lowCorner.z = (origin.z > currentChunkOrigin.z) ? origin.z : currentChunkOrigin.z;

					IntVector3 currentChunkHighCorner = currentChunkOrigin.add(RehashTerrainChunk.defaultSize);

					// High corner of the region of the requested chunk that crosses currentChunk.
					IntVector3 highCorner = origin.add(size);
					highCorner.x = (highCorner.x < currentChunkHighCorner.x) ? highCorner.x : currentChunkHighCorner.x;
					highCorner.y = (highCorner.y < currentChunkHighCorner.y) ? highCorner.y : currentChunkHighCorner.y;
					highCorner.z = (highCorner.z < currentChunkHighCorner.z) ? highCorner.z : currentChunkHighCorner.z;

					// Copy the overlapping region of currentChunk into the requested chunk.
					for (int x = lowCorner.x; x < highCorner.x; x++)
						for (int y = lowCorner.y; y < highCorner.y; y++)
							for (int z = lowCorner.z; z < highCorner.z; z++)
							{
								IntVector3 location = new IntVector3(x, y, z).subtract(currentChunkOrigin);
								Block block = currentChunk.getBlock(location);
								Biome biome = currentChunk.getBiome(location);

								location = new IntVector3(x, y, z).subtract(origin);
								newChunk.setBlock(location, block);
								newChunk.setBiome(location, biome);
							}
				}
		return newChunk;
	}

	@Override
	public boolean updateBlock(IntVector3 location)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateBlockNeighbors(IntVector3 location)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void scheduleUpdateBlock(IntVector3 location, int ticksFromNow)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void unscheduleUpdateBlock(IntVector3 location)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setBiome(IntVector3 location, Biome biome)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Biome getBiome(IntVector3 location)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean regionExists(IntVector3 origin, IntVector3 size)
	{
		if (origin == null || size == null)
			return false;

		// Determine the number of chunks crossed by the requested chunk along each axis.
		int chunkSpanX = 1 + (int)Math.floor((double)(origin.x + size.x - 1) / RehashTerrainChunk.defaultSize.x) - (int)Math.floor((double)origin.x / RehashTerrainChunk.defaultSize.x);
		int chunkSpanY = 1 + (int)Math.floor((double)(origin.y + size.y - 1) / RehashTerrainChunk.defaultSize.y) - (int)Math.floor((double)origin.y / RehashTerrainChunk.defaultSize.y);
		int chunkSpanZ = 1 + (int)Math.floor((double)(origin.z + size.z - 1) / RehashTerrainChunk.defaultSize.z) - (int)Math.floor((double)origin.z / RehashTerrainChunk.defaultSize.z);

		// The origin of the lowest chunk crossed by the requested chunk.
		IntVector3 startOrigin = this.getContainingChunkOrigin(origin);

		// For each chunk crossed by the requested chunk...
		for (int chunkX = 0; chunkX < chunkSpanX; chunkX++)
			for (int chunkY = 0; chunkY < chunkSpanY; chunkY++)
				for (int chunkZ = 0; chunkZ < chunkSpanZ; chunkZ++)
				{
					IntVector3 currentChunkOrigin = new IntVector3(chunkX, chunkY, chunkZ).multiply(RehashTerrainChunk.defaultSize).add(startOrigin);
					TerrainChunk currentChunk = this.chunks.get(currentChunkOrigin);

					if (currentChunk == null)
						return false;
				}

		return true;
	}

	@Override
	public void update(World world)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Terrain newTerrain(Properties settings)
	{
		return new RehashTerrain(settings);
	}

	private IntVector3 getContainingChunkOrigin(IntVector3 location)
	{
		IntVector3 chunkOrigin = new IntVector3();
		chunkOrigin.x = RehashTerrainChunk.defaultSize.x * (int)Math.floor((double)location.x / RehashTerrainChunk.defaultSize.x);
		chunkOrigin.y = RehashTerrainChunk.defaultSize.y * (int)Math.floor((double)location.y / RehashTerrainChunk.defaultSize.y);
		chunkOrigin.z = RehashTerrainChunk.defaultSize.z * (int)Math.floor((double)location.z / RehashTerrainChunk.defaultSize.z);
		return chunkOrigin;
	}

}
