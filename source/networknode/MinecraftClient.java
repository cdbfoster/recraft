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

package recraft.networknode;

import recraft.core.Configurator;
import recraft.core.Creatable;
import recraft.core.NetworkInterface;
import recraft.core.NetworkNode;
import recraft.core.Configurator.ConfiguratorCreatable;
import recraft.core.Configurator.ConfiguratorSelect;

public class MinecraftClient extends NetworkNode implements Creatable
{
	public static MinecraftClient create()
	{
		return new MinecraftClient();
	}

	public MinecraftClient()
	{
		super();

		ConfiguratorSelect interfaceSelect = (ConfiguratorSelect)Configurator.get("Options.Network.Network Interface");
		ConfiguratorCreatable interfaceCreator= (ConfiguratorCreatable)interfaceSelect.getValue();

		this.networkInterface = (NetworkInterface)interfaceCreator.create(null);
	}

	public MinecraftClient(NetworkInterface networkInterface)
	{
		this.networkInterface = networkInterface;
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

	}

}
