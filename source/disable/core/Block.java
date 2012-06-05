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

import java.lang.String;

import recraft.util.AutoArrayList;
import recraft.util.IntVector3;

/** The block interface provides access to all common block features. */
public interface Block
{
	static final AutoArrayList<Block> registry = new AutoArrayList<Block>(256);

	/** Returns the block's ID number. */
	int getID();

	/** Updates the block.  The Terrain and IntVector3 arguments are used to determine the block's neighbors. */
	void update(Terrain terrain, IntVector3 location);

	/** Denotes whether or not the block should be updated each tick. */
	boolean requiresConstantUpdate();

	/** Denotes whether or not the block contains data extra to a simple ID number that needs to be stored. */
	boolean isSimpleBlock();

	/** Returns a new block of the same type. */
	Block newBlock();

	/** Returns a clone of the given block.  The given block should be of the same type as the owner of the cloneBlock method. */
	Block cloneBlock(Block block);
}
