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

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class NetworkNodeIdentifier implements Serializable
{
	private static final long serialVersionUID = 419254874403174733L;
	
	public final InetAddress inetAddress;
	public final int port;

	public NetworkNodeIdentifier(String hostName, int port) throws UnknownHostException
	{
		this(InetAddress.getByName(hostName), port);
	}

	public NetworkNodeIdentifier(InetAddress inetAddress, int port)
	{
		this.inetAddress = inetAddress;
		this.port = port;
	}

	@Override
	public boolean equals(Object b)
	{
		if (this == b)
			return true;
		if (b == null)
			return false;
		if (this.getClass() != b.getClass())
			return false;

		NetworkNodeIdentifier c = (NetworkNodeIdentifier)b;

		if (!this.inetAddress.equals(c.inetAddress))
			return false;
		if (this.port != c.port)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		// 17 is an odd prime.
		return 17 * this.inetAddress.hashCode() + this.port;
	}

	@Override
	public String toString()
	{
		return String.format("%s:%d", this.inetAddress.toString(), this.port);
	}
}
