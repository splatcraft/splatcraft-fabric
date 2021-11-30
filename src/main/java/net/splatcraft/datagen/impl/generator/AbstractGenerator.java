package net.splatcraft.datagen.impl.generator;

import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractGenerator<T, U> implements Consumer<BiConsumer<T, U>> {
    protected final String modId;
    protected final Map<T, U> map;

    public AbstractGenerator(String modId) {
        this.modId = modId;
        this.map = Maps.newHashMap();
    }

    public abstract void generate();

    @Override
    public void accept(BiConsumer<T, U> biConsumer) {
        this.generate();
        this.map.forEach(biConsumer);
    }

    public void add(T id, U factory) {
        this.map.put(id, factory);
    }

    public Identifier getId(String id) {
        return new Identifier(this.modId, id);
    }
}
