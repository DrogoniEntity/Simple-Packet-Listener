package fr.drogonistudio.spigot.packets.event;

import fr.drogonistudio.spigot.packets.RemoteClient;

/**
 * Sending packet event.
 * 
 * <p>
 * This packet event is fired when server is about to send packet.
 * </p>
 * <p>
 * This event can be used to change some packet's fields to send fake
 * information to clients. When this event is cancelled, then clients will never
 * receive sending packet.
 * </p>
 * 
 * @author DrogoniEntity
 * @see PacketEvent Base packet event class.
 */
public final class PacketSendEvent extends PacketEvent
{
    public PacketSendEvent(Object packet, RemoteClient client)
    {
        super(packet, client);
    }
}
