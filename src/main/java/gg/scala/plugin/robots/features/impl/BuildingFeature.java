package gg.scala.plugin.robots.features.impl;

import gg.scala.plugin.robots.bot.behavior.ExecutionBehavior;
import gg.scala.plugin.robots.bot.behavior.actions.BuildingAction;
import gg.scala.plugin.robots.bot.behavior.actions.GoldenAppleConsumeAction;
import gg.scala.practice.games.GameService;
import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.features.Feature;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Schedulers;
import me.lucko.helper.random.RandomSelector;
import me.lucko.helper.random.Weighted;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class BuildingFeature extends Feature {

    @Override
    public void tickEvent(Bot bot) {
        if (!(!bot.getInventoryManager().hasItem(Material.WOOD).isEmpty() || !bot.getInventoryManager().hasItem(Material.COBBLESTONE).isEmpty() || !bot.getInventoryManager().hasItem(Material.WOOL).isEmpty()))
            return;

        if (bot.getMovements().getLocation().getBlock().getType().isSolid()) {
            if (bot.getMovements().getLocation().clone().add(0, 1, 0).getBlock().getType().isSolid()
                    || bot.getMovements().getLocation().clone().add(0, 2, 0).getBlock().getType().isSolid()) {
            } else {
                bot.getMovements().getLocation().add(0, 1, 0);
                bot.getMovements().updatedLoc();
            }
        }

        boolean enabled = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getEnabled());
        if (!enabled) {
            return;
        }

        if (bot.isCurrentlyBuilding()) {
            bot.setLastPlaced(System.currentTimeMillis());
            return;
        }

        double minDistanceFromPlayer = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getMinDistanceFromPlayer());
        int placeCooldown = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getPlaceCooldown());
        int buildingTicks = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildingTicks());
        boolean buildWhenCombo = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenCombo());
        int buildWhenComboHits = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenComboHits());

        var distanceToPlayer = bot.getMovements().getLocation().distance(bot.getMovements().getTargetLocate().getTargetEntity().getLocation());
        var currentComboBots = bot.getCombo();
        var currentTime = System.currentTimeMillis();

        boolean buildWhenPlayerIsHigh = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenPlayerIsHigh());
        int buildWhenPlayerIsHighBlocks = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenPlayerIsHighBlocks());
        int buildUpTicks = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildingUpTicks());

//        bot.getPlayer().sendMessage(
//                BuildingUtilitiesKt.isMovingTowards(
//                        bot.getMovements().getTargetLocate().getTargetEntity().getLocation(),
//                        bot.getMovements().getLocation()
//                ) + " | " + BuildingUtilitiesKt.isInLineOfSight(
//                        bot.getMovements().getLocation().getDirection(),
//                        bot.getMovements().getLocation(),
//                        bot.getMovements().getTargetLocate().getTargetEntity().getLocation(),
//                        40.0
//                ) + " | " + (Math.floor(bot.getMovements().getLocation().getY()) == Math.floor(bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY()))
//        );

        boolean shouldBuild = ((buildWhenCombo && currentComboBots >= buildWhenComboHits && bot.getDamageManager().getDamageTicks() < 10) || distanceToPlayer <= minDistanceFromPlayer)
                && currentTime - placeCooldown > bot.getLastPlaced()
                && BuildingUtilitiesKt.isMovingTowards(
                bot.getMovements().getTargetLocate().getTargetEntity().getLocation(),
                bot.getMovements().getLocation()
        )
                && BuildingUtilitiesKt.isInLineOfSight(
                bot.getMovements().getLocation().getDirection(),
                bot.getMovements().getLocation(),
                bot.getMovements().getTargetLocate().getTargetEntity().getLocation(),
                40.0
        )
                && Math.floor(bot.getMovements().getLocation().getY()) == Math.floor(bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY());

        var buildLimit = bot.getGame().buildLimit();
        if (buildLimit == null) {
            buildLimit = 16;
        }

        if (buildWhenPlayerIsHigh && Math.max(bot.getMovements().getLocation().getY(), bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY())
                - Math.min(bot.getMovements().getLocation().getY(), bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY()) >= buildWhenPlayerIsHighBlocks
                && bot.getMovements().getLocation().getY() + 1.0 < buildLimit) {
            Location loc1 = bot.getMovements().getLocation().clone();
            loc1.setY(0);
            Location loc2 = bot.getMovements().getTargetLocate().getTargetEntity().getLocation().clone();
            loc2.setY(0);

            if (loc1.distance(loc2) < minDistanceFromPlayer) {
                shouldBuild = true;
            }
        }

        if (!shouldBuild) {
            return;
        }

        bot.getActionController().runAction(
                BuildingAction.INSTANCE,
                (configuration) -> {
                    configuration.setExecutionBehavior(ExecutionBehavior.Block);
                    configuration.setShouldContinueMovement(true);
                    return null;
                }
        );
    }

    public void build(Bot bot, Terminable terminable, Consumer<Boolean> switchSlow) {
        double minDistanceFromPlayer = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getMinDistanceFromPlayer());
        int placeCooldown = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getPlaceCooldown());
        int buildingTicks = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildingTicks());
        boolean buildWhenCombo = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenCombo());
        int buildWhenComboHits = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenComboHits());

        var distanceToPlayer = bot.getMovements().getLocation().distance(bot.getMovements().getTargetLocate().getTargetEntity().getLocation());
        var currentComboBots = bot.getCombo();
        var currentTime = System.currentTimeMillis();

        boolean buildWhenPlayerIsHigh = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenPlayerIsHigh());
        int buildWhenPlayerIsHighBlocks = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenPlayerIsHighBlocks());
        int buildUpTicks = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildingUpTicks());

        final boolean buildUp;
        final int finalTicks;

        int delay = 0;

        var buildLimit = bot.getGame().buildLimit();
        if (buildLimit == null) {
            buildLimit = 16;
        }

        if (buildWhenPlayerIsHigh && Math.max(bot.getMovements().getLocation().getY(), bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY())
                - Math.min(bot.getMovements().getLocation().getY(), bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY()) >= buildWhenPlayerIsHighBlocks
                && bot.getMovements().getLocation().getY() + 1.0 < buildLimit) {
            Location loc1 = bot.getMovements().getLocation().clone();
            loc1.setY(0);
            Location loc2 = bot.getMovements().getTargetLocate().getTargetEntity().getLocation().clone();
            loc2.setY(0);

            if (loc1.distance(loc2) < minDistanceFromPlayer) {
                buildUp = true;
                finalTicks = buildUpTicks;
                delay = 20;
            } else {
                finalTicks = buildingTicks;
                buildUp = false;
            }
        } else {
            finalTicks = buildingTicks;
            buildUp = false;
        }

        PlacementConfiguration placementConfiguration = new PlacementConfiguration().random().updateLocations(bot);
        // AtomicInteger previousSlot = new AtomicInteger(bot.getInventoryManager().getMainHand());

        Schedulers
                .sync()
                .runRepeating((task) -> {
                    Location location = bot.getMovements().getLocation();
                    if (bot.getInventoryManager().isEating()) return;

                    if (!buildUp) {
                        if (!placementConfiguration.hasNext()) {
                            bot.setCurrentlyBuilding(false);
                            // bot.getInventoryManager().setMainHand(previousSlot.get());
                            bot.getBlockedLocations().clear();
                            bot.setLastPlaced(System.currentTimeMillis());
                            switchSlow.accept(true);

                            task.closeAndReportException();
                            terminable.closeAndReportException();
                            return;
                        }

                        location = placementConfiguration.next();

                        if (location.getBlock().getType().isSolid()
                                || BuildingUtilitiesKt.isLocationInsidePlayerHitbox(location, bot.getMovements().getTargetLocate().getTargetEntity())
                                || location.distance(bot.getMovements().getLocation()) < 0.2
                                || location.distance(bot.getMovements().getTargetLocate().getTargetEntity().getLocation()) < 0.2) {
                            Location betterLocation = null;
                            bot.getBlockedLocations().add(location);
                            int index = 0;
                            while (betterLocation == null) {
                                if (index > 15) break;
                                betterLocation = findBetterStartLocation(bot, bot.getMovements().getLocation(), placementConfiguration.buildAction.getSize());
                                index++;
                            }

                            if (betterLocation == null) {
                                bot.setCurrentlyBuilding(false);
                                // bot.getInventoryManager().setMainHand(previousSlot.get());
                                bot.getBlockedLocations().clear();
                                bot.setLastPlaced(System.currentTimeMillis());
                                switchSlow.accept(true);
                                task.closeAndReportException();
                                terminable.closeAndReportException();
                                return;
                            }

                            placementConfiguration.refresh(bot, betterLocation);
                            if (!placementConfiguration.hasNext()) return;
                            location = placementConfiguration.next();
                        }
                    } else {
                        if (System.currentTimeMillis() - (finalTicks * 50L) < bot.getLastPlacedBlockUp()) return;
                        bot.setLastPlacedBlockUp(System.currentTimeMillis());
                        bot.getMovements().setLockLocation(true);

                        if (!location.clone().subtract(0, 1, 0).getBlock().getType().isSolid() || Math.max(bot.getMovements().getLocation().getY(), bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY())
                                - Math.min(bot.getMovements().getLocation().getY(), bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY()) < buildWhenPlayerIsHighBlocks
                                || location.clone().add(0, 1, 0).getBlock().getType().isSolid()
                                || location.clone().add(0, 2, 0).getBlock().getType().isSolid()
                                || location.clone().add(0, 3, 0).getBlock().getType().isSolid()) {

                            bot.setCurrentlyBuilding(false);
                            // bot.getInventoryManager().setMainHand(previousSlot.get());
                            bot.getBlockedLocations().clear();
                            bot.setLastPlaced(System.currentTimeMillis());
                            bot.getMovements().setLockLocation(false);
                            switchSlow.accept(true);
                            task.closeAndReportException();
                            terminable.closeAndReportException();
                            return;
                        } else {
                            Location loc1 = bot.getMovements().getLocation().clone();
                            loc1.setY(0);
                            Location loc2 = bot.getMovements().getTargetLocate().getTargetEntity().getLocation().clone();
                            loc2.setY(0);

                            if (loc1.distance(loc2) > minDistanceFromPlayer) {
                                bot.setCurrentlyBuilding(false);
                                // bot.getInventoryManager().setMainHand(previousSlot.get());
                                bot.getBlockedLocations().clear();
                                bot.setLastPlaced(System.currentTimeMillis());
                                bot.getMovements().setLockLocation(false);
                                switchSlow.accept(true);
                                task.closeAndReportException();
                                terminable.closeAndReportException();
                                return;
                            }
                        }
                    }

                    bot.setCurrentlyBuilding(true);

//                    List<ItemStack> blocks = BuildingAction.INSTANCE.getBlocks(bot);
//
//                    if (blocks.isEmpty()) return
//
//                    bot.getInventoryManager().setMainHand(
//                            bot.getInventoryManager().getItemSlot(blocks.get(0))
//                    );

                    var blockType = bot.getInventoryManager().getItemInMainHand().getType();
                    location.getBlock().setType(blockType);
                    location.getBlock().setMetadata(
                            "placed",
                            new FixedMetadataValue(GameService.INSTANCE.getPlugin(), true)
                    );

                    var yawPitch = getYawPitch(bot.getMovements().getLocation(), location);

                    if (buildUp) {
                        bot.getMovements().rotate(bot.getMovements().getLocation().getYaw(), 90);
                    } else {
                        bot.getMovements().getRotations().setRotationLockUntil(System.currentTimeMillis() + 500L);
                        bot.getMovements().rotate(yawPitch[0], yawPitch[1]);
                    }

                    var block = net.minecraft.server.v1_8_R3.Block.getById(location.getBlock().getTypeId());

                    bot.getGame().getPlacedBlocks().add(location.getBlock().getLocation().toVector());
                    bot.getMovements().getTargetLocate().getTargetEntity()
                            .playSound(
                                    location, block.stepSound.getPlaceSound(), 1, 1
                            );
                }, delay, finalTicks)
                .bindWith(bot.getTerminable());
    }

    public Location findBetterStartLocation(Bot bot, Location botLocation, Vector size) {
        return findBetterStartLocation(bot, botLocation, (int) size.getX(), (int) size.getY(), (int) size.getZ());
    }

    public Location findBetterStartLocation(Bot bot, Location botLocation, int width, int height, int depth) {
        int reach = 4;

        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                for (int dy = -reach; dy <= reach; dy++) {
                    Location candidateLocation = botLocation.clone().add(dx, dy, dz);

                    if (containsLocation(bot, candidateLocation)) continue;

                    if (isNonSolidArea(candidateLocation, width, height, depth)) {
                        return candidateLocation;
                    }
                }
            }
        }

        return null;
    }

    public boolean containsLocation(Bot bot, Location location) {
        for (Location loc : bot.getBlockedLocations()) {
            if (loc.distance(location) < 0.1) return true;
        }

        return false;
    }

    private boolean isNonSolidArea(Location location, int width, int height, int depth) {
        World world = location.getWorld();
        int startX = location.getBlockX();
        int startY = location.getBlockY();
        int startZ = location.getBlockZ();

        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                for (int z = startZ; z < startZ + depth; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    if (block.getType().isSolid()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Derived from https://github.com/CitizensDev/Citizens/blob/master/src/core/net/citizensnpcs/resources/npclib/NPCManager.java#L42
     */
    public static float[] getYawPitch(Location from, Location to) {
        double xDiff = to.getX() - from.getX();
        double yDiff = to.getY() - from.getY();
        double zDiff = to.getZ() - from.getZ();
        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double yaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
        double pitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
        if (zDiff < 0.0) {
            yaw = yaw + (Math.abs(180 - yaw) * 2);
        }
        return new float[]{(float) yaw - 90, (float) pitch};
    }

    @RequiredArgsConstructor
    public static class PlacementConfiguration {
        private ExitComboBuildAction buildAction;
        private List<Location> locations = new ArrayList<>();
        private int current = -1;


        public PlacementConfiguration random() {
            this.buildAction = ExitComboBuildAction.random();
            return this;
        }

        public Location next() {
            current++;
            return locations.get(current);
        }

        public boolean hasNext() {
            return current + 1 <= locations.size() - 1;
        }

        public PlacementConfiguration setBuildAction(ExitComboBuildAction buildAction) {
            this.buildAction = buildAction;
            return this;
        }

        public PlacementConfiguration refresh(Bot bot, Location... sl) {
            buildAction = ExitComboBuildAction.random();
            current = 0;
            updateLocations(bot, sl);

            return this;
        }

        public PlacementConfiguration updateLocations(Bot bot, Location... sl) {
            List<Location> result = new ArrayList<>();
            var selfLocation = bot.getMovements().getLocation().clone();
            var selfDirection = selfLocation.getDirection();
            var targetLocation = bot.getMovements().getTargetLocate().getTargetEntity().getLocation().clone();

            var startLocation = sl.length > 0 ? sl[0] : buildAction.blockPlayer ? targetLocation.clone().add(targetLocation.getDirection().clone()) :
                    selfLocation.clone().add(selfDirection.clone().multiply(1.5));

            if (startLocation.getBlock().getType().isSolid()) {
                for (var i = 1; i <= 10; i++) {
                    var above = startLocation.clone().add(0, i, 0);
                    if (!above.getBlock().getType().isSolid()) {
                        startLocation = above;
                        break;
                    }
                }
            }

            for (var i = 1; i <= 10; i++) {
                var beneath = startLocation.clone().subtract(0, i, 0);
                if (!beneath.getBlock().getType().isSolid()) startLocation = beneath;
                else {
                    break;
                }
            }

            result.add(startLocation);
            Location botLocation = bot.getMovements().getLocation();

            Location lastLoc = startLocation.clone();
            for (String placementPosition : buildAction.getPlacementDirections().keySet()) {
                int amountToPlace = buildAction.getPlacementDirections().get(placementPosition);
                for (int level = 1; level <= amountToPlace; level++) {
                    lastLoc = getRelativeLocation(
                            lastLoc.clone(),
                            placementPosition, 1,
                            getDirectionFromYaw(botLocation.getYaw())
                    );
                    result.add(lastLoc.clone());
                }
            }

            locations = result;

            return this;
        }

        private Location getRelativeLocation(Location base, String direction, int distance, Direction facing) {
            if (direction.contains("UP")) {
                base.add(0, distance, 0);
            }
            if (direction.contains("DOWN")) {
                base.add(0, -distance, 0);
            }
            if (direction.contains("LEFT")) {
                getLeft(base, distance, facing);
            }
            if (direction.contains("RIGHT")) {
                getRight(base, distance, facing);
            }
            if (direction.contains("FORWARDS")) {
                getForwards(base, distance, facing);
            }
            if (direction.contains("BACKWARDS")) {
                getBackwards(base, distance, facing);
            }
//            return switch (direction.toUpperCase()) {
//                case "UP" -> base.add(0, distance, 0);
//                case "DOWN" -> base.add(0, -distance, 0);
//                case "LEFT" -> getLeft(base, distance, facing);
//                case "RIGHT" -> getRight(base, distance, facing);
//                case "FORWARDS" -> getForwards(base, distance, facing);
//                case "BACKWARDS" -> getBackwards(base, distance, facing);
//                default -> throw new IllegalArgumentException("Invalid direction: " + direction);
//            };

            return base;
        }

        private Location getLeft(Location base, int distance, Direction facing) {
            return switch (facing) {
                case NORTH -> base.add(-distance, 0, 0);
                case SOUTH -> base.add(distance, 0, 0);
                case EAST -> base.add(0, 0, -distance);
                default -> base.add(0, 0, distance);
            };
        }

        private Location getRight(Location base, int distance, Direction facing) {
            return switch (facing) {
                case NORTH -> base.add(distance, 0, 0);
                case SOUTH -> base.add(-distance, 0, 0);
                case EAST -> base.add(0, 0, distance);
                default -> base.add(0, 0, -distance);
            };
        }

        private Location getForwards(Location base, int distance, Direction facing) {
            return switch (facing) {
                case NORTH -> base.add(0, 0, -distance);
                case SOUTH -> base.add(0, 0, distance);
                case EAST -> base.add(distance, 0, 0);
                default -> base.add(-distance, 0, 0);
            };
        }

        private Location getBackwards(Location base, int distance, Direction facing) {
            return switch (facing) {
                case NORTH -> base.add(0, 0, distance);
                case SOUTH -> base.add(0, 0, -distance);
                case EAST -> base.add(-distance, 0, 0);
                default -> base.add(distance, 0, 0);
            };
        }

        private Direction getDirectionFromYaw(float yaw) {
            if (yaw >= -135 && yaw < -45) {
                return Direction.EAST;
            }

            if (yaw >= -45 && yaw < 45) {
                return Direction.SOUTH;
            }

            if (yaw >= 45 && yaw < 135) {
                return Direction.WEST;
            }

            return Direction.NORTH;
        }

        public enum Direction {
            NORTH, SOUTH, EAST, WEST
        }

        @Getter
        public enum ExitComboBuildAction implements Weighted {
            PLAYER_BLOCK(new HashMap<>() {{
                put("UP", 1);
            }}, 50, true),
            LEFT(new HashMap<>() {{
                put("LEFT", 1);
            }}, 15, true),
            RIGHT(new HashMap<>() {{
                put("RIGHT", 1);
            }}, 15, true),
            DIAGONAL_LEFT(new HashMap<>() {{
                put("LEFT FORWARDS", 1);
            }}, 30, true),
            DIAGONAL_RIGHT(new HashMap<>() {{
                put("RIGHT FORWARDS", 1);
            }}, 30, true),
            L_SHAPE(new HashMap<>() {{
                put("UP", 1);
                put("LEFT", 2);
            }}, 20, true);

            private static final List<ExitComboBuildAction> ENTRIES = Arrays.asList(values());
            private static final RandomSelector<ExitComboBuildAction> WEIGHTED_RANDOM = RandomSelector.weighted(ENTRIES);

            @NotNull
            public static ExitComboBuildAction random() {
                return WEIGHTED_RANDOM.pick();
            }

            private final int chance;
            private final Map<String, Integer> placementDirections;
            private final boolean blockPlayer;

            ExitComboBuildAction(Map<String, Integer> placementDirections, int chance, boolean blockPlayer) {
                this.placementDirections = placementDirections;
                this.chance = chance;
                this.blockPlayer = blockPlayer;
            }

            @Override
            public double getWeight() {
                return chance;
            }

            public int getTotalBlocks() {
                int total = 0;

                for (String key : placementDirections.keySet()) {
                    total += placementDirections.get(key);
                }

                return total;
            }

            public Vector getSize() {
                int x = 1, y = 0, z = 0;

                for (String key : placementDirections.keySet()) {
                    if (key.equalsIgnoreCase("LEFT") || key.equalsIgnoreCase("RIGHT"))
                        x += placementDirections.get(key);
                    if (key.equalsIgnoreCase("UP") || key.equalsIgnoreCase("DOWN")) y += placementDirections.get(key);
                    if (key.equalsIgnoreCase("BACKWARDS") || key.equalsIgnoreCase("FORWARDS"))
                        z += placementDirections.get(key);
                }

                return new Vector(x, y, z);
            }
        }
    }
}
