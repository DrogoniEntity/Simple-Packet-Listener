package fr.drogonistudio.spigot.packets;

import org.bukkit.Bukkit;

import fr.drogonistudio.spigot.packets.event.PacketEvent;
import fr.drogonistudio.spigot.packets.event.PacketReceiveEvent;
import fr.drogonistudio.spigot.packets.event.PacketSendEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class RemoteClientChannelHandler extends ChannelDuplexHandler
{
	public static final String HANDLER_NAME = "spl_prehandler";
	
	private final RemoteClient client;
	
	public RemoteClientChannelHandler(RemoteClient client)
	{
		this.client = client;
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
	{
		if (this.handle(msg, PacketSendEvent.class))
			super.write(ctx, msg, promise);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		if (this.handle(msg, PacketReceiveEvent.class))
			super.channelRead(ctx, msg);
	}

	private boolean handle(Object msg, Class<? extends PacketEvent> eventClass)
	{
		boolean shouldContinue = true;
		
		// Proceed only if msg's class name contains 'net.minecraft' and 'Packet'.
		String className = msg.getClass().getName();
		if (className.contains("net.minecraft") && className.contains("Packet"))
		{
			try
			{
				PacketEvent event = eventClass.getConstructor(Object.class, RemoteClient.class).newInstance(msg, this.client);
				Bukkit.getServer().getPluginManager().callEvent(event);
				
				shouldContinue = !event.isCancelled();
			}
			catch (Throwable fatal)
			{
			}
		}
		
		return shouldContinue;
	}
}
