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

public class Properties
{
	private HashMap<String, Object> properties;

	public boolean containsField(String fieldName)
	{
		return this.properties.containsKey(fieldName);
	}

	public Object getObject(String fieldName)
	{
		return this.properties.get(fieldName);
	}

	public int getInt(String fieldName)
	{
		Object object = this.properties.get(fieldName);
		if (object == null || !(object instanceof Integer))
			return 0;
		return ((Integer)object).intValue();
	}

	public float getFloat(String fieldName)
	{
		Object object = this.properties.get(fieldName);
		if (object == null || !(object instanceof Float))
			return 0.0f;
		return ((Float)object).floatValue();
	}

	public boolean getBoolean(String fieldName)
	{
		Object object = this.properties.get(fieldName);
		if (object == null || !(object instanceof Boolean))
			return false;
		return ((Boolean)object).booleanValue();
	}

	public String getString(String fieldName)
	{
		Object object = this.properties.get(fieldName);
		if (object == null || !(object instanceof String))
			return null;
		return (String)object;
	}

	public void setObject(String fieldName, Object value)
	{
		this.properties.put(fieldName, value);
	}

	public void setInt(String fieldName, int value)
	{
		this.properties.put(fieldName, new Integer(value));
	}

	public void setFloat(String fieldName, float value)
	{
		this.properties.put(fieldName, new Float(value));
	}

	public void setBoolean(String fieldName, boolean value)
	{
		this.properties.put(fieldName, new Boolean(value));
	}

	public void setString(String fieldName, String value)
	{
		this.properties.put(fieldName, value);
	}
}
