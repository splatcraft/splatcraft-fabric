package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tag.EntityTypeTags;
import net.splatcraft.api.tag.SplatcraftEntityTypeTags;

import static net.minecraft.entity.EntityType.*;
import static net.splatcraft.api.entity.SplatcraftEntityType.*;

public class EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public EntityTypeTagProvider(FabricDataGenerator gen) {
        super(gen);
    }

    @Override
    protected void generateTags() {
        this.getOrCreateTagBuilder(EntityTypeTags.IMPACT_PROJECTILES).add(INK_PROJECTILE);

        // ink
        this.getOrCreateTagBuilder(SplatcraftEntityTypeTags.INK_PASSABLES).add(
            PLAYER,
            INK_SQUID,
            INK_PROJECTILE
        );

        // damage
        this.getOrCreateTagBuilder(SplatcraftEntityTypeTags.HURT_BY_WATER).add(
            PLAYER,
            INK_SQUID,
            INK_PROJECTILE
        );

        this.getOrCreateTagBuilder(SplatcraftEntityTypeTags.HURT_BY_ENEMY_INK).add(
            PLAYER,
            INK_SQUID
        );

        this.getOrCreateTagBuilder(SplatcraftEntityTypeTags.KILLED_BY_STAGE_VOID).add(
            PLAYER,
            ITEM,
            INK_SQUID
        ).addTag(EntityTypeTags.IMPACT_PROJECTILES);
    }
}
