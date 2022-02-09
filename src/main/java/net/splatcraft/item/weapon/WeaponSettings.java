package net.splatcraft.item.weapon;

import java.util.Objects;

public class WeaponSettings {
    protected final WeaponWeight weight; // TODO

    public WeaponSettings(WeaponWeight weight) {
        this.weight = weight;
    }

    public WeaponWeight getWeight() {
        return this.weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeaponSettings that = (WeaponSettings) o;
        return weight == that.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight);
    }

    @Override
    public String toString() {
        return "WeaponSettings{" + "weight=" + weight + '}';
    }
}
