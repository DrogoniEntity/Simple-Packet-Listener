package fr.drogonistudio.spigot.packets.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.drogonistudio.spigot.packets.RemoteClient;

public abstract class PacketEvent extends Event implements Cancellable
{
	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Object packet;
	private final RemoteClient remote;
	
	private boolean cancelled;
	
	public PacketEvent(Object packet, RemoteClient client)
	{
		super(true);
		
		this.packet = packet;
		this.remote = client;
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}
	
	public final Object getPacket()
	{
		return this.packet;
	}
	
	public final RemoteClient getRemote()
	{
		return this.remote;
	}
	
	public void setCancelled(boolean statement)
	{
		this.cancelled = statement;
	}
	
	@Override
	public final boolean isCancelled()
	{
		return this.cancelled;
	}
}
