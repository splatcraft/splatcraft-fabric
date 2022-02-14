package net.splatcraft.datagen;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.frame.api.toymaker.v0.generator.tag.AbstractTagGenerator;
import net.splatcraft.Splatcraft;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.tag.SplatcraftEntityTypeTags;

public class EntityTagGenerator extends AbstractTagGenerator<EntityType<?>> {
    public EntityTagGenerator() {
        super(Splatcraft.MOD_ID, Registry.ENTITY_TYPE);
    }

    @Override
    public void generate() {
        this.add(SplatcraftEntityTypeTags.INK_PASSABLES,
            EntityType.PLAYER,
            SplatcraftEntities.INK_SQUID,
            SplatcraftEntities.INK_PROJECTILE
        );
        this.add(SplatcraftEntityTypeTags.HURT_BY_WATER,
            EntityType.PLAYER,
            SplatcraftEntities.INK_SQUID,
            SplatcraftEntities.INK_PROJECTILE
        );
        this.add(SplatcraftEntityTypeTags.HURT_BY_ENEMY_INK,
            EntityType.PLAYER,
            SplatcraftEntities.INK_SQUID
        );

        this.add(SplatcraftEntityTypeTags.KILLED_BY_STAGE_VOID,
            EntityType.PLAYER,
            EntityType.ITEM,
            SplatcraftEntities.INK_SQUID
        ).add(EntityTypeTags.IMPACT_PROJECTILES);

        this.add(EntityTypeTags.IMPACT_PROJECTILES,
            SplatcraftEntities.INK_PROJECTILE
        );
    }
}
