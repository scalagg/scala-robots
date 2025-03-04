package gg.scala.plugin.robots.bots;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.scala.plugin.robots.bots.behavior.*;
import gg.scala.practice.games.GameImpl;
import gg.scala.practice.kit.Kit;
import gg.scala.plugin.robots.ScalaRobotsPlugin;
import gg.scala.plugin.robots.bot.behavior.ActionController;
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile;
import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.events.impl.BotDeathEvent;
import gg.scala.plugin.robots.bots.events.impl.SpawnEvent;
import gg.scala.plugin.robots.bots.managers.ManagerService;
import gg.scala.plugin.robots.bots.behavior.movements.MovementsMain;
import gg.scala.plugin.robots.npc.NPCEntity;
import gg.scala.plugin.robots.npc.NPCNetworkManager;
import gg.scala.plugin.robots.npc.NPCPlayerConnection;
import gg.scala.plugin.robots.utils.objects.EventRunnable;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.*;

@Getter
@Setter
public class Bot {
    private MovementsMain movements;
    private NPCEntity npc;
    private Kit kit;
    private PacketsManager packetsManager;
    private ViewsManager viewsManager;
    private EffectsManager effectsManager;
    private VelocityCalculator velocityCalculator;
    private ManagerService eventManager;
    private DamageManager damageManager;
    private InventoryManager inventoryManager;
    private AttackingManager attackingManager;
    private HealthManager healthManager;
    private LagCompensatorManager lagCompensatorManager;
    private FleeManager fleeManager;
    private EventRunnable eventListener = (event) -> {};
    private DamageCalculator damageCalculator;

    private final CompositeTerminable terminable = CompositeTerminable.create();
    private final ActionController actionController = new ActionController(this);

    private boolean alive = true;
    private final UUID uniqueId = UUID.randomUUID();

    private final SettingProfile profile;
    private Location startLocation;

    private int combo = 0, ticks = 0, prevSlot = 0, pearlTick = 0;
    private long lastSwitch = 0;

    /**
     * Keeps track of how many potions have been used in the hotbar
     * so we can trigger the refill pause system.
     */
    private int potionsThrown = 0;
    private GameImpl game;

    private boolean currentlyBuilding;

    // Temp
    private long lastPlaced = System.currentTimeMillis();
    private List<Location> blockedLocations = new ArrayList<>();
    private long lastPlacedBlockUp = System.currentTimeMillis();
    private long lastLiquid = System.currentTimeMillis();
    private long lastSuffocationDamage = System.currentTimeMillis();
    private long lastPlacedWater = System.currentTimeMillis();

    private final UUID scalaID;

    public Bot(UUID internalSID, Location location, String name, GameImpl game, SettingProfile profile, Kit kit) {
        this.scalaID = internalSID;
        this.startLocation = location;
        this.kit = kit;
        this.profile = profile;
        this.game = game;

        registerNPC(name, location);
        Bukkit.getOnlinePlayers().forEach(other ->
                other.hidePlayer(npc.getBukkitEntity())
        );

        registerManagers(location);
        packetsManager.sendCreatesPackets();

        loadManagers();
        registerBot();
    }

    private void registerBot() {
        ScalaRobotsPlugin.getInstance().getBotManager().addBot(this);
        movements.callEventAsync(new SpawnEvent(this));
    }

    private void loadManagers() {
        eventManager.addManager(movements);
        eventManager.addManager(viewsManager);
        eventManager.addManager(inventoryManager);
        eventManager.addManager(packetsManager);
        eventManager.addManager(damageManager = new DamageManager(this));
        eventManager.addManager(attackingManager = new AttackingManager(this));
        eventManager.addManager(healthManager = new HealthManager(this));
        eventManager.addManager(damageCalculator = new DamageCalculator(this));
        eventManager.addManager(fleeManager);
        eventManager.addManager(effectsManager);
    }

    private void registerManagers(Location location) {
        lagCompensatorManager = new LagCompensatorManager(this);
        eventManager = new ManagerService(this);
        effectsManager = new EffectsManager(this);
        velocityCalculator = new VelocityCalculator(this);
        movements = new MovementsMain(this, location);
        packetsManager = new PacketsManager(this);
        inventoryManager = new InventoryManager(this);
        fleeManager = new FleeManager(this);
        viewsManager = new ViewsManager(this);
    }

    private void registerNPC(String name, Location loc) {
        GameProfile profile = new GameProfile(uniqueId, name);
        profile.getProperties().put("textures", this.profile.getSkinProperties());

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        PlayerInteractManager interactManager = new PlayerInteractManager(world);

        npc = new NPCEntity(this, server, world, profile, interactManager);
        npc.playerConnection = new NPCPlayerConnection(server, new NPCNetworkManager(EnumProtocolDirection.CLIENTBOUND), npc);
        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        npc.setPosition(loc.getX(), loc.getY(), loc.getZ());

        Schedulers
                .sync()
                .run(() -> world.addEntity(npc))
                .bindWith(terminable);
    }

    public static final Property DEFAULT = new Property(
            "textures",
            "ewogICJ0aW1lc3RhbXAiIDogMTcyMzE4NTQ1NzAzMSwKICAicHJvZmlsZUlkIiA6ICI2M2RmMDBjMTJmY2I0YjFhOWVhMGVmOTdkNzk0MzU4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJCbGFja05pZ2h0MDgwMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lNjNjMGQ1ZDY3ZGU5Zjk4YmUxZGZhMzY4YmU4ODRmY2E2NDk5ZTA1MjdhZjMyZWZlMzI3M2VhMjM2YmVhOTZiIgogICAgfQogIH0KfQ==",
            "qd/hF5lFWoFMrIwCVhSiIYxAF/r3XHIHetoNe3Bv2xf0IXec4i6a+C4cAWrD3sfOYmVJu5WEKO1o5LTXUtem53oRe1kZ6qr+VRvhR/5I2H9kNYVcjwzTmUILFSfBo/mWyRXNlIHLmRyX0coVBL132Zz2MNI8ekB4vJ5sTY7aRYVnYfDlHiSCcBtKUFZyzgDIlP2Ti/YPCoIgmUXuElvziwgut1+wYyHHCRxwiRO0NXdWN7OblE4Oj+QbFY+QKGwBZaaIQnZJ+DhruPHzsn0u7AyPnlBTYJgxPRNatD/1OHqYuQ+/MKXZ4GbvxXjxkfBO7Y/yjrhKrkEGIJKOWz8a5oMTKrOWpocvWxbtyzN6TQIYn6BATtGI1IkBlHbQJeXh9WMEnkU+sQfQh53h8QjDv2dI/U9Y/PXCEFn1znNRXnsfDkpOt3AtQR3lAJ6n2LMzyiaGEHBvth/ZPeloH8+jCgnJvRzR+BUPEuR2getehGtPTZg5ChImLOusRCod46Z8ed4MZuLO3G/qPVxlo/OoCJ/wKjHyxtdiRv3SMBMX/tpV27j6GCkf/vgrNhIYW6D3feTF3zas7MIuXfa9mGLWNZEfZzIW65E2LTQF2hhYYJn4VzhWPaQ9ZNgRdX5bICpjeg4mdHa1UwLOCNKrmxNsDjX6PKU3RuG/zWvbnlczCZ4="
    );

    public void kill() {
        if (alive) {
            attackingManager.setAttacking(false);
            damageManager.setInvaluable(true);

            //Animation
            animateDeath();
        }
    }

    public void animateDeath() {
        if (alive) {
            movements.setMove(false);
            packetsManager.sendDeathPackets();

            Schedulers.sync()
                    .runLater(this::destroy, 14L)
                    .bindWith(terminable);
        }
    }

    public void destroy() {
        if (alive) {
            alive = false;
            packetsManager.sendRemovePackets();
            ScalaRobotsPlugin.getInstance().getBotManager().removeBot(this);

            terminable.closeAndReportException();

            npc.getBukkitEntity().remove();
            Bukkit.getServer().getPluginManager().callEvent(new BotDeathEvent(this));
        }
    }

    public void tick() {
        velocityCalculator.setEarly(true);

        if (!eventManager.ensureReady())
        {
            velocityCalculator.setEarly(false);
            return;
        }

        eventManager.queueUpdate();
        eventManager.tickEarly();
        eventManager.tick();
        eventManager.tickLate();

        velocityCalculator.setEarly(false);
        eventManager.executeEvents();

        /**
         * Do tick last so that the above events have time to queue up new actions.
         *
         * If an action is overridden, the initialize sequence will run during
         * {@link runAction}, and the actionController will run the initial tick.
         */
        actionController.tick();
    }
}
