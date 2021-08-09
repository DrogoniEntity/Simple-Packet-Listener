package fr.drogonistudio.spigot.packets;

import java.net.InetSocketAddress;

import org.bukkit.entity.Player;

public final class RemoteClient
{
	private final InetSocketAddress remoteAddress;
	private final Player player;
	
	public RemoteClient(InetSocketAddress addr, Player player)
	{
		this.remoteAddress = addr;
		this.player = player;
	}
	
	public InetSocketAddress getAddress()
	{
		return this.remoteAddress;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
}
