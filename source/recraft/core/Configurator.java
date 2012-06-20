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
	private static HashMap<String, Object> configuration = new HashMap<String, Object>();

	static
	{
		try
		{
			ConfiguratorSelect select = null;
			ConfiguratorIntRange intRange = null;

			select = Configurator.addSelect("Options.Network.Network Interface");
			select.addItem(new ConfiguratorCreatable("UDP-Based Interface", UDPNetworkInterface.class));

			select = Configurator.addSelect("Options.Network.Network Nodes.Client");
			//select.addItem(new ConfiguratorCreatable("Minecraft Client", MinecraftClient.class));

			select = Configurator.addSelect("Options.Network.Network Nodes.Server.Node Type");
			select.addItem(new ConfiguratorCreatable("Minecraft Server", MinecraftServer.class));

			intRange = Configurator.addIntRange("Options.Network.Network Nodes.Server.Bind Port", 1024, 65535, 25565);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static ConfiguratorSelect addSelect(String path)
	{
		// Delimit around "."
		String[] splitPath = path.split("\\.");

		// Path must contain a top level group
		if (splitPath.length <= 1)
			return null;

		ConfiguratorGroup containingGroup = getContainingGroup(splitPath);

		String name = splitPath[splitPath.length - 1];

		if (containingGroup.items.containsKey(name))
		{
			Object object = containingGroup.items.get(name);
			if (object instanceof ConfiguratorSelect)
				return (ConfiguratorSelect)object;
			else
				return null;
		}
		else
		{
			ConfiguratorSelect select = new ConfiguratorSelect(name, new ArrayList<Object>());
			containingGroup.items.put(name, select);
			return select;
		}
	}

	public static ConfiguratorIntRange addIntRange(String path, int rangeLow, int rangeHigh, int defaultValue)
	{
		String[] splitPath = path.split("\\.");
		if (splitPath.length <= 1)
			return null;

		ConfiguratorGroup containingGroup = getContainingGroup(splitPath);

		String name = splitPath[splitPath.length - 1];

		if (containingGroup.items.containsKey(name))
		{
			Object object = containingGroup.items.get(name);
			if (object instanceof ConfiguratorIntRange)
				return (ConfiguratorIntRange)object;
			else
				return null;
		}
		else
		{
			ConfiguratorIntRange intRange = new ConfiguratorIntRange(name, rangeLow, rangeHigh, defaultValue);
			containingGroup.items.put(name, intRange);
			return intRange;
		}
	}

	public static ConfiguratorFloatRange addFloatRange(String path, float rangeLow, float rangeHigh, float defaultValue)
	{
		String[] splitPath = path.split("\\.");
		if (splitPath.length <= 1)
			return null;

		ConfiguratorGroup containingGroup = getContainingGroup(splitPath);

		String name = splitPath[splitPath.length - 1];

		if (containingGroup.items.containsKey(name))
		{
			Object object = containingGroup.items.get(name);
			if (object instanceof ConfiguratorFloatRange)
				return (ConfiguratorFloatRange)object;
			else
				return null;
		}
		else
		{
			ConfiguratorFloatRange floatRange = new ConfiguratorFloatRange(name, rangeLow, rangeHigh, defaultValue);
			containingGroup.items.put(name, floatRange);
			return floatRange;
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

		public int defaultValue;
		public int currentValue;

		public ConfiguratorSelect(String name, ArrayList<Object> items)
		{
			this.name = name;
			this.items = items;

			this.defaultValue = 0;
			this.currentValue = -1;
		}

		public boolean set(int item)
		{
			if (item < 0 || item >= this.items.size())
				return false;

			this.currentValue = item;
			return true;
		}

		public Object getValue()
		{
			if (this.items.size() == 0)
				return null;

			if (this.currentValue == -1)
				return this.items.get(this.defaultValue);
			else
				return this.items.get(this.currentValue);
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
			Class[] parameterClasses = null;
			if (parameters != null)
			{
				parameterClasses = new Class[parameters.length];
				for (int index = 0; index < parameters.length; index++)
					parameterClasses[index] = parameters[index].getClass();
			}

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

	public static class ConfiguratorIntRange
	{
		public final String name;
		public final int rangeLow;
		public final int rangeHigh;

		public int defaultValue;
		public int currentValue;

		public ConfiguratorIntRange(String name, int rangeLow, int rangeHigh, int defaultValue)
		{
			this.name = name;
			this.rangeLow = rangeLow;
			this.rangeHigh = rangeHigh;
			this.defaultValue = defaultValue;
			this.currentValue  = -1;
		}

		public boolean set(int value)
		{
			if (value < this.rangeLow || value > this.rangeHigh)
				return false;

			this.currentValue = value;
			return true;
		}

		public int getValue()
		{
			if (this.currentValue == -1)
				return this.defaultValue;

			return this.currentValue;
		}
	}

	public static class ConfiguratorFloatRange
	{
		public final String name;
		public final float rangeLow;
		public final float rangeHigh;

		public float defaultValue;
		public float currentValue;

		public ConfiguratorFloatRange(String name, float rangeLow, float rangeHigh, float defaultValue)
		{
			this.name = name;
			this.rangeLow = rangeLow;
			this.rangeHigh = rangeHigh;
			this.defaultValue = defaultValue;
			this.currentValue  = -1;
		}

		public boolean set(float value)
		{
			if (value < this.rangeLow || value > this.rangeHigh)
				return false;

			this.currentValue = value;
			return true;
		}

		public float getValue()
		{
			if (this.currentValue == -1)
				return this.defaultValue;

			return this.currentValue;
		}
	}

	// TODO Add other input types like string input, boolean, check group, etc.
}
