package net.splatcraft.datagen.impl.generator.model.block;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.datagen.impl.generator.model.AbstractModelGenerator;
import net.splatcraft.datagen.impl.generator.model.InheritingModelGen;
import net.splatcraft.datagen.impl.generator.model.ModelGen;
import net.splatcraft.datagen.impl.generator.model.StateGen;
import net.splatcraft.datagen.impl.generator.model.StateModelInfo;

import java.util.function.Function;

@SuppressWarnings("unused")
public abstract class AbstractStateModelGenerator extends AbstractModelGenerator<Block, StateGen> {
    public AbstractStateModelGenerator(String modId) {
        super(modId);
    }

    @Override
    public Registry<Block> getRegistry() {
        return Registry.BLOCK;
    }

    public void add(Block block, Function<Block, StateGen> factory) {
        this.add(Registry.BLOCK.getId(block), factory.apply(block));
    }

    public void add(Block block) {
        this.add(block, this::cubeAll);
    }

    public StateGen empty(Block block) {
        return simple(name(block), ModelGen.EMPTY);
    }

    public StateGen predefined(Identifier name) {
        return VariantsStateGen.variants(StateModelInfo.create(name));
    }

    public StateGen predefined(Block block) {
        return predefined(name(block));
    }

    public StateGen simple(Identifier name, ModelGen model) {
        return VariantsStateGen.variants(StateModelInfo.create(name, model));
    }

    public StateGen alternate(Identifier name, ModelGen model1, ModelGen model2) {
        return VariantsStateGen.variants(StateModelInfo.create(Identifier.tryParse(name + "_1"), model1), StateModelInfo.create(Identifier.tryParse(name + "_2"), model2));
    }

    public StateGen cubeAll(Block block) {
        return simple(name(block), InheritingModelGen.cubeAll(name(block)));
    }

    public StateGen farmland(Identifier name, ModelGen model, ModelGen moist) {
        return VariantsStateGen.variants("moisture=0", StateModelInfo.create(name, model))
                               .variant("moisture=1", StateModelInfo.create(name, model))
                               .variant("moisture=2", StateModelInfo.create(name, model))
                               .variant("moisture=3", StateModelInfo.create(name, model))
                               .variant("moisture=4", StateModelInfo.create(name, model))
                               .variant("moisture=5", StateModelInfo.create(name, model))
                               .variant("moisture=6", StateModelInfo.create(name, model))
                               .variant("moisture=7", StateModelInfo.create(Identifier.tryParse(name + "_moist"), moist));
    }

    public StateGen netherPortal(Identifier name, ModelGen ns, ModelGen ew) {
        return VariantsStateGen.variants("axis=x", StateModelInfo.create(Identifier.tryParse(name + "_ns"), ns))
                               .variant("axis=z", StateModelInfo.create(Identifier.tryParse(name + "_ew"), ew));
    }

    public StateGen dualConnecting(Identifier name) {
        return VariantsStateGen.variants("connection=none", StateModelInfo.create(name, InheritingModelGen.cubeAll(name)))
                               .variant("connection=up", StateModelInfo.create(Identifier.tryParse(name + "_up"), InheritingModelGen.cubeColumn(name, Identifier.tryParse(name + "_bottom"))))
                               .variant("connection=down", StateModelInfo.create(Identifier.tryParse(name + "_down"), InheritingModelGen.cubeColumn(name, Identifier.tryParse(name + "_top"))))
                               .variant("connection=north", StateModelInfo.create(Identifier.tryParse(name + "_north"), InheritingModelGen.cube(name, Identifier.tryParse(name + "_left"), name, Identifier.tryParse(name + "_right"), Identifier.tryParse(name + "_bottom"), Identifier.tryParse(name + "_top"))))
                               .variant("connection=south", StateModelInfo.create(Identifier.tryParse(name + "_south"), InheritingModelGen.cube(name, Identifier.tryParse(name + "_right"), name, Identifier.tryParse(name + "_left"), Identifier.tryParse(name + "_top"), Identifier.tryParse(name + "_bottom"))))
                               .variant("connection=west", StateModelInfo.create(Identifier.tryParse(name + "_east"), InheritingModelGen.cube(Identifier.tryParse(name + "_left"), name, Identifier.tryParse(name + "_right"), name, Identifier.tryParse(name + "_right"), Identifier.tryParse(name + "_right"))))
                               .variant("connection=east", StateModelInfo.create(Identifier.tryParse(name + "_west"), InheritingModelGen.cube(Identifier.tryParse(name + "_right"), name, Identifier.tryParse(name + "_left"), name, Identifier.tryParse(name + "_left"), Identifier.tryParse(name + "_left"))));
    }

    public StateGen axisRotated(Identifier name, ModelGen model) {
        return VariantsStateGen.variants("axis=y", StateModelInfo.create(name, model).rotate(0, 0))
                               .variant("axis=z", StateModelInfo.create(name, model).rotate(90, 0))
                               .variant("axis=x", StateModelInfo.create(name, model).rotate(90, 90));
    }

    public StateGen facingRotated(Identifier name, ModelGen model) {
        return VariantsStateGen.variants("facing=up", StateModelInfo.create(name, model).rotate(0, 0))
                               .variant("facing=down", StateModelInfo.create(name, model).rotate(180, 0))
                               .variant("facing=north", StateModelInfo.create(name, model).rotate(90, 0))
                               .variant("facing=east", StateModelInfo.create(name, model).rotate(90, 90))
                               .variant("facing=south", StateModelInfo.create(name, model).rotate(90, 180))
                               .variant("facing=west", StateModelInfo.create(name, model).rotate(90, 270));
    }

    public StateGen facingHorizontalRotated(Identifier name, ModelGen model) {
        return VariantsStateGen.variants("facing=north", StateModelInfo.create(name, model).rotate(0, 0))
                               .variant("facing=east", StateModelInfo.create(name, model).rotate(0, 90))
                               .variant("facing=south", StateModelInfo.create(name, model).rotate(0, 180))
                               .variant("facing=west", StateModelInfo.create(name, model).rotate(0, 270));
    }

    public StateGen randomRotationY(Identifier name, ModelGen model) {
        return VariantsStateGen.variants(
            StateModelInfo.create(name, model).rotate(0, 0),
            StateModelInfo.create(name, model).rotate(0, 90),
            StateModelInfo.create(name, model).rotate(0, 180),
            StateModelInfo.create(name, model).rotate(0, 270)
        );
    }

    public StateGen snowyBlock(Identifier name, ModelGen model, ModelGen snowy) {
        return VariantsStateGen.variants(
            "snowy=false",
            StateModelInfo.create(name, model)
        ).variant(
            "snowy=true",
            StateModelInfo.create(Identifier.tryParse(name + "_snowy"), snowy)
        );
    }

    public StateGen randomRotationXY(Identifier name, ModelGen model) {
        return VariantsStateGen.variants(
            StateModelInfo.create(name, model).rotate(0, 0),
            StateModelInfo.create(name, model).rotate(0, 90),
            StateModelInfo.create(name, model).rotate(0, 180),
            StateModelInfo.create(name, model).rotate(0, 270),
            StateModelInfo.create(name, model).rotate(90, 0),
            StateModelInfo.create(name, model).rotate(90, 90),
            StateModelInfo.create(name, model).rotate(90, 180),
            StateModelInfo.create(name, model).rotate(90, 270),
            StateModelInfo.create(name, model).rotate(180, 0),
            StateModelInfo.create(name, model).rotate(180, 90),
            StateModelInfo.create(name, model).rotate(180, 180),
            StateModelInfo.create(name, model).rotate(180, 270),
            StateModelInfo.create(name, model).rotate(270, 0),
            StateModelInfo.create(name, model).rotate(270, 90),
            StateModelInfo.create(name, model).rotate(270, 180),
            StateModelInfo.create(name, model).rotate(270, 270)
        );
    }

    public StateGen doubleBlock(Identifier lower, ModelGen lowerModel, Identifier upper, ModelGen upperModel) {
        return VariantsStateGen.variants("half=lower", StateModelInfo.create(lower, lowerModel))
                               .variant("half=upper", StateModelInfo.create(upper, upperModel));
    }
}
