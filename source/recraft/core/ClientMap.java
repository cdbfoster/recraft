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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientMap<E extends Client>
{
	private Map<NetworkNodeIdentifier, E> clients;

	public ClientMap()
	{
		this.clients = new HashMap<NetworkNodeIdentifier, E>();
	}

	public E put(NetworkNodeIdentifier node, E client)
	{
		return this.clients.put(node, client);
	}

	public E remove(NetworkNodeIdentifier node)
	{
		return this.clients.remove(node);
	}

	public E get(NetworkNodeIdentifier node)
	{
		return this.clients.get(node);
	}

	public Collection<E> getClientList()
	{
		return this.clients.values();
	}
}
