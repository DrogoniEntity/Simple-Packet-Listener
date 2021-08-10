package fr.drogonistudio.spigot.packets.event;

import fr.drogonistudio.spigot.packets.RemoteClient;

/**
 * Receiving packet event.
 * 
 * <p>
 * This packet event is fired when server is about to read incoming packets.
 * </p>
 * <p>
 * This event can be used to change some incoming data or to intercept some
 * uncaught events and allow to perform advanced actions. When this event is
 * canceled, server will never handle packet and doesn't about packet's
 * existence.
 * </p>
 * 
 * @author DrogoniEntity
 * @see PacketEvent Base packet event class.
 */
public final class PacketReceiveEvent extends PacketEvent
{
    public PacketReceiveEvent(Object packet, RemoteClient client)
    {
        super(packet, client);
    }
}
