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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import recraft.core.Creatable.CreatableException;
import recraft.networkinterface.UDPNetworkInterface;
import recraft.networknode.MinecraftServer;

public class Configurator
{
	static
	{
		try
		{
			ConfiguratorSelect select = null;

			select = Configurator.addSelect("Options.Network.Network Interface");
			select.addItem(new ConfiguratorCreatable("UDP-Based Interface", UDPNetworkInterface.class));

			select = Configurator.addSelect("Options.Network.Network Nodes.Client");
			//select.addItem(new ConfiguratorCreatable("Minecraft Client", MinecraftClient.class));

			select = Configurator.addSelect("Options.Network.Network Nodes.Server");
			select.addItem(new ConfiguratorCreatable("Minecraft Server", MinecraftServer.class));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static HashMap<String, Object> configuration = new HashMap<String, Object>();

	public static ConfiguratorSelect addSelect(String path)
	{
		// Delimit around "."
		String[] splitPath = path.split("\\.");

		// Path must contain a top level group
		if (splitPath.length <= 1)
			return null;

		ConfiguratorGroup containingGroup = getContainingGroup(splitPath);

		String selectName = splitPath[splitPath.length - 1];

		if (containingGroup.items.containsKey(selectName))
		{
			Object object = containingGroup.items.get(selectName);
			if (object instanceof ConfiguratorSelect)
				return (ConfiguratorSelect)object;
			else
				return null;
		}
		else
		{
			ConfiguratorSelect select = new ConfiguratorSelect(selectName, new ArrayList<Object>());
			containingGroup.items.put(selectName, select);
			return select;
		}
	}

	public static Object get(String path)
	{
		String[] splitPath = path.split("\\.");
		ConfiguratorGroup containingGroup = getContainingGroup(splitPath);

		HashMap<String, Object> containingMap = null;

		if (containingGroup == null)
			containingMap = configuration;
		else
			containingMap = containingGroup.items;

		return containingMap.get(splitPath[splitPath.length - 1]);
	}

	private static ConfiguratorGroup getContainingGroup(String[] splitPath)
	{
		// Traverse the configurations until right before the last path segment
		ConfiguratorGroup currentGroup = null;
		for (int index = 0; index < splitPath.length - 1; index++)
		{
			String currentPath = splitPath[index];
			HashMap<String, Object> currentMap = null;
			if (currentGroup == null)
			{
				currentMap = configuration;
			}
			else
			{
				currentMap = currentGroup.items;
			}

			if (currentMap.containsKey(currentPath))
			{
				currentGroup = (ConfiguratorGroup)currentMap.get(currentPath);
			}
			else
			{
				currentGroup = new ConfiguratorGroup(currentPath, new HashMap<String, Object>());
				currentMap.put(currentPath, currentGroup);
			}
		}
		return currentGroup;
	}

	public static class ConfiguratorGroup
	{
		public final String name;
		public final HashMap<String, Object> items;

		public ConfiguratorGroup(String name, HashMap<String, Object> items)
		{
			this.name = name;
			this.items = items;
		}

		@Override
		public String toString()
		{
			return this.name;
		}
	}

	public static class ConfiguratorSelect
	{
		public final String name;
		public final ArrayList<Object> items;

		public int defaultItem;
		public int selectedItem;

		public ConfiguratorSelect(String name, ArrayList<Object> items)
		{
			this.name = name;
			this.items = items;

			this.defaultItem = 0;
			this.selectedItem = -1;
		}

		public boolean select(int item)
		{
			if (item < 0 || item >= this.items.size())
				return false;

			this.selectedItem = item;
			return true;
		}

		public Object getSelected()
		{
			if (this.items.size() == 0)
				return null;

			if (this.selectedItem == -1)
				return this.items.get(this.defaultItem);
			else
				return this.items.get(this.selectedItem);
		}

		public void addItem(Object item)
		{
			this.items.add(item);
		}

		@Override
		public String toString()
		{
			return this.name;
		}
	}

	public static class ConfiguratorCreatable
	{
		private String name;
		private Class creatable;

		public ConfiguratorCreatable(String name, Class creatable) throws CreatableException
		{
			if (!Creatable.class.isAssignableFrom(creatable))
				throw new CreatableException("Class does not implement the Creatable interface");

			this.name = name;
			this.creatable = creatable;
		}

		public Object create(Object[] parameters)
		{
			// Extract class objects from parameters
			Class[] parameterClasses = new Class[parameters.length];
			for (int index = 0; index < parameters.length; index++)
				parameterClasses[index] = parameters[index].getClass();

			try
			{
				// Get the creatable object's create(...) method
				Method createMethod = this.creatable.getDeclaredMethod("create", parameterClasses);

				// Use it
				return createMethod.invoke(null, parameters);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public String toString()
		{
			return this.name;
		}
	}

	// TODO Add other input types like integer ranges, float ranges, string input, boolean, check group, etc.
}
