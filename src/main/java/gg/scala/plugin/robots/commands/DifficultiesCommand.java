package gg.scala.plugin.robots.commands;

import gg.scala.commons.acf.annotation.CommandAlias;
import gg.scala.commons.acf.annotation.CommandPermission;
import gg.scala.commons.annotations.commands.AutoRegister;
import gg.scala.commons.command.ScalaCommand;
import gg.scala.commons.issuer.ScalaPlayer;
import gg.scala.plugin.robots.bot.menu.profiles.GenericDescribeProfilesMenu;
import gg.scala.plugin.robots.bot.menu.profiles.ProfileCategoryMenu;
import gg.scala.plugin.robots.bot.setting.BotSettingService;
import gg.scala.plugin.robots.bot.setting.setting.GlobalsKt;

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@AutoRegister
public class DifficultiesCommand extends ScalaCommand {

    @CommandAlias("difficulties")
    @CommandPermission("bot.staff")
    public void onBotGUI(ScalaPlayer player) {
        new GenericDescribeProfilesMenu(
                () -> BotSettingService.INSTANCE
                        .cached()
                        .getMap()
                        .values()
                        .stream()
                        .toList(),
                settingProfile -> {
                    new ProfileCategoryMenu(
                            settingProfile,
                            (settings) -> {
                                final var cached = BotSettingService.INSTANCE.cached();
                                cached.getMap().put(settings.getId(), settings);

                                BotSettingService.INSTANCE.sync(cached);
                            },
                            GlobalsKt.getRoot()
                    ).openMenu(player.bukkit());
                    return null;
                }
        ).openMenu(player.bukkit());
    }
}
