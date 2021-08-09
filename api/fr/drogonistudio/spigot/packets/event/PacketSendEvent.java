package fr.drogonistudio.spigot.packets.event;

import fr.drogonistudio.spigot.packets.RemoteClient;

public class PacketSendEvent extends PacketEvent
{
	public PacketSendEvent(Object packet, RemoteClient client)
	{
		super(packet, client);
	}
}
