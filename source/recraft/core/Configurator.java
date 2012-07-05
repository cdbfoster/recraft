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

import recraft.core.InputDevice.InputBinding;
import recraft.networkinterface.UDPNetworkInterface;
import recraft.networknode.MinecraftServer;

// TODO Make use of the Map interface to abstract map implementation.  Same goes for List.

public class Configurator
{
	private static HashMap<String, Object> configuration = new HashMap<String, Object>();

	static
	{
		try
		{
			ConfiguratorSelect select = null;

			select = Configurator.addSelect("Options.Input.Main Input Device");

			select = Configurator.addSelect("Options.Network.Network Interface");
			select.addItem(new ConfiguratorCreatable("UDP-Based Interface", UDPNetworkInterface.class));

			select = Configurator.addSelect("Options.Network.Network Nodes.Client.Node Type");
			//select.addItem(new ConfiguratorCreatable("Minecraft Client", MinecraftClient.class));

			Configurator.addLink("Options.Input.Main Input Device", "Options.Network.Network Nodes.Client.Input Device");


			select = Configurator.addSelect("Options.Network.Network Nodes.Server.Node Type");
			select.addItem(new ConfiguratorCreatable("Minecraft Server", MinecraftServer.class));

			Configurator.addIntRange("Options.Network.Network Nodes.Server.Bind Port", 1024, 65535, 25565);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static ConfiguratorSelect addSelect(String path)
	{
		ConfiguratorSelect select = new ConfiguratorSelect(getNameFromPath(path), new ArrayList<Object>());
		return (ConfiguratorSelect)addObject(path, select);
	}

	public static ConfiguratorIntRange addIntRange(String path, int rangeLow, int rangeHigh, int defaultValue)
	{
		ConfiguratorIntRange intRange = new ConfiguratorIntRange(getNameFromPath(path), rangeLow, rangeHigh, defaultValue);
		return (ConfiguratorIntRange)addObject(path, intRange);
	}

	public static ConfiguratorFloatRange addFloatRange(String path, float rangeLow, float rangeHigh, float defaultValue)
	{
		ConfiguratorFloatRange floatRange = new ConfiguratorFloatRange(getNameFromPath(path), rangeLow, rangeHigh, defaultValue);
		return (ConfiguratorFloatRange)addObject(path, floatRange);
	}

	public static boolean addLink(String source, String destination)
	{
		Object object = get(source);
		if (object == null)
			return false;

		if (addObject(destination, object) == null)
			return false;
		return true;
	}

	public static ConfiguratorInputDevice addInputDevice(String containingPath, ConfiguratorInputDevice inputDevice)
	{
		String fullPath = containingPath + "." + inputDevice.getName();
		return (ConfiguratorInputDevice)addObject(fullPath, inputDevice);
	}

	public static Object get(String path)
	{
		ConfiguratorGroup containingGroup = getContainingGroup(path);

		HashMap<String, Object> containingMap = null;

		if (containingGroup == null)
			containingMap = configuration;
		else
			containingMap = containingGroup.items;

		return containingMap.get(getNameFromPath(path));
	}

	private static ConfiguratorGroup getContainingGroup(String path)
	{
		String[] splitPath = path.split("\\.");
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

	private static String getNameFromPath(String path)
	{
		// Delimit around "."
		String[] splitPath = path.split("\\.");
		if (splitPath.length == 0)
			return "";

		return splitPath[splitPath.length - 1];
	}

	private static Object addObject(String path, Object newObject)
	{
		ConfiguratorGroup containingGroup = getContainingGroup(path);
		if (containingGroup == null)
			return null;

		String name = getNameFromPath(path);

		if (containingGroup.items.containsKey(name))
		{
			Object object = containingGroup.items.get(name);
			if (newObject.getClass().isInstance(object)) //if (object instanceof newObject.class)
				return object;
			else
				return null;
		}
		else
		{
			containingGroup.items.put(name, newObject);
			return newObject;
		}
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
			return this.name; // TODO Wrap all name returns in new Strings
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
		private Class<? extends Creatable> creatable;

		public ConfiguratorCreatable(String name, Class<? extends Creatable> creatable)
		{
			this.name = name;
			this.creatable = creatable;
		}

		public Object create(Object[] parameters)
		{
			// Extract class objects from parameters
			Class<?>[] parameterClasses = null;
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

	public static class ConfiguratorInputDevice extends ConfiguratorGroup
	{
		private InputDevice inputDevice;

		public ConfiguratorInputDevice(InputDevice inputDevice)
		{
			super(inputDevice.getName(), new HashMap<String, Object>());

			this.inputDevice = inputDevice;
		}

		public InputDevice getInputDevice()
		{
			return this.inputDevice;
		}

		public void addBinding(String name, InputBinding inputBinding)
		{
			this.items.put(name, new ConfiguratorInputBinding(this.inputDevice, inputBinding));
		}

		public ConfiguratorInputBinding getBinding(String name)
		{
			return (ConfiguratorInputBinding)this.items.get(name);
		}

		public String getName()
		{
			return this.inputDevice.getName();
		}

		@Override
		public String toString()
		{
			return this.getName();
		}

		public static class ConfiguratorInputBinding
		{
			private InputDevice inputDevice;
			private InputBinding inputBinding;

			public ConfiguratorInputBinding(InputDevice inputDevice, InputBinding inputBinding)
			{
				this.inputDevice = inputDevice;
				this.inputBinding = inputBinding;
			}

			public boolean receive()
			{
				return this.inputDevice.receiveBinding(this.inputBinding);
			}

			@Override
			public String toString()
			{
				return this.inputBinding.getBindingString();
			}
		}
	}

	// TODO Add other input types like string input, boolean, check group, etc.
}
