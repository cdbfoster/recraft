package recraft.networknode;

import recraft.core.NetworkInterface;
import recraft.core.NetworkNode;

public class MinecraftServer extends NetworkNode
{
	private MinecraftServerConfiguration configuration;

	public MinecraftServer(MinecraftServerConfiguration configuration, NetworkInterface networkInterface)
	{
		super(networkInterface);

		this.configuration = configuration;
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

	public static class MinecraftServerConfiguration
	{
		// How do we handle configuration?

	}
}
