package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static net.minecraft.data.client.ModelIds.*;
import static net.minecraft.data.client.VariantSettings.*;
import static net.splatcraft.api.block.SplatcraftBlocks.*;
import static net.splatcraft.api.item.SplatcraftItems.*;

public class ModelProvider extends FabricModelProvider {
    private BlockStateModelGenerator blockGen;
    private ItemModelGenerator itemGen;

    public ModelProvider(FabricDataGenerator gen) {
        super(gen);
    }

    private boolean isSplatcraft(Item item) {
        Identifier id = Registry.ITEM.getId(item);
        return id.getNamespace().equals(Splatcraft.MOD_ID);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator gen) {
        this.blockGen = gen;

        List.of(
            GRATE_BLOCK,
            STAGE_BARRIER,
            STAGE_VOID
        ).forEach(gen::registerSimpleCubeAll);

        List.of(
            EMPTY_INKWELL,
            INKWELL
        ).forEach(gen::registerSimpleState);

        List.of(
            INK_CLOTH_HELMET,
            INK_CLOTH_CHESTPLATE,
            INK_CLOTH_LEGGINGS,
            INK_CLOTH_BOOTS
        ).forEach(this::registerGeneratedOverlay);

        this.register(SplatcraftTexturedModels.CUBE_ALL_TINTED,
            CANVAS,
            INKED_BLOCK,
            GLOWING_INKED_BLOCK
        );

        this.registerGrate(GRATE);
        this.registerGrateRamp(GRATE_RAMP);

        StreamSupport.stream(SpawnEggItem.getAll().spliterator(), false)
                     .filter(this::isSplatcraft)
                     .forEach(item -> gen.registerParentedItemModel(item, getMinecraftNamespacedItem("template_spawn_egg")));
    }

    public BlockStateVariant createVariant(Identifier model) {
        return BlockStateVariant.create().put(MODEL, model);
    }

    public BlockStateVariant createVariantY(Rotation rotation) {
        return BlockStateVariant.create().put(Y, rotation);
    }

    public void registerGrate(Block... blocks) {
        for (Block block : blocks) {
            TextureMap textureMap = TextureMap.sideEnd(block);
            Identifier bottom = this.upload(SplatcraftModels.TEMPLATE_SIDED_TRAPDOOR_BOTTOM, block, textureMap);
            Identifier top = this.upload(SplatcraftModels.TEMPLATE_SIDED_TRAPDOOR_TOP, block, textureMap);
            this.accept(
                VariantsBlockStateSupplier.create(block)
                                          .coordinate(
                                              BlockStateVariantMap.create(Properties.BLOCK_HALF)
                                                                  .register(BlockHalf.BOTTOM, createVariant(bottom))
                                                                  .register(BlockHalf.TOP, createVariant(top))
                                          )
            );
            this.blockGen.registerParentedItemModel(block, bottom);
        }
    }

    public void registerGrateRamp(Block... blocks) {
        for (Block block : blocks) {
            Identifier model = ModelIds.getBlockModelId(block);
            this.accept(
                VariantsBlockStateSupplier.create(block, createVariant(model))
                                          .coordinate(
                                              BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                                                                  .register(Direction.EAST, createVariantY(Rotation.R180))
                                                                  .register(Direction.SOUTH, createVariantY(Rotation.R270))
                                                                  .register(Direction.WEST, BlockStateVariant.create())
                                                                  .register(Direction.NORTH, createVariantY(Rotation.R90))
                                          )
            );
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator gen) {
        this.itemGen = gen;

        this.register(Models.GENERATED,
            SPLATFEST_BAND,
            INK_SQUID_BANNER_PATTERN,
            OCTOLING_BANNER_PATTERN
        );

        this.registerInkTank(INK_TANK);
    }

    public void registerInkTank(Item... items) {
        for (Item item : items) {
            for (int i = 1; i <= 7; i++) {
                Identifier id = ModelIds.getItemSubModelId(item, "_" + i);
                this.upload(SplatcraftModels.INK_TANK, id, TextureMap.layer0(id));
            }
        }
    }

    public void registerGeneratedOverlay(Item... items) {
        for (Item item : items) this.upload(SplatcraftModels.GENERATED_OVERLAY, ModelIds.getItemModelId(item), SplatcraftTextureMaps.generatedOverlay(item));
    }

    public void register(Model model, Item... items) {
        for (Item item : items) this.itemGen.register(item, model);
    }

    public void register(TexturedModel.Factory model, Block... blocks) {
        for (Block block : blocks) this.blockGen.registerSingleton(block, model);
    }

    public void register(Model model, TextureMap textureMap, Block... blocks) {
        for (Block block : blocks) this.blockGen.registerSingleton(block, textureMap, model);
    }

    public void accept(BlockStateSupplier state) {
        this.blockGen.blockStateCollector.accept(state);
    }

    public Identifier upload(Model model, Block block, TextureMap textureMap) {
        return model.upload(block, textureMap, this.blockGen.modelCollector);
    }

    public Identifier upload(Model model, Block block, String suffix, TextureMap textureMap) {
        return model.upload(block, suffix, textureMap, this.blockGen.modelCollector);
    }

    public Identifier upload(Model model, Identifier id, TextureMap textureMap) {
        return model.upload(id, textureMap, this.blockGen.modelCollector);
    }

    public Identifier upload(TexturedModel.Factory model, Block block) {
        return model.upload(block, this.blockGen.modelCollector);
    }

    public Identifier upload(TexturedModel.Factory model, Block block, String suffix) {
        return model.upload(block, suffix, this.blockGen.modelCollector);
    }

    public interface SplatcraftTextureKeys {
        TextureKey LAYER1 = TextureKey.of("layer1");
    }

    public interface SplatcraftModels {
        Model CUBE_ALL_TINTED = block("cube_all_tinted", TextureKey.ALL);
        Model GENERATED_OVERLAY = item(Identifier.DEFAULT_NAMESPACE, "generated", TextureKey.LAYER0, SplatcraftTextureKeys.LAYER1);
        Model INK_TANK = item("ink_tank", TextureKey.LAYER0);
        Model TEMPLATE_SIDED_TRAPDOOR_TOP = block("template_sided_trapdoor_top", "_top", TextureKey.SIDE, TextureKey.END);
        Model TEMPLATE_SIDED_TRAPDOOR_BOTTOM = block("template_sided_trapdoor_bottom", "_bottom", TextureKey.SIDE, TextureKey.END);

        static Model make(TextureKey... keys) {
            return new Model(Optional.empty(), Optional.empty(), keys);
        }

        static Model block(String parent, TextureKey... keys) {
            return new Model(Optional.of(new Identifier(Splatcraft.MOD_ID, "block/" + parent)), Optional.empty(), keys);
        }

        static Model item(String namespace, String parent, TextureKey... keys) {
            return new Model(Optional.of(new Identifier(namespace, "item/" + parent)), Optional.empty(), keys);
        }

        static Model item(String parent, TextureKey... keys) {
            return item(Splatcraft.MOD_ID, parent, keys);
        }

        static Model block(String parent, String variant, TextureKey... keys) {
            return new Model(Optional.of(new Identifier(Splatcraft.MOD_ID, "block/" + parent)), Optional.of(variant), keys);
        }
    }

    public interface SplatcraftTexturedModels {
        TexturedModel.Factory CUBE_ALL_TINTED = TexturedModel.makeFactory(TextureMap::all, SplatcraftModels.CUBE_ALL_TINTED);
    }

    public interface SplatcraftTextureMaps {
        static TextureMap generatedOverlay(Item item) {
            return new TextureMap().put(TextureKey.LAYER0, ModelIds.getItemModelId(item))
                                   .put(SplatcraftTextureKeys.LAYER1, ModelIds.getItemSubModelId(item, "_overlay"));
        }
    }
}
