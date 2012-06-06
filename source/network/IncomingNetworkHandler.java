package recraft.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.LinkedList;

/** Uses a thread to listen to serverSocket for incoming objects.  Objects can be found in the LinkedList
 * receivedObjects.  NOTE: Synchronize all accesses to receivedObjects! */
public class IncomingNetworkHandler
{
	public final LinkedList<Object> receivedQueue;

	private Socket incomingSocket;
	private ObjectInputStream incomingStream;
	private Thread listener;

	public IncomingNetworkHandler(Socket incomingSocket)
	{
		this.receivedQueue = new LinkedList<Object>();

		this.incomingSocket = incomingSocket;

		try
		{
			this.incomingStream = new ObjectInputStream(incomingSocket.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		this.listener = new Thread(new Listener(this.incomingStream, this.receivedQueue));
		this.listener.start();
	}

	public void close()
	{
		if (this.listener == null)

			return;

		this.listener.interrupt();
		try
		{
			this.listener.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		try
		{
			this.incomingStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		this.incomingStream = null;
		this.listener = null;
	}

	/** Waits on incomingStream and populates receivedObjects. */
	private static class Listener implements Runnable
	{
		private ObjectInputStream incomingStream;
		private LinkedList<Object> receivedObjects;

		public Listener(ObjectInputStream incomingStream, LinkedList<Object> receivedObjects)
		{
			this.incomingStream = incomingStream;
			this.receivedObjects = receivedObjects;
		}

		@Override
		public void run()
		{
			while (!Thread.interrupted())
			{
				Object in = null;
				try
				{
					in = this.incomingStream.readObject();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}

				if (in != null)
					synchronized (this.receivedObjects)
					{
						this.receivedObjects.add(in);
					}
			}
		}
	}
}
