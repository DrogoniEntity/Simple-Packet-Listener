# Simple Packet Listener
Another Spigot plugin to allow plugins to listening to packets.

This spigot plugin is designed to add new Bukkit events (about packet receiving and sending) and catch these events like other Bukkit events.

### But, how do it work ?
Like the most packet interception plugins, it will inject into players connections an new channel handler which it will fire new Bukkit events.

Currently, this plugin only support for packet in-game and packet status listening (not handshaking or login).

### It's seem to be good but how can I use it ?
It's quite simple ! You just need to specified in your `plugin.yml` your plugin depend on `Simple-Packet-Listener` and in your code, you handle `PacketReceiveEvent` and/or `PacketSendEvent` like that :

```java
@EventHandler
public void onPacketReceive(PacketReceiveEvent event)
{
     System.out.println("I receive " + event.getPacket().getClass().getSimpleName() + " !");
}

@EventHandler
public void onPacketSend(PacketSendEvent event)
{
     System.out.println("I send " + event.getPacket().getClass().getSimpleName() + " !");
}
```

### And what can I do with these 2 events ?
You can :
* Handle packet in their raw format (you can use NMS or reflection).
* Access to packet's fields via Reflection (an utility class has been created to help you to access to classes and fields named as `NmsReflection`).
* Implementing custom events depends on packets sending or receiving.

### Which version of server are supported ?
This plugin has been designed to run on Spigot 1.17 but it works from 1.13. (1.17 and 1.16.5 have been used to test this plugin).

### What is the structure of this plugin ?
This project is split into 2 parts :
* `api` contains all shared classes to all plugins.
* `src` contains core plugin code.

