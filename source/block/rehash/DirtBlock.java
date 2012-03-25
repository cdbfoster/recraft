package recraft.block.rehash;

import recraft.core.Block;
import recraft.core.Terrain;
import recraft.util.IntVector3;

public class DirtBlock implements Block
{
	public static final int id = 3;

	@Override
	public int getID() { return id; }

	@Override
	public void update(Terrain terrain, IntVector3 location) { }

	@Override
	public boolean requiresConstantUpdate() { return false; }

	@Override
	public boolean isSimpleBlock() { return true; }

	@Override
	public Block newBlock() { return new DirtBlock(); }

	@Override
	public Block cloneBlock(Block block) { return this.newBlock(); }
}
