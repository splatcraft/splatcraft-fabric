package net.splatcraft.datagen.impl.generator.model;

import net.splatcraft.datagen.impl.generator.AbstractGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("ConstantConditions")
public abstract class AbstractModelGenerator<T, U> extends AbstractGenerator<T, U> {
    public AbstractModelGenerator(String modId) {
        super(modId);
    }

    public abstract Registry<T> getRegistry();

    public Identifier name(T block, String nameFormat) {
        Identifier id = this.getRegistry().getId(block);
        return new Identifier(id.getNamespace(), String.format(nameFormat, id.getPath()));
    }

    public Identifier name(T block, String nameFormat, String omitSuffix) {
        Identifier id = this.getRegistry().getId(block);

        String path = id.getPath();
        if (path.endsWith(omitSuffix)) {
            path = path.substring(0, path.length() - omitSuffix.length());
        }

        return new Identifier(id.getNamespace(), String.format(nameFormat, path));
    }

    public Identifier name(T block, String nameFormat, String pattern, String reformat) {
        Identifier id = this.getRegistry().getId(block);

        String path = id.getPath();
        path = path.replaceAll(pattern, reformat);

        return new Identifier(id.getNamespace(), String.format(nameFormat, path));
    }

    public Identifier name(T block) {
        return name(block, "block/%s");
    }
}
