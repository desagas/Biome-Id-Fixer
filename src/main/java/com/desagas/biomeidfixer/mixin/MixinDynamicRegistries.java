package com.desagas.biomeidfixer.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.registry.*;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO Inject code only in for loop, if possible to override only that.

// BYG, BoP, and Vanilla are assigned ids here
@Mixin(DynamicRegistries.class)
public abstract class MixinDynamicRegistries {

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/util/registry/DynamicRegistries;addBuiltinElements(Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/util/registry/WorldSettingsImport$IResourceAccess$RegistryAccess;Lnet/minecraft/util/registry/DynamicRegistries$CodecHolder;)V", cancellable = true)
    private static <E> void registerRegistry(DynamicRegistries.Impl p_243607_0_, WorldSettingsImport.IResourceAccess.RegistryAccess p_243607_1_, DynamicRegistries.CodecHolder<E> p_243607_2_, CallbackInfo callback) {
        RegistryKey<? extends Registry<E>> registrykey = p_243607_2_.key();
        boolean flag = !registrykey.equals(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY) && !registrykey.equals(Registry.DIMENSION_TYPE_REGISTRY);
        Registry<E> registry = BUILTIN.registryOrThrow(registrykey);
        if (!registrykey.equals(Registry.DIMENSION_TYPE_REGISTRY))
            registry = ((Registry<Registry<E>>)WorldGenRegistries.REGISTRY).get((RegistryKey<Registry<E>>)registrykey);
        MutableRegistry<E> mutableregistry = p_243607_0_.registryOrThrow(registrykey);

        for(Map.Entry<RegistryKey<E>, E> entry : registry.entrySet()) {
            E e = entry.getValue();

            // My changes start here. Very small.
            // Desagas added: if is biome registry below this line, add link to my path for id.

            // org.apache.logging.log4j.LogManager.getLogger().debug("Desagas: DynamicEntry - " + entry.getKey().getRegistryName().getPath().toString());
            boolean isBiomeReg = entry.getKey().getRegistryName().getPath().equals("worldgen/biome"); // ADD

            com.desagas.biomeidfixer.Write thisBiomeId = new com.desagas.biomeidfixer.Write(); // ADD

            if (flag) {
                if (isBiomeReg) { // ADD
                    p_243607_1_.add(BUILTIN, entry.getKey(), p_243607_2_.codec(), thisBiomeId.getOrTryBiomeAssignment(registry.getId(e), entry.getKey().location().toString()), e, registry.lifecycle(e));
                    // MODIFIED
                } else {
                    p_243607_1_.add(BUILTIN, entry.getKey(), p_243607_2_.codec(), registry.getId(e), e, registry.lifecycle(e));
                }
            } else {
                if (isBiomeReg) { // ADD
                    mutableregistry.registerMapping(thisBiomeId.getOrTryBiomeAssignment(registry.getId(e), entry.getKey().location().toString()), entry.getKey(), e, registry.lifecycle(e));
                    // MODIFIED
                } else {
                    mutableregistry.registerMapping(registry.getId(e), entry.getKey(), e, registry.lifecycle(e));
                }
            }
            //
            // Everything else is as is.
            //
        }

        callback.cancel();
    }

    @Shadow private static <E> void put(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistries.CodecHolder<?>> codecHolder, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec, Codec<E> codec2) { throw new IllegalStateException("Mixin failed to shadow put1()"); }
    @Shadow private static <E> void put(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistries.CodecHolder<?>> codecHolder, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec) { throw new IllegalStateException("Mixin failed to shadow put2()"); }
    // @Shadow private static <R extends Registry<?>> void getWorldGenRegistry(DynamicRegistries.Impl dynamicRegistries, RegistryKey<R> key) { throw new IllegalStateException("Mixin failed to shadow getWorldGenRegistry()"); };

    @Shadow private static final Map<RegistryKey<? extends Registry<?>>, DynamicRegistries.CodecHolder<?>> REGISTRIES = Util.make(() -> {
        ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistries.CodecHolder<?>> builder = ImmutableMap.builder();
        put(builder, Registry.DIMENSION_TYPE_REGISTRY, DimensionType.DIRECT_CODEC, DimensionType.DIRECT_CODEC);
        put(builder, Registry.BIOME_REGISTRY, Biome.DIRECT_CODEC, Biome.NETWORK_CODEC);
        put(builder, Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, ConfiguredSurfaceBuilder.DIRECT_CODEC);
        put(builder, Registry.CONFIGURED_CARVER_REGISTRY, ConfiguredCarver.DIRECT_CODEC);
        put(builder, Registry.CONFIGURED_FEATURE_REGISTRY, ConfiguredFeature.DIRECT_CODEC);
        put(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeature.DIRECT_CODEC);
        put(builder, Registry.PROCESSOR_LIST_REGISTRY, IStructureProcessorType.DIRECT_CODEC);
        put(builder, Registry.TEMPLATE_POOL_REGISTRY, JigsawPattern.DIRECT_CODEC);
        put(builder, Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, DimensionSettings.DIRECT_CODEC);
        return builder.build();
    });

    @Shadow private static final DynamicRegistries.Impl BUILTIN = Util.make(() -> {
        DynamicRegistries.Impl dynamicregistries$impl = new DynamicRegistries.Impl();
        DimensionType.registerBuiltin(dynamicregistries$impl);
        REGISTRIES.keySet().stream().filter((p_243616_0_) -> {
            return !p_243616_0_.equals(Registry.DIMENSION_TYPE_REGISTRY);
        }).forEach((p_243611_1_) -> {
            copyBuiltin(dynamicregistries$impl, p_243611_1_);
        });
        return dynamicregistries$impl;
    });

    @Shadow private static <R extends Registry<?>> void copyBuiltin(DynamicRegistries.Impl p_243609_0_, RegistryKey<R> p_243609_1_) {
        Registry<R> registry = (Registry<R>)WorldGenRegistries.REGISTRY;
        Registry<?> registry1 = registry.get(p_243609_1_);
        if (registry1 == null) {
            throw new IllegalStateException("Missing builtin registry: " + p_243609_1_);
        } else {
            copy(p_243609_0_, registry1);
        }
    }

    @Shadow private static <E> void copy(DynamicRegistries.Impl p_243606_0_, Registry<E> p_243606_1_) {
        MutableRegistry<E> mutableregistry = p_243606_0_.<E>registry(p_243606_1_.key()).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + p_243606_1_.key());
        });

        for(Map.Entry<RegistryKey<E>, E> entry : p_243606_1_.entrySet()) {
            E e = entry.getValue();
            mutableregistry.registerMapping(p_243606_1_.getId(e), entry.getKey(), e, p_243606_1_.lifecycle(e));
        }
    }
}
