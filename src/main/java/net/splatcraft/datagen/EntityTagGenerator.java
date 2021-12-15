package net.splatcraft.datagen;

import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;
import net.splatcraft.datagen.impl.generator.tag.AbstractTagGenerator;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.tag.SplatcraftEntityTypeTags;

public class EntityTagGenerator extends AbstractTagGenerator<EntityType<?>> {
    public EntityTagGenerator() {
        super(Splatcraft.MOD_ID, Registry.ENTITY_TYPE);
    }

    @Override
    public void generate() {
        this.add(SplatcraftEntityTypeTags.INK_PASSABLES,
            EntityType.SQUID, EntityType.GLOW_SQUID
        );
        this.add(SplatcraftEntityTypeTags.HURT_BY_WATER,
            SplatcraftEntities.INK_SQUID
        );
        this.add(SplatcraftEntityTypeTags.HURT_BY_ENEMY_INK,
            SplatcraftEntities.INK_SQUID
        );
    }
}
