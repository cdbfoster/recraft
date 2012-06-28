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

package recraft.inputdevice;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import math.Constants;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import recraft.core.Configurator;
import recraft.core.Configurator.ConfiguratorInputDevice;
import recraft.core.Configurator.ConfiguratorSelect;
import recraft.core.Creatable;
import recraft.core.Input;
import recraft.core.InputDevice;

public class LWJGLKeyboardMouse implements InputDevice, Creatable
{
	private List<MouseEvent> mouseEvents;
	private List<KeyboardEvent> keyboardEvents;

	private InputBinding bindings[];
	private static enum BindingType {MOVE_FORWARD, MOVE_LEFT, MOVE_BACKWARD, MOVE_RIGHT,
		LOOK_UP, LOOK_LEFT, LOOK_DOWN, LOOK_RIGHT,
		ATTACK, USE, INVENTORY, JUMP, RUN, CROUCH, DROP, CHAT, ESCAPE, PICK_BLOCK, LIST_PLAYERS, ZOOM,
		SELECT_ITEM_ABS_1, SELECT_ITEM_ABS_2, SELECT_ITEM_ABS_3, SELECT_ITEM_ABS_4, SELECT_ITEM_ABS_5, SELECT_ITEM_ABS_6, SELECT_ITEM_ABS_7, SELECT_ITEM_ABS_8, SELECT_ITEM_ABS_9,
		SELECT_ITEM_REL_UP, SELECT_ITEM_REL_DOWN}

	private boolean isReceiving;

	public static LWJGLKeyboardMouse create()
	{
		return new LWJGLKeyboardMouse();
	}

	public LWJGLKeyboardMouse()
	{
		this.mouseEvents = new LinkedList<MouseEvent>();
		this.keyboardEvents = new LinkedList<KeyboardEvent>();

		this.bindings = new InputBinding[]
				{/*MOVE_FORWARD*/new DirectionBinding(DirectionBinding.Type.KEY, Keyboard.KEY_W, this.mouseEvents, this.keyboardEvents),
				 /*MOVE_LEFT*/new DirectionBinding(DirectionBinding.Type.KEY, Keyboard.KEY_A, this.mouseEvents, this.keyboardEvents),
				 /*MOVE_BACKWARD*/new DirectionBinding(DirectionBinding.Type.KEY, Keyboard.KEY_S, this.mouseEvents, this.keyboardEvents),
				 /*MOVE_RIGHT*/new DirectionBinding(DirectionBinding.Type.KEY, Keyboard.KEY_D, this.mouseEvents, this.keyboardEvents),

				 /*LOOK_UP*/new DirectionBinding(DirectionBinding.Type.MOUSE_AXIS_Y, 1, this.mouseEvents, this.keyboardEvents),
				 /*LOOK_LEFT*/new DirectionBinding(DirectionBinding.Type.MOUSE_AXIS_X, -1, this.mouseEvents, this.keyboardEvents),
				 /*LOOK_DOWN*/new DirectionBinding(DirectionBinding.Type.MOUSE_AXIS_Y, -1, this.mouseEvents, this.keyboardEvents),
				 /*LOOK_RIGHT*/new DirectionBinding(DirectionBinding.Type.MOUSE_AXIS_X, 1, this.mouseEvents, this.keyboardEvents),

				 /*ATTACK*/new KeyBinding(KeyBinding.Type.MOUSE, 0, this.mouseEvents, this.keyboardEvents),
				 /*USE*/new KeyBinding(KeyBinding.Type.MOUSE, 1, this.mouseEvents, this.keyboardEvents),
				 /*INVENTORY*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_E, this.mouseEvents, this.keyboardEvents),
				 /*JUMP*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_SPACE, this.mouseEvents, this.keyboardEvents),
				 /*RUN*/null, // We'll set this right after initialization
				 /*CROUCH*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_LSHIFT, this.mouseEvents, this.keyboardEvents),
				 /*DROP*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_Q, this.mouseEvents, this.keyboardEvents),
				 /*CHAT*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_T, this.mouseEvents, this.keyboardEvents),
				 /*ESCAPE*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_ESCAPE, this.mouseEvents, this.keyboardEvents),
				 /*PICK_BLOCK*/new KeyBinding(KeyBinding.Type.MOUSE, 2, this.mouseEvents, this.keyboardEvents),
				 /*LIST_PLAYERS*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_TAB, this.mouseEvents, this.keyboardEvents),
				 /*ZOOM*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_LCONTROL, this.mouseEvents, this.keyboardEvents),

				 /*SELECT_ITEM_ABS_1*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_1, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_ABS_2*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_2, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_ABS_3*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_3, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_ABS_4*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_4, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_ABS_5*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_5, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_ABS_6*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_6, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_ABS_7*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_7, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_ABS_8*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_8, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_ABS_9*/new KeyBinding(KeyBinding.Type.KEY, Keyboard.KEY_9, this.mouseEvents, this.keyboardEvents),

				 /*SELECT_ITEM_REL_UP*/new DirectionBinding(DirectionBinding.Type.MOUSE_AXIS_Z, 1, this.mouseEvents, this.keyboardEvents),
				 /*SELECT_ITEM_REL_UP*/new DirectionBinding(DirectionBinding.Type.MOUSE_AXIS_Z, -1, this.mouseEvents, this.keyboardEvents)};
		this.bindings[BindingType.RUN.ordinal()] = new DoubleTapBinding(this.bindings[BindingType.MOVE_FORWARD.ordinal()], 350);

		this.isReceiving = false;

		ConfiguratorSelect select = (ConfiguratorSelect)Configurator.get("Options.Input.Main Input Device");
		select.addItem(this.getName());

		ConfiguratorInputDevice config = Configurator.addInputDevice("Options.Input.Input Devices", new ConfiguratorInputDevice(this));
		config.addBinding("Attack", this.bindings[BindingType.ATTACK.ordinal()]);
		config.addBinding("Use Item", this.bindings[BindingType.USE.ordinal()]);
		config.addBinding("Forward", this.bindings[BindingType.MOVE_FORWARD.ordinal()]);
		config.addBinding("Left", this.bindings[BindingType.MOVE_LEFT.ordinal()]);
		config.addBinding("Back", this.bindings[BindingType.MOVE_BACKWARD.ordinal()]);
		config.addBinding("Right", this.bindings[BindingType.MOVE_RIGHT.ordinal()]);
		config.addBinding("Jump", this.bindings[BindingType.JUMP.ordinal()]);
		config.addBinding("Sneak", this.bindings[BindingType.CROUCH.ordinal()]);
		config.addBinding("Drop", this.bindings[BindingType.DROP.ordinal()]);
		config.addBinding("Inventory", this.bindings[BindingType.INVENTORY.ordinal()]);
		config.addBinding("Chat", this.bindings[BindingType.CHAT.ordinal()]);
		config.addBinding("List Players", this.bindings[BindingType.LIST_PLAYERS.ordinal()]);
		config.addBinding("Pick Block", this.bindings[BindingType.PICK_BLOCK.ordinal()]);
		config.addBinding("Zoom", this.bindings[BindingType.ZOOM.ordinal()]);
	}

	@Override
	public String getName()
	{
		return "Keyboard and Mouse (LWJGL)";
	}

	@Override
	public Input getInputState()
	{
		if (!this.isValid() || this.isReceiving)
			return null;

		try
		{
			Display.processMessages();

			this.mouseEvents.clear();
			this.keyboardEvents.clear();
			// Read out keyboard and mouse events for the bindings to look at.
			while (Mouse.next())
				this.mouseEvents.add(new MouseEvent()); // All info is taken from the current event during construction
			while (Keyboard.next())
				this.keyboardEvents.add(new KeyboardEvent()); // All info is taken from the current event during construction
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Input input = new Input();

		// Movement
		{
			input.setActive(Input.Type.MOVE_DIRECTION, false);

			float forward = ((Float)this.bindings[BindingType.MOVE_FORWARD.ordinal()].getValue()).floatValue();
			float right = ((Float)this.bindings[BindingType.MOVE_RIGHT.ordinal()].getValue()).floatValue();
			float backward = ((Float)this.bindings[BindingType.MOVE_BACKWARD.ordinal()].getValue()).floatValue();
			float left = ((Float)this.bindings[BindingType.MOVE_LEFT.ordinal()].getValue()).floatValue();

			if (Math.abs(forward + right + backward + left) > Constants.FLT_EPSILON)
			{
				float x = right - left;
				float y = forward - backward;

				input.setActive(Input.Type.MOVE_DIRECTION, true);
				input.setDirection(Input.Type.MOVE_DIRECTION, x, y);
			}
		}

		// Look
		{
			input.setActive(Input.Type.LOOK_DIRECTION, false);

			float up = ((Float)this.bindings[BindingType.LOOK_UP.ordinal()].getValue()).floatValue();
			float right = ((Float)this.bindings[BindingType.LOOK_RIGHT.ordinal()].getValue()).floatValue();
			float down = ((Float)this.bindings[BindingType.LOOK_DOWN.ordinal()].getValue()).floatValue();
			float left = ((Float)this.bindings[BindingType.LOOK_LEFT.ordinal()].getValue()).floatValue();

			if (Math.abs(up + right + down + left) > Constants.FLT_EPSILON)
			{
				float x = right - left;
				float y = up - down;

				input.setActive(Input.Type.LOOK_DIRECTION, true);
				input.setDirection(Input.Type.LOOK_DIRECTION, x, y);
			}
		}

		// Select Item: Relative
		{
			input.setActive(Input.Type.SELECT_ITEM_REL, false);

			float up = ((Float)this.bindings[BindingType.SELECT_ITEM_REL_UP.ordinal()].getValue()).floatValue();
			float down = ((Float)this.bindings[BindingType.SELECT_ITEM_REL_DOWN.ordinal()].getValue()).floatValue();

			if (Math.abs(up + down) > Constants.FLT_EPSILON)
			{
				input.setActive(Input.Type.SELECT_ITEM_REL, true);
				input.setValue(Input.Type.SELECT_ITEM_REL, (int)(up - down));
			}
		}

		// Select Item: Absolute
		{
			boolean slots[] = {this.bindings[BindingType.SELECT_ITEM_ABS_1.ordinal()].isActive(),
							   this.bindings[BindingType.SELECT_ITEM_ABS_2.ordinal()].isActive(),
							   this.bindings[BindingType.SELECT_ITEM_ABS_3.ordinal()].isActive(),
							   this.bindings[BindingType.SELECT_ITEM_ABS_4.ordinal()].isActive(),
							   this.bindings[BindingType.SELECT_ITEM_ABS_5.ordinal()].isActive(),
							   this.bindings[BindingType.SELECT_ITEM_ABS_6.ordinal()].isActive(),
							   this.bindings[BindingType.SELECT_ITEM_ABS_7.ordinal()].isActive(),
							   this.bindings[BindingType.SELECT_ITEM_ABS_8.ordinal()].isActive(),
							   this.bindings[BindingType.SELECT_ITEM_ABS_9.ordinal()].isActive()};
			for (int i = 0; i < slots.length; i++)
				if (slots[i] == true)
				{
					input.setActive(Input.Type.SELECT_ITEM_ABS, true);
					input.setValue(Input.Type.SELECT_ITEM_ABS, i + 1);
					break;
				}
		}

		input.setActive(Input.Type.ATTACK, this.bindings[BindingType.ATTACK.ordinal()].isActive());
		input.setActive(Input.Type.USE, this.bindings[BindingType.USE.ordinal()].isActive());
		input.setActive(Input.Type.INVENTORY, this.bindings[BindingType.INVENTORY.ordinal()].isActive());
		input.setActive(Input.Type.JUMP, this.bindings[BindingType.JUMP.ordinal()].isActive());
		input.setActive(Input.Type.RUN, this.bindings[BindingType.RUN.ordinal()].isActive());
		input.setActive(Input.Type.CROUCH, this.bindings[BindingType.CROUCH.ordinal()].isActive());
		input.setActive(Input.Type.DROP, this.bindings[BindingType.DROP.ordinal()].isActive());
		input.setActive(Input.Type.CHAT, this.bindings[BindingType.CHAT.ordinal()].isActive());
		input.setActive(Input.Type.ESCAPE, this.bindings[BindingType.ESCAPE.ordinal()].isActive());
		input.setActive(Input.Type.PICK_BLOCK, this.bindings[BindingType.PICK_BLOCK.ordinal()].isActive());
		input.setActive(Input.Type.LIST_PLAYERS, this.bindings[BindingType.LIST_PLAYERS.ordinal()].isActive());
		input.setActive(Input.Type.ZOOM, this.bindings[BindingType.ZOOM.ordinal()].isActive());

		return input;
	}

	@Override
	public boolean receiveBinding(InputBinding binding)
	{
		if (!this.isValid() || this.isReceiving)
			return false;

		this.isReceiving = true;

		int index = -1;
		for (int i = 0; i < this.bindings.length; i++)
			if (this.bindings[i] == binding)
				index = i;
		if (index == -1)
			return false; // This input device does not contain the given binding.

		do
		{
			try
			{
				Display.processMessages();

				this.mouseEvents.clear();
				this.keyboardEvents.clear();
				// Read out keyboard and mouse events for the bindings to look at.
				while (Mouse.next())
					this.mouseEvents.add(new MouseEvent()); // All info is taken from the current event during construction
				while (Keyboard.next())
					this.keyboardEvents.add(new KeyboardEvent()); // All info is taken from the current event during construction
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			boolean breakOut = false;
			ListIterator<KeyboardEvent> eventIterator = this.keyboardEvents.listIterator();
			while (eventIterator.hasNext())
				if (eventIterator.next().key == Keyboard.KEY_ESCAPE)
				{
					breakOut = true;
					break;
				}
			if (breakOut)
			{
				this.isReceiving = false;
				return false;
			}

		} while (!binding.receive());

		this.isReceiving = false;
		return true;
	}

	@Override
	public void resetBindings()
	{
		for (int i = 0; i < this.bindings.length; i++)
			this.bindings[i].reset();
	}

	private boolean isValid()
	{
		return Display.isCreated() && Keyboard.isCreated() && Mouse.isCreated();
	}

	private static class MouseEvent
	{
		public final int dx, dy, dz;
		public final int button;
		public final boolean state;

		public MouseEvent()
		{
			this.dx = Mouse.getDX();
			this.dy = Mouse.getDY();
			this.dz = Mouse.getEventDWheel();
			this.button = Mouse.getEventButton();
			this.state = Mouse.getEventButtonState();
		}
	}

	private static class KeyboardEvent
	{
		public final int key;
		public final boolean state;

		public KeyboardEvent()
		{
			this.key = Keyboard.getEventKey();
			this.state = Keyboard.getEventKeyState();
		}
	}

	private static class DirectionBinding implements InputBinding
	{
		public static enum Type {MOUSE_AXIS_X, MOUSE_AXIS_Y, MOUSE_AXIS_Z, KEY}

		private DirectionBinding.Type defaultType;
		private int defaultValue;

		private DirectionBinding.Type type;
		private int value;
		private String name;

		private List<MouseEvent> mouseEvents;
		private List<KeyboardEvent> keyboardEvents;

		private boolean currentlyActive;

		public DirectionBinding(DirectionBinding.Type defaultType, int defaultValue, List<MouseEvent> mouseEvents, List<KeyboardEvent> keyboardEvents)
		{
			this.defaultType = defaultType;
			this.defaultValue = defaultValue;
			this.reset();
			this.mouseEvents = mouseEvents;
			this.keyboardEvents = keyboardEvents;

			this.currentlyActive = false;
		}

		@Override
		public String getBindingString()
		{
			return new String(this.name);
		}

		@Override
		public boolean isActive()
		{
			if (this.name == "Unbound")
				return false;

			this.getValue(); // Tests activity
			return this.currentlyActive;
		}

		@Override
		public Object getValue()
		{
			if (this.type == DirectionBinding.Type.KEY)
			{
				ListIterator<KeyboardEvent> eventIterator = this.keyboardEvents.listIterator();
				while (eventIterator.hasNext())
				{
					KeyboardEvent event = eventIterator.next();

					if (event.key == this.value)
						this.currentlyActive = event.state;
				}

				if (this.currentlyActive)
					return new Float(1.0f);

				return new Float(0.0f);
			}
			else
			{
				float totalMove = 0.0f;

				ListIterator<MouseEvent> eventIterator = this.mouseEvents.listIterator();
				while (eventIterator.hasNext())
				{
					MouseEvent event = eventIterator.next();

					if (event.button != -1)
						continue;

					float move = 0.0f;

					if (this.type == DirectionBinding.Type.MOUSE_AXIS_X)
						move = (this.value * event.dx) / 500.0f; // TODO Tweak these
					else if (this.type == DirectionBinding.Type.MOUSE_AXIS_Y)
						move = (this.value * event.dy) / 500.0f; //
					else if (this.type == DirectionBinding.Type.MOUSE_AXIS_Z)
						move = (this.value * event.dz) / 120.0f;

					if (move < 0.0f)
						move = 0.0f;

					totalMove += move;
				}

				if (totalMove > Constants.FLT_EPSILON || (this.mouseEvents.size() == 0 && this.currentlyActive))
					this.currentlyActive = true;
				else
					this.currentlyActive = false;

				return new Float(totalMove);
			}
		}

		@Override
		public boolean receive()
		{
			{
				ListIterator<MouseEvent> eventIterator = this.mouseEvents.listIterator();
				while (eventIterator.hasNext())
				{
					MouseEvent event = eventIterator.next();

					if (event.button != -1)
						continue;

					if (Math.abs(event.dx) + Math.abs(event.dy) + Math.abs(event.dz) <= Constants.FLT_EPSILON)
						continue;

					if (Math.abs(event.dx) > Math.abs(event.dy))
					{
						if (Math.abs(event.dx) > Math.abs(event.dz))
							this.setBinding(DirectionBinding.Type.MOUSE_AXIS_X, (event.dx > 0 ? 1 : -1));
						else
							this.setBinding(DirectionBinding.Type.MOUSE_AXIS_Z, (event.dz > 0 ? 1 : -1));
					}
					else if (Math.abs(event.dy) > Math.abs(event.dz))
						this.setBinding(DirectionBinding.Type.MOUSE_AXIS_Y, (event.dy > 0 ? 1 : -1));
					else
						this.setBinding(DirectionBinding.Type.MOUSE_AXIS_Z, (event.dz > 0 ? 1 : -1));

					return true;
				}
			}

			{
				ListIterator<KeyboardEvent> eventIterator = this.keyboardEvents.listIterator();
				while (eventIterator.hasNext())
				{
					KeyboardEvent event = eventIterator.next();

					this.setBinding(DirectionBinding.Type.KEY, event.key);
					return true;
				}
			}

			return false;
		}

		@Override
		public void reset()
		{
			this.setBinding(this.defaultType, this.defaultValue);
		}

		private void setBinding(DirectionBinding.Type type, int value)
		{
			this.type = type;
			if (type == DirectionBinding.Type.KEY)
			{
				if (value >= 0)
				{
					this.name = Keyboard.getKeyName(value); // FIXME Match keyboard layout name
					if (this.name == null)
					{
						this.name = "Unbound";
						this.value = -1;
					}
					else
						this.value = value;
				}
				else
				{
					this.name = "Unbound";
					this.value = -1;
				}
			}
			else
			{
				this.name = "Mouse Axis ";
				if (value > 0)
				{
					this.name += "+";
					this.value = 1;
				}
				else
				{
					this.name += "-";
					this.value = -1;
				}

				switch (type)
				{
				case MOUSE_AXIS_X:
					this.name += "X";
					break;
				case MOUSE_AXIS_Y:
					this.name += "Y";
					break;
				case MOUSE_AXIS_Z:
					this.name += "Z";
					break;
				}
			}
		}


	}

	private static class KeyBinding implements InputBinding
	{
		public static enum Type {MOUSE, KEY}

		private KeyBinding.Type defaultType;
		private int defaultValue;

		private KeyBinding.Type type;
		private int value;
		private String name;

		private List<MouseEvent> mouseEvents;
		private List<KeyboardEvent> keyboardEvents;

		private boolean currentlyActive;

		public KeyBinding(KeyBinding.Type defaultType, int defaultValue, List<MouseEvent> mouseEvents, List<KeyboardEvent> keyboardEvents)
		{
			this.defaultType = defaultType;
			this.defaultValue = defaultValue;
			this.reset();
			this.mouseEvents = mouseEvents;
			this.keyboardEvents = keyboardEvents;

			this.currentlyActive = false;
		}

		@Override
		public String getBindingString()
		{
			return new String(this.name);
		}

		@Override
		public boolean isActive()
		{
			if (this.name == "Unbound")
				return false;

			if (this.type == KeyBinding.Type.KEY)
			{
				ListIterator<KeyboardEvent> eventIterator = this.keyboardEvents.listIterator();
				while (eventIterator.hasNext())
				{
					KeyboardEvent event = eventIterator.next();

					if (event.key == this.value)
						this.currentlyActive = event.state;
				}
			}
			else
			{
				ListIterator<MouseEvent> eventIterator = this.mouseEvents.listIterator();
				while (eventIterator.hasNext())
				{
					MouseEvent event = eventIterator.next();

					if (event.button == this.value)
						this.currentlyActive = event.state;
				}
			}

			return this.currentlyActive;
		}

		@Override
		public Object getValue()
		{
			return new Boolean(this.isActive());
		}

		@Override
		public boolean receive()
		{
			{
				ListIterator<MouseEvent> eventIterator = this.mouseEvents.listIterator();
				while (eventIterator.hasNext())
				{
					MouseEvent event = eventIterator.next();

					if (event.button == -1)
						continue;

					this.setBinding(KeyBinding.Type.MOUSE, event.button);
					return true;
				}
			}

			{
				ListIterator<KeyboardEvent> eventIterator = this.keyboardEvents.listIterator();
				while (eventIterator.hasNext())
				{
					KeyboardEvent event = eventIterator.next();

					this.setBinding(KeyBinding.Type.KEY, event.key);
					return true;
				}
			}

			return false;
		}

		@Override
		public void reset()
		{
			this.setBinding(this.defaultType, this.defaultValue);
		}

		private void setBinding(KeyBinding.Type type, int value)
		{
			this.type = type;
			if (type == KeyBinding.Type.KEY)
			{
				if (value >= 0)
				{
					this.name = Keyboard.getKeyName(value); // FIXME Match keyboard layout name
					if (this.name == null)
					{
						this.name = "Unbound";
						this.value = -1;
					}
					else
						this.value = value;
				}
				else
				{
					this.name = "Unbound";
					this.value = -1;
				}
			} else if (value >= 0)
			{
				this.name = String.format("Mouse Button %d", value + 1);
				this.value = value;
			}
			else
			{
				this.name = "Unbound";
				this.value = -1;
			}
		}
	}

	private static class DoubleTapBinding implements InputBinding
	{
		private InputBinding child;
		private long tapThreshold;

		private boolean childWasActive;
		private long lastActiveTime;

		private boolean currentlyActive;

		public DoubleTapBinding(InputBinding child, long tapThreshold)
		{
			this.child = child;
			this.tapThreshold = tapThreshold;

			this.childWasActive = this.child.isActive();
			this.lastActiveTime = 0;

			this.currentlyActive = false;
		}

		@Override
		public String getBindingString() { return this.child.getBindingString(); }

		@Override
		public boolean isActive()
		{
			// Make so that time starts with the beginning of each press
			boolean childIsActive = this.child.isActive();

			System.out.print(childIsActive);
			System.out.print(" ");
			System.out.print(this.childWasActive);
			System.out.print(" ");
			System.out.print(this.currentlyActive);
			System.out.print(" ");
			System.out.println(this.lastActiveTime);

			if (this.childWasActive && !childIsActive) // Child has been released --start the timer
			{
				this.childWasActive = false;
				this.currentlyActive = false;
			}
			else if (!this.childWasActive && childIsActive) // Child has been pressed --check the time since the child was last released
			{
				this.childWasActive = true;

				if (this.lastActiveTime == 0)
					this.lastActiveTime = System.currentTimeMillis();
				else if (System.currentTimeMillis() - this.lastActiveTime <= this.tapThreshold)
					this.currentlyActive = true;
			}

			if (System.currentTimeMillis() - this.lastActiveTime > this.tapThreshold)
				this.lastActiveTime = 0;

			return this.currentlyActive;
		}

		@Override
		public Object getValue() { return this.child.getValue(); }

		@Override
		public boolean receive() { return this.child.receive(); }

		@Override
		public void reset() { this.child.reset(); }
	}
}
