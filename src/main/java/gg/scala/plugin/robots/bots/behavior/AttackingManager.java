package gg.scala.plugin.robots.bots.behavior;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.events.impl.BotAttackLandEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.events.impl.BotAttackEvent;
import gg.scala.plugin.robots.bots.managers.Manager;
import gg.scala.plugin.robots.bots.behavior.movements.submovements.TargetLocate;
import gg.scala.plugin.robots.utils.Nms;
import gg.scala.plugin.robots.utils.objects.AABB;
import gg.scala.plugin.robots.utils.raycast.RayCasting;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@Getter
@Setter
public class AttackingManager extends Manager {
    public AttackingManager(Bot bot) {
        super(bot);
    }

    private boolean attacking = true;
    private boolean blocking = false;
    private int tickCounter;
    private long attacked;
    private boolean landAttack;
    private int attackTicks;

    private boolean hitSelecting;
    private int hitSelectingTicks;
    private long lastProjectile;
    private boolean projectile;

    public void updateProjectile() {
        lastProjectile = System.currentTimeMillis();
        projectile = true;
    }

    private long lastAttackedOrDamaged = System.currentTimeMillis();

    @Override
    public void tickEarly() {
        if (!landAttack) return;
        landAttack = false;

        Event event = new BotAttackLandEvent(getBot(), getBot().getMovements().getTargetLocate().getTargetEntity());
        getBot().getEventListener().run(event); // Additional calling
        callEvent(event);
        getBot().getVelocityCalculator().setSprinting(false);
    }

    @Override
    public void tickLate() {
        attackTicks++;
        if (!attacking) return;
        /**
         * Bots should not attack while there is an active action.
         */
        if (bot.getActionController().current() != null) return;
        if (getBot().getFleeManager().isActive()) return;
        TargetLocate targetLocate = getBot().getMovements().getTargetLocate();
        int delay = RootAccessor.INSTANCE.getAttackSettings().getDelay(getBot().getProfile());
        if (getBot().getMovements().isJumpCombo())
            delay = 1000 / getBot().getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getJumpComboCPS());
        int clicks = 0;

        tickCounter += 50;

        if (getBot().getDamageManager().getDamageTicks() >= getBot().getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getHitSelectTicks()) && getBot().getDamageManager().getDamageTicks() < 10)
            hitSelecting = false;

        //Do not attack if hit selecting
        if (getBot().getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getHitSelect()) && hitSelectingTicks > getBot().getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getHitSelectDelay()) && attackTicks > 20) {
            hitSelecting = true;
            hitSelectingTicks = 0;
        }

        if (hitSelecting) return;
        hitSelectingTicks++;

        while (tickCounter > delay) {
            tickCounter -= delay;
            clicks++;
        }

        for (int i = 0; i < clicks; i++) {
            double dist = getDistance();
            if (dist == -1) return;
            if (dist >= getBot().getProfile().value(RootAccessor.INSTANCE.getAttackSettings().getSwingRange())) return;

            swing();
            if (!rayTrace()) return;
            attack(targetLocate.getTargetEntity());
            projectile = false;
        }
    }

    public boolean rayTrace() {
        Location loc = getBot().getMovements().getLocation().clone();
        Vector from = loc.toVector();

        double eyeHeight = 1.62;
        loc.setY(loc.getY() + eyeHeight);
        loc.add(loc.getDirection().normalize().multiply(-0.05));

        double offSet = getBot().getMovements().getCollision().getXzHitBoxOffset();

        Vector to = from.clone().add(loc.getDirection().normalize().multiply(getBot().getProfile().value(RootAccessor.INSTANCE.getAttackSettings().getReach()) + 0.05));
        var clientLocation = getBot().getLagCompensatorManager().getClientLocation();
        if (clientLocation == null) {
            return false;
        }
        AABB collision = getHitBox(clientLocation, (float) offSet);

        //Block collision check
        Vector bc = traceWithBlocks(loc.toVector(), to.toLocation(loc.getWorld()), 0.5);
        double dist = 100;
        if (bc != null) dist = bc.distance(loc.toVector());
        if (dist <= 0.3) dist = 0.3;

        Vector result = RayCasting.hitboxIntersection(loc.toVector(), to, collision);
        if (result == null) return false;

        double reach = result.distance(from);
        boolean attack = dist > reach;

        return reach <= getBot().getProfile().value(RootAccessor.INSTANCE.getAttackSettings().getReach()) && attack;
    }

    public Vector traceWithBlocks(Vector from, Location to, double accurate) {
        Vector dir = to.toVector().subtract(from).normalize();
        double reach = to.toVector().distance(from);
        World world = to.getWorld();
        Vector lastResult = from;

        for (double i = 0; i < reach; i += accurate) {
            Vector newPos = from.clone().add(dir.clone().multiply(i));

            if (newPos.toLocation(world).getBlock().getType().isSolid()) return lastResult;
            lastResult = newPos;
        }

        return null;
    }

    public AABB getHitBox(Location location, float offSet) {
        Vector targetPos = location.toVector();

        return new AABB(new Vector(targetPos.getX() - offSet, targetPos.getY(), targetPos.getZ() - offSet), new Vector(targetPos.getX() + offSet, targetPos.getY() + 2, targetPos.getZ() + offSet));
    }

    public void swing() {
        var npc = getBot().getNpc();
        if (npc != null) {
            globalAction((player, temp) -> Nms.sendPacket(player, new PacketPlayOutAnimation(npc, 0)));
        }
    }

    double calculateItemAttackDamage(ItemStack item) {
        //Item has enchantments
        if (item == null) return 1;
        //return damage
        double attackDamage = 1;

        //SWORD
        if (item.getType().equals(Material.DIAMOND_SWORD)) {
            attackDamage += 7;
        } else if (item.getType().equals(Material.IRON_SWORD)) {
            attackDamage += 6;
        } else if (item.getType().equals(Material.STONE_SWORD)) {
            attackDamage += 5;
        } else if (item.getType().equals(Material.GOLD_SWORD)) {
            attackDamage += 4;
        } else if (item.getType().equals(Material.WOOD_SWORD)) {
            attackDamage += 4;
        }

        //AXE
        else if (item.getType().equals(Material.DIAMOND_AXE)) {
            attackDamage += 6;
        } else if (item.getType().equals(Material.IRON_AXE)) {
            attackDamage += 5;
        } else if (item.getType().equals(Material.STONE_AXE)) {
            attackDamage += 4;
        } else if (item.getType().equals(Material.GOLD_AXE)) {
            attackDamage += 3;
        } else if (item.getType().equals(Material.WOOD_AXE)) {
            attackDamage += 3;
        }

        //PICKAXE
        else if (item.getType().equals(Material.DIAMOND_PICKAXE)) {
            attackDamage += 5;
        } else if (item.getType().equals(Material.IRON_PICKAXE)) {
            attackDamage += 4;
        } else if (item.getType().equals(Material.STONE_PICKAXE)) {
            attackDamage += 3;
        } else if (item.getType().equals(Material.GOLD_PICKAXE)) {
            attackDamage += 2;
        } else if (item.getType().equals(Material.WOOD_PICKAXE)) {
            attackDamage += 2;
        }

        //SPADE
        else if (item.getType().equals(Material.DIAMOND_SPADE)) {
            attackDamage = 5.5;
        } else if (item.getType().equals(Material.IRON_SPADE)) {
            attackDamage = 4.5;
        } else if (item.getType().equals(Material.STONE_SPADE)) {
            attackDamage = 3.5;
        } else if (item.getType().equals(Material.GOLD_SPADE)) {
            attackDamage = 2.5;
        } else if (item.getType().equals(Material.WOOD_SPADE)) {
            attackDamage = 2.5;
        }

        //Player has strength effect
        int strenghteffectLVL = getBot().getEffectsManager().getLevel(PotionEffectType.INCREASE_DAMAGE);
        attackDamage += 3 * strenghteffectLVL;

        //Get sharpness enchantment level
        int sharpnessLVL = item.getEnchantmentLevel(Enchantment.DAMAGE_ALL);

        //Player has sharpness on item
        attackDamage += sharpnessLVL * 1.25;

        //Player has armour
        return attackDamage;
    }

    public void attack(Entity player) {
        getBot().getMovements().attack();
        int damage = (int) calculateItemAttackDamage(getBot().getInventoryManager().getItemInMainHand());
        if (getBot().getInventoryManager().getItemInMainHand() != null) {
            if (getBot().getInventoryManager().getItemInMainHand().getEnchantments().containsKey(Enchantment.FIRE_ASPECT)) {
                player.setFireTicks(80);
            }
        }

        ((Damageable) player).damage(damage, getBot().getNpc().getBukkitEntity());
        callEvent(new BotAttackEvent(getBot()));
        this.attacked = System.currentTimeMillis();
        this.lastAttackedOrDamaged = System.currentTimeMillis();
    }
}
