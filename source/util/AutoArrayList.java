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

package recraft.util;

import java.util.ArrayList;

/** An auto-expanding ArrayList class.  Calls to the set method will call ArrayList's add method until the
 * list's size is large enough to contain the specified index. Calls to the get method that specify an index
 * greater than the list's size will not throw an exception, but instead return null. */
public class AutoArrayList<E> extends ArrayList<E>
{
	public AutoArrayList()
	{
		super();
	}

	public AutoArrayList(int initialCapacity)
	{
		super(initialCapacity);
	}

	@Override
	public E get(int index)
	{
		if (index >= super.size())
			return null;
		return super.get(index);
	}

	@Override
	public E set(int index, E element)
	{
		super.ensureCapacity(index + 1);

		while (super.size() <= index)
			super.add(null);

		return super.set(index, element);
	}
}
