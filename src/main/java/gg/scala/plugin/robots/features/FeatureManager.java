package gg.scala.plugin.robots.features;

import gg.scala.plugin.robots.features.impl.*;
import gg.scala.plugin.robots.features.impl.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FeatureManager {
    final List<Feature> features;
    private final StrafeFeature strafeFeature;
    private final HealthPotionThrowing healthPotionThrowing;
    private final BuildingFeature buildingFeature;
    private final EatingAndDrinking eatingAndDrinking;
    private final Pearling pearling;
    private final WaterPlacementFeature waterPlacementFeature;

    public FeatureManager() {
        features = new ArrayList<>();

        addFeature(eatingAndDrinking = new EatingAndDrinking());
        addFeature(healthPotionThrowing = new HealthPotionThrowing());
        addFeature(new ArmorEquipFeature());
        addFeature(pearling = new Pearling());
        addFeature(strafeFeature = new StrafeFeature());
        addFeature(new Soups());
        addFeature(new WTapFeature());
        addFeature(new PreGap());
        addFeature(new RodFeature());
        addFeature(new EdgeFeature());
        addFeature(buildingFeature = new BuildingFeature());
        addFeature(new LiquidFeature());
        addFeature(new SuffocationFeature());
        addFeature(waterPlacementFeature = new WaterPlacementFeature());
    }

    public void addFeature(Feature feature) {
        features.add(feature);
    }
}
