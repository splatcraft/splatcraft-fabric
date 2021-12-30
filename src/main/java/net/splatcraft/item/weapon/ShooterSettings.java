package net.splatcraft.item.weapon;

public record ShooterSettings(int baseDamage, int range, int damage, float fireRate, float inkConsumption, float mobility, float projectileSize) {
    /**
     * @return the amount of times a shooter can
     *         be fired per second
     */
    @Override
    public float fireRate() {
        return this.fireRate;
    }

    public float calculateSpeed() {
        return (float) this.range() / 100;
    }

    public int calculateFireRateModulo() {
        return (int) (20 / this.fireRate());
    }
}
