package net.splatcraft.api.item.weapon;

import net.splatcraft.api.item.UsageSpeedProvider;
import net.splatcraft.api.item.weapon.settings.WeaponSettings;
import net.splatcraft.api.item.weapon.settings.WeaponWeight;

public class RollerItem extends WeaponItem implements UsageSpeedProvider {
    public RollerItem(Settings settings) {
        super(settings);
    }

    @Override
    public WeaponSettings getWeaponSettings() {
        return new WeaponSettings(WeaponWeight.LIGHTWEIGHT); // TODO
    }

    @Override
    public Pose getWeaponPose() {
        return Pose.ROLLING;
    }
}
