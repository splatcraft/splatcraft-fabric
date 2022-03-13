package net.splatcraft.impl.util;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.api.item.SplatcraftItems;

import java.util.Optional;

import static net.splatcraft.api.util.SplatcraftUtil.*;

public final class IntegrationUtil {
    private IntegrationUtil() {}

    public static class Trinkets {
        /**
         * Checks for a {@link SplatcraftItems#SPLATFEST_BAND} in any trinket slot.
         */
        public static boolean checkSplatfestBand(PlayerEntity player) {
            if (!isModLoaded("trinkets")) return false;

            Optional<TrinketComponent> trinketso = TrinketsApi.getTrinketComponent(player);
            if (trinketso.isPresent()) {
                TrinketComponent trinkets = trinketso.get();
                return !trinkets.getEquipped(SplatcraftItems.SPLATFEST_BAND).isEmpty();
            }
            return false;
        }
    }
}
