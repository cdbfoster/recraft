package recraft.network;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.ListIterator;

// TODO Docstrings for this stuff.
public class OutgoingNetworkHandler
{
	private Socket outgoingSocket;
	private LinkedList<Object> outgoingQueue;

	private BufferedOutputStream bufferedStream;
	private ObjectOutputStream outgoingStream;

	public OutgoingNetworkHandler(Socket outgoingSocket)
	{
		this.outgoingSocket = outgoingSocket;
		this.outgoingQueue = new LinkedList<Object>();

		try
		{
			this.bufferedStream = new BufferedOutputStream(this.outgoingSocket.getOutputStream());
			this.outgoingStream = new ObjectOutputStream(this.bufferedStream);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void enqueueObject(Object object)
	{
		synchronized (this.outgoingQueue)
		{
			this.outgoingQueue.add(object);
		}
	}

	public void sendOutgoingQueue()
	{
		synchronized (this.outgoingStream)
		{
			synchronized (this.bufferedStream)
			{
				synchronized (this.outgoingQueue)
				{
					try
					{
						ListIterator iterator = this.outgoingQueue.listIterator();
						while (iterator.hasNext())
							this.outgoingStream.writeObject(iterator.next());
						this.bufferedStream.flush();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					this.outgoingQueue.clear();
				}
			}
		}
	}

	public void sendImmediate(Object object)
	{
		synchronized (this.outgoingStream)
		{
			synchronized (this.bufferedStream)
			{
				try
				{
					this.outgoingStream.writeObject(object);
					this.bufferedStream.flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void close()
	{
		synchronized (this.outgoingStream)
		{
			synchronized (this.bufferedStream)
			{
				try
				{
					this.outgoingStream.close();
					this.bufferedStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
