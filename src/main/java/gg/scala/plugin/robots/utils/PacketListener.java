package gg.scala.plugin.robots.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import gg.scala.plugin.robots.ScalaRobotsPlugin;
import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.Bot;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.reflect.Field;

@Getter
@Setter
public class PacketListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        try {
            removePlayer(e.getPlayer());
        } catch (Exception ignored) {

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        addPlayer(e.getPlayer());
    }

    public void addPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            @Deprecated
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                try {
                    if (packet instanceof PacketPlayInFlying) {
                        DataPlayer data = ScalaRobotsPlugin.getInstance().getDataManager().getPlayer(player);
                        if (data != null) {
                            var target = data.getTargetedBy();
                            if (target != null) {
                                data.handleFlying((PacketPlayInFlying) packet);
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                DataPlayer data = ScalaRobotsPlugin.getInstance().getDataManager().getPlayer(player);
                if (data == null) {
                    super.write(channelHandlerContext, packet, channelPromise);
                    return;
                }

                //OverWrites Player velocity

                try {
                    if (packet instanceof PacketPlayOutEntityVelocity) {
                        PacketPlayOutEntityVelocity wrapper = (PacketPlayOutEntityVelocity) packet;
                        ByteBuf byteBuf = Unpooled.buffer();
                        PacketDataSerializer packetData = new PacketDataSerializer(byteBuf);

                        wrapper.b(packetData);

                        int id = packetData.e();
                        double x = packetData.readShort() / 8000D;
                        double y = packetData.readShort() / 8000D;
                        double z = packetData.readShort() / 8000D;

                        if (id == player.getEntityId()) {
                            var targetedBot = data.getTargetedBy();
                            if (targetedBot != null) {
                                if (data.handle(targetedBot)) {
                                    Vector v = data.velocity(targetedBot);
                                    if (v != null) {
                                        packetData = new PacketDataSerializer(byteBuf);
                                        packetData.b(id);
                                        packetData.writeShort((int) (v.getX() * 8000D));
                                        packetData.writeShort((int) (v.getY() * 8000D));
                                        packetData.writeShort((int) (v.getZ() * 8000D));

                                        wrapper.a(packetData);
                                    }
                                }
                            }
                        }
                    }

                    //OverWrites Relative move if enabled
                    if (packet instanceof PacketPlayOutEntity) {
                        PacketPlayOutEntity wrapper = (PacketPlayOutEntity) packet;
                        ByteBuf byteBuf = Unpooled.buffer();
                        PacketDataSerializer packetData = new PacketDataSerializer(byteBuf);

                        wrapper.b(packetData);
                        int id = packetData.e();

                        var bot = ScalaRobotsPlugin.getInstance().getBotManager().getBots().get(id);
                        if (bot != null && !bot.getPacketsManager().isRelativeMovement()) {
                            return;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                super.write(channelHandlerContext, packet, channelPromise);
            }
        };

        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline == null)
            return;
        String handlerName = "bot_processor";
        channel.eventLoop().submit(() -> {
            if (pipeline.get(handlerName) != null)
                pipeline.remove(handlerName);
            pipeline.addBefore("packet_handler", handlerName, channelDuplexHandler);
            return null;
        });
    }

    public void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        ChannelPipeline pipeline = channel.pipeline();
        String handlerName = "bot_processor";
        channel.eventLoop().submit(() -> {
            if(pipeline.get(handlerName) != null)
                pipeline.remove(handlerName);
            return null;
        });
    }
}
