package gg.scala.plugin.robots.bots.behavior;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.Getter;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import gg.scala.plugin.robots.utils.Nms;
import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PacketsManager extends Manager {

    private final boolean relativeMovement = bot.getProfile().value(RootAccessor.INSTANCE.getPacketSettings().getRelativeMove());
    private final List<Packet> queue;

    public PacketsManager(Bot bot) {
        super(bot);
        queue = new ArrayList<>();
    }

    @Override
    public void queueUpdate() {
        queue.forEach(packet -> globalAction((player, temp) -> Nms.sendPacket(player, packet)));

        queue.clear();
    }

    public void sendCreatesPackets() {
        globalAction(this::sendCreatesPackets);
    }

    public void sendCreatesPackets(Player player, int temp) {
        var npc = getBot().getNpc();
        if (npc != null) {
            Nms.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            Nms.sendPacket(player, new PacketPlayOutNamedEntitySpawn(npc));
            Nms.sendPacket(player, new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));

            Schedulers
                    .async()
                    .runLater(() -> {
                        Bukkit.getOnlinePlayers().forEach(otherPlayer -> {
                            if (!otherPlayer.getUniqueId().equals(player.getUniqueId())) {
                                Nms.sendPacket(
                                        otherPlayer,
                                        new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc)
                                );
                            }
                        });
                    }, 20L)
                    .bindWith(getBot().getTerminable());

            skinUpdate(player);
        }
    }

    public void sendRemovePackets() {
        getBot().getViewsManager().getPlayers().forEach(this::sendRemovePackets);
    }

    public void sendDeathPackets() {
        if (bot.getNpc() != null) {
            for (Player player : getBot().getViewsManager().getPlayers()) {
                Nms.sendPacket(player, new PacketPlayOutEntityStatus(bot.getNpc(), (byte) 3));
            }
        }
    }

    public void sendRemovePackets(Player player) {
        if (bot.getNpc() != null) {
            Nms.sendPacket(player, new PacketPlayOutEntityDestroy(getBot().getNpc().getId()));
            Nms.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, getBot().getNpc()));
        }
    }

    public void skinUpdate(Player player) {
        if (getBot().getNpc() != null) {
            DataWatcher watcher = getBot().getNpc().getDataWatcher();
            watcher.watch(10, (byte) 127);
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(getBot().getNpc().getId(), watcher, true);
            Nms.sendPacket(player, packet);
        }
    }

    public void updateLocation(Location location) {
        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();
        final float yaw = location.getYaw();
        final float pitch = location.getPitch();

        if (bot.getNpc() != null) {
            bot.getNpc().setLocation(x, y, z, yaw, pitch);
            globalAction(this::sendUpdateLocationPackets);
        }
    }

    public void sendUpdateLocationPackets(Player player, int temp) {
        EntityPlayer npc = getBot().getNpc();
        if (npc != null) {
            if (!relativeMovement) Nms.sendPacket(player, new PacketPlayOutEntityTeleport(npc));
            Nms.sendPacket(player, new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
        }
    }

    public void sendBlockHittingPackets(boolean enabled) {
        if (getBot().getNpc() != null) {
            PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata();
            DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(0, 0, (byte) (enabled ? 16 : 0));
            ArrayList<DataWatcher.WatchableObject> list = new ArrayList<>();
            list.add(watchableObject);

            for (Field field : metadata.getClass().getDeclaredFields()) {
                if (field.getName().equals("a")) {
                    field.setAccessible(true);
                    try {
                        field.set(metadata, bot.getNpc().getBukkitEntity().getEntityId());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                if (field.getName().equals("b")) {
                    field.setAccessible(true);
                    try {
                        field.set(metadata, list);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            globalAction((player, temp) -> {
                Nms.sendPacket(player, metadata);
            });
        }
    }

    public void setPlayerOnFirePackets(boolean fire) {
        if (getBot().getNpc() != null) {
            PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata();
            DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(0, 0, (byte) (fire ? 1 : 0));
            ArrayList<DataWatcher.WatchableObject> list = new ArrayList<>();
            list.add(watchableObject);

            for (Field field : metadata.getClass().getDeclaredFields()) {
                if (field.getName().equals("a")) {
                    field.setAccessible(true);
                    try {
                        field.set(metadata, bot.getNpc().getBukkitEntity().getEntityId());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                if (field.getName().equals("b")) {
                    field.setAccessible(true);
                    try {
                        field.set(metadata, list);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            globalAction((player, temp) -> {
                Nms.sendPacket(player, metadata);
            });
        }
    }
}
