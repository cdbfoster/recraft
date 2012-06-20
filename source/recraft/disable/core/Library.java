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

import java.util.HashMap;
import recraft.util.AutoArrayList;

public abstract class Library
{
	public static final int SET_BIOME = 0, SET_BLOCK = 1, SET_RENDERER = 2, SET_TERRAIN = 3, SET_TERRAIN_PROVIDER = 4, SET_WORLD = 5;
	public static final AutoArrayList<String> knownSetNames = new AutoArrayList<String>(8);
	static
	{
		knownSetNames.set(SET_BIOME, "Biomes");
		knownSetNames.set(SET_BLOCK, "Blocks");
		knownSetNames.set(SET_RENDERER, "Renderers");
		knownSetNames.set(SET_TERRAIN, "Terrains");
		knownSetNames.set(SET_TERRAIN_PROVIDER, "Terrain Providers");
		knownSetNames.set(SET_WORLD, "Worlds");
	}

	protected final HashMap<String, LibrarySet> librarySets;

	public Library()
	{
		this.librarySets = new HashMap<String, LibrarySet>();
	}

	// TODO Not all of these register/map methods may be necessary.  Cull at a later time.
	public boolean registerSet(String setName, boolean overwriteExisting)
	{
		LibrarySet librarySet = this.librarySets.get(setName);
		if (librarySet == null)
			return false;

		this.conditionalWrite(librarySet.registry, librarySet.target, overwriteExisting);
		return true;
	}

	public boolean registerItem(String setName, int sourceIndex, boolean overwriteExisting)
	{
		LibrarySet librarySet = this.librarySets.get(setName);
		if (librarySet == null)
			return false;

		this.conditionalWrite(librarySet.registry, sourceIndex, librarySet.target, sourceIndex, overwriteExisting);
		return true;
	}

	public boolean mapItem(String setName, int sourceIndex, int destinationIndex, boolean overwriteExisting)
	{
		LibrarySet librarySet = this.librarySets.get(setName);
		if (librarySet == null)
			return false;

		this.conditionalWrite(librarySet.registry, sourceIndex, librarySet.target, destinationIndex, overwriteExisting);
		return true;
	}

	private boolean targetExists(AutoArrayList list, int index)
	{
		return (list.get(index) != null);
	}

	private void conditionalWrite(AutoArrayList source, AutoArrayList destination, boolean overwriteExisting)
	{
		int size = source.size();
		for (int index = 0; index < size; index++)
		{
			if (overwriteExisting)
				destination.set(index, source.get(index));
			else
				if (!this.targetExists(destination, index))
					destination.set(index, source.get(index));
		}
	}

	private void conditionalWrite(AutoArrayList source, int sourceIndex, AutoArrayList destination, int destinationIndex, boolean overwriteExisting)
	{
		if (overwriteExisting)
			destination.set(destinationIndex, source.get(sourceIndex));
		else
			if (!this.targetExists(destination, sourceIndex))
				destination.set(destinationIndex, source.get(sourceIndex));
	}

	protected class LibrarySet<E>
	{
		private final AutoArrayList<E> registry;
		private final AutoArrayList<E> target;

		public LibrarySet(AutoArrayList<E> registry, AutoArrayList<E> target)
		{
			this.registry = registry;
			this.target = target;
		}

		public E get(int index)
		{
			return registry.get(index);
		}

		public E set(int index, E element)
		{
			return registry.set(index, element);
		}
	}
}
