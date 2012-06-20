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

import recraft.util.AutoArrayList;
import recraft.util.IntVector3;

/** The TerrainProvider interface abstracts the source of a particular game world.  The source could be
 * anything: loaded from disk, generated on the fly, received from the network, etc. */
public interface TerrainProvider extends IDless
{
	public static final AutoArrayList<TerrainProvider> registry = new AutoArrayList<TerrainProvider>(2);

	/** Returns a TerrainChunk containing the terrain described by origin and size. */
	TerrainChunk provideTerrain(IntVector3 origin, IntVector3 size);

	TerrainProvider newTerrainProvider(Properties settings);
}
