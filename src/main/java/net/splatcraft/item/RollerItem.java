package net.splatcraft.item;

public class RollerItem extends WeaponItem {
    public RollerItem(Settings settings) {
        super(settings);
    }

    @Override
    public float getUsageMobility() {
        return 1.08f;
    }

    @Override
    public Pose getWeaponPose() {
        return Pose.ROLLING;
    }
}
