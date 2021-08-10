package fr.drogonistudio.spigot.packets.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.drogonistudio.spigot.packets.RemoteClient;

/**
 * A simple packet event.
 * 
 * <p>
 * A packet event represent the event when server receive or send packets to an
 * client.
 * </p>
 * <p>
 * These events are fired when server is about to send packets or is about to
 * receive packet before the server has handled packets. Packets can be handled
 * in any form you want (by NMS or by reflection).
 * </p>
 * 
 * <p>
 * Be careful, these events run asynchronously. However, you can cancel these
 * events and prevent server to read incoming packets or send outgoing packets.
 * </p>
 * 
 * @author DrogoniEntity
 * @see PacketReceiveEvent Packet reading event.
 * @see PacketSendEvent Packet writing event.
 */
public abstract class PacketEvent extends Event implements Cancellable
{
    /**
     * Internal use by Bukkit.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Packet to handled.
     */
    private final Object packet;

    /**
     * Remote client who receive or send {@code packet}.
     */
    private final RemoteClient remote;

    /**
     * Cancel flag.
     * 
     * When this flag is enabled, server will not be handled packet.
     */
    private boolean cancelled;

    /**
     * Setup asynchronous event.
     * 
     * @param packet - packet to handled.
     * @param client - remote client who communicate with server.
     */
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

    /**
     * Internal use by Bukkit.
     * 
     * @return event's handler list.
     */
    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }

    /**
     * Getting handled packet.
     * 
     * <p>
     * This packet is representing in its raw representation and be handled by NMS
     * or with reflective.
     * </p>
     * 
     * @return handled packet.
     */
    public final Object getPacket()
    {
        return this.packet;
    }

    /**
     * Getting remote client.
     * 
     * <p>
     * This remote client warp the player who sending or receiving the handled
     * packet.
     * </p>
     * 
     * @return remote client.
     */
    public final RemoteClient getRemote()
    {
        return this.remote;
    }

    /**
     * Change cancel statement.
     * 
     * <p>
     * When this statement is enabled, it will prevent packet to be read or send by
     * server.
     * </p>
     * 
     * @param statement - new cancel statement (set {@code true} to prevent packet
     *                  handling).
     */
    public void setCancelled(boolean statement)
    {
        this.cancelled = statement;
    }

    @Override
    /**
     * Getting cancel statement.
     * 
     * <p>
     * When this statement is enabled, it will prevent packet to be read or send by
     * server.
     * </p>
     * 
     * @return cancel statement.
     */
    public final boolean isCancelled()
    {
        return this.cancelled;
    }
}
