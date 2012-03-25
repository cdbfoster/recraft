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

package recraft.block.rehash;

import recraft.core.Block;
import recraft.core.Terrain;
import recraft.util.IntVector3;

public class StoneBlock implements Block
{
	public static final int id = 1;

	@Override
	public int getID() { return id; }

	@Override
	public void update(Terrain terrain, IntVector3 location) { }

	@Override
	public boolean requiresConstantUpdate() { return false; }

	@Override
	public boolean isSimpleBlock() { return true; }

	@Override
	public Block newBlock() { return new StoneBlock(); }

	@Override
	public Block cloneBlock(Block block) { return this.newBlock(); }
}
