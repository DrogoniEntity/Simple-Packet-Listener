package fr.drogonistudio.spigot.packets;

import org.bukkit.Bukkit;

import fr.drogonistudio.spigot.packets.event.PacketEvent;
import fr.drogonistudio.spigot.packets.event.PacketReceiveEvent;
import fr.drogonistudio.spigot.packets.event.PacketSendEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * Custom channel handler.
 * 
 * <p>
 * This channel handler is injected into player's channel and fire
 * {@code PacketEvent} depends on if client send data or server send data.
 * </p>
 * 
 * @author DrogoniEntity
 * @see fr.drogonistudio.spigot.packets.event.PacketEvent PacketEvent class.
 */
public class RemoteClientChannelHandler extends ChannelDuplexHandler
{
    /**
     * Channel handler name.
     */
    public static final String HANDLER_NAME = "spl_prehandler";

    /**
     * Remote client who communicate with server.
     */
    private final RemoteClient client;

    /**
     * Setup channel handler.
     * 
     * @param client - remote client.
     */
    public RemoteClientChannelHandler(RemoteClient client)
    {
        this.client = client;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        Pointer<Object> msgPointer = new Pointer<>(msg);
        if (this.handle(msgPointer, PacketSendEvent.class))
            super.write(ctx, msgPointer.content, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        Pointer<Object> msgPointer = new Pointer<>(msg);
        if (this.handle(msgPointer, PacketReceiveEvent.class))
            super.channelRead(ctx, msgPointer.content);
    }

    /**
     * Custom job to execute before continuing netty's job.
     * 
     * <p>
     * If {@code msg} is a valide Minecraft packet, a new packet event will be
     * created and fired by Bukkit's plugin manager.
     * </p>
     * 
     * @param msg        - data to handle.
     * @param eventClass - {@code PacketEvent} type to fire.
     * @return {@code true} if event havn't been cancelled.
     */
    private boolean handle(Pointer<Object> msg, Class<? extends PacketEvent> eventClass)
    {
        boolean shouldContinue = true;

        // Proceed only if msg's class name contains 'net.minecraft' and 'Packet'.
        String className = msg.content.getClass().getName();
        if (className.contains("net.minecraft") && className.contains("Packet"))
        {
            try
            {
                PacketEvent event = eventClass.getConstructor(Object.class, RemoteClient.class).newInstance(msg.content, this.client);
                Bukkit.getServer().getPluginManager().callEvent(event);
                msg.content = event.getPacket();

                shouldContinue = !event.isCancelled();
            }
            catch (Throwable fatal)
            {
            }
        }

        return shouldContinue;
    }
}
