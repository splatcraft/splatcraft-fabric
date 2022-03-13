package net.splatcraft.impl.data;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.frame.api.toymaker.v0.generator.tag.AbstractTagGenerator;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.entity.SplatcraftEntityType;
import net.splatcraft.api.tag.SplatcraftEntityTypeTags;

public class EntityTagGenerator extends AbstractTagGenerator<EntityType<?>> {
    public EntityTagGenerator() {
        super(Splatcraft.MOD_ID, Registry.ENTITY_TYPE);
    }

    @Override
    public void generate() {
        this.add(SplatcraftEntityTypeTags.INK_PASSABLES,
            EntityType.PLAYER,
            SplatcraftEntityType.INK_SQUID,
            SplatcraftEntityType.INK_PROJECTILE
        );
        this.add(SplatcraftEntityTypeTags.HURT_BY_WATER,
            EntityType.PLAYER,
            SplatcraftEntityType.INK_SQUID,
            SplatcraftEntityType.INK_PROJECTILE
        );
        this.add(SplatcraftEntityTypeTags.HURT_BY_ENEMY_INK,
            EntityType.PLAYER,
            SplatcraftEntityType.INK_SQUID
        );

        this.add(SplatcraftEntityTypeTags.KILLED_BY_STAGE_VOID,
            EntityType.PLAYER,
            EntityType.ITEM,
            SplatcraftEntityType.INK_SQUID
        ).add(EntityTypeTags.IMPACT_PROJECTILES);

        this.add(EntityTypeTags.IMPACT_PROJECTILES,
            SplatcraftEntityType.INK_PROJECTILE
        );
    }
}
