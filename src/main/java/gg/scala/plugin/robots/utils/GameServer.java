package gg.scala.plugin.robots.utils;

import gg.scala.commons.agnostic.sync.ServerSync;
import lombok.experimental.UtilityClass;

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@UtilityClass
public class GameServer
{

    public static boolean practice()
    {
        return ServerSync.INSTANCE.getLocal().getGroups().contains("mipgame");
    }

    public static boolean pit()
    {
        return ServerSync.INSTANCE.getLocal().getGroups().contains("pit");
    }
}
