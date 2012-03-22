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

import recraft.util.IntVector3;

public interface TerrainChunk
{
	boolean setBlock(IntVector3 location, Block block);
	Block getBlock(IntVector3 location);

	// Fix these: Do not work with IDs, work with objects that can tell you IDs.  And do not return bytes.
	byte setBiome(IntVector3 location, byte biomeID);
	byte getBiome(IntVector3 location);

	IntVector3 getOrigin();

	boolean containsLocation(IntVector3 location);

	int getSizeX();
	int getSizeY();
	int getSizeZ();
}
