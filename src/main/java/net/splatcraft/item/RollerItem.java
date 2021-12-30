package net.splatcraft.item;

public class RollerItem extends WeaponItem {
    public RollerItem(Settings settings) {
        super(settings);
    }

    @Override
    public float getMobility() {
        return 1.08f;
    }
}
