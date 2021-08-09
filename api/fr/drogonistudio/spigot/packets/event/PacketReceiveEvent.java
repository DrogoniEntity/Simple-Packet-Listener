package fr.drogonistudio.spigot.packets.event;

import fr.drogonistudio.spigot.packets.RemoteClient;

public final class PacketReceiveEvent extends PacketEvent
{
	public PacketReceiveEvent(Object packet, RemoteClient client)
	{
		super(packet, client);
	}
}
