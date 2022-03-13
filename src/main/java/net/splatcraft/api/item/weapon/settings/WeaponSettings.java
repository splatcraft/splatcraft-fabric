package net.splatcraft.api.item.weapon.settings;

public class WeaponSettings {
    protected final WeaponWeight weight;

    public WeaponSettings(WeaponWeight weight) {
        this.weight = weight;
    }

    public WeaponWeight getWeight() {
        return this.weight;
    }

    @Override
    public String toString() {
        return "WeaponSettings{" + "weight=" + weight + '}';
    }

    @FunctionalInterface public interface Provider { WeaponSettings getWeaponSettings(); }
}
