package recraft.terrainprovider.rehash.generator;

import recraft.block.rehash.*;
import recraft.core.Settings;
import recraft.core.TerrainChunk;
import recraft.core.TerrainProvider;
import recraft.terrainchunk.rehash.RehashTerrainChunk;
import recraft.util.IntVector3;

public class RehashFlatWorldTerrainGenerator implements TerrainProvider
{
	public RehashFlatWorldTerrainGenerator(Settings settings)
	{

	}

	@Override
	public TerrainChunk provideTerrain(IntVector3 origin, IntVector3 size)
	{
		if (origin == null)
			origin = new IntVector3(0, 0, 0);
		if (size == null)
			size = new IntVector3(16, 256, 16);

		TerrainChunk chunk = new RehashTerrainChunk(origin, size);

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
	public TerrainProvider newTerrainProvider(Settings settings)
	{
		return new RehashFlatWorldTerrainGenerator(settings);
	}

}
