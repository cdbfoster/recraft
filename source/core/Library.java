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

public interface Library
{
	void registerBiomes(boolean overwriteExisting);
	void registerBiome(int id, boolean overwriteExisting);

	void registerBlocks(boolean overwriteExisting);
	void registerBlock(int id, boolean overwriteExisting);

	void registerRenderers(boolean overwriteExisting);
	void registerRenderer(int id, boolean overwriteExisting);

	void registerTerrains(boolean overwriteExisting);
	void registerTerrain(int id, boolean overwriteExisting);

	void registerTerrainChunks(boolean overwriteExisting);
	void registerTerrainChunk(int id, boolean overwriteExisting);

	void registerTerrainProviders(boolean overwriteExisting);
	void registerTerrainProvider(int id, boolean overwriteExisting);

	void registerWorlds(boolean overwriteExisting);
	void registerWorld(int id, boolean overwriteExisting);
}
