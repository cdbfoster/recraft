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

package recraft.terrainprovider.rehash;

import recraft.core.Settings;
import recraft.core.TerrainChunk;
import recraft.core.TerrainProvider;
import recraft.util.IntVector3;

public class RehashFlatWorldTerrainProvider implements TerrainProvider
{
	public RehashFlatWorldTerrainProvider()
	{

	}

	public RehashFlatWorldTerrainProvider(Settings settings)
	{

	}

	@Override
	public TerrainChunk provideTerrain(IntVector3 origin, IntVector3 size)
	{
		return null;
	}

	@Override
	public TerrainProvider newTerrainProvider(Settings settings)
	{
		return new RehashFlatWorldTerrainProvider(settings);
	}
}
