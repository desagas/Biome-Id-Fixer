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

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/util/registry/DynamicRegistries;registerRegistry(Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/util/registry/WorldSettingsImport$IResourceAccess$RegistryAccess;Lnet/minecraft/util/registry/DynamicRegistries$CodecHolder;)V", cancellable = true)
    private static <E> void registerRegistry(DynamicRegistries.Impl dynamicRegistries, WorldSettingsImport.IResourceAccess.RegistryAccess registryAccess, DynamicRegistries.CodecHolder<E> codecHolder, CallbackInfo callback) {
        RegistryKey<? extends Registry<E>> registrykey = codecHolder.getRegistryKey();
        boolean flag = !registrykey.equals(Registry.NOISE_SETTINGS_KEY) && !registrykey.equals(Registry.DIMENSION_TYPE_KEY);
        Registry<E> registry = registries.getRegistry(registrykey);
        if (!registrykey.equals(Registry.DIMENSION_TYPE_KEY))
            registry = ((Registry<Registry<E>>)WorldGenRegistries.ROOT_REGISTRIES).getValueForKey((RegistryKey<Registry<E>>)registrykey);
        MutableRegistry<E> mutableregistry = dynamicRegistries.getRegistry(registrykey);

        for(Map.Entry<RegistryKey<E>, E> entry : registry.getEntries()) {
            E e = entry.getValue();

            //
            // My changes start here. Very small.
            // Desagas added: if is biome registry below this line, and link to my path for id.
            //
            boolean isBiomeReg = entry.getKey().getRegistryName().getPath().equals("worldgen/biome"); // ADD

            com.desagas.biomeidfixer.Write thisBiomeId = new com.desagas.biomeidfixer.Write(); // ADD

            if (flag) {
                if (isBiomeReg) { // ADD
                    registryAccess.encode(registries, entry.getKey(), codecHolder.getRegistryCodec(), thisBiomeId.getOrTryBiomeAssignment(registry.getId(e), entry.getKey().getLocation().toString()), e, registry.getLifecycleByRegistry(e));
                    // MODIFIED
                } else {
                    registryAccess.encode(registries, entry.getKey(), codecHolder.getRegistryCodec(), registry.getId(e), e, registry.getLifecycleByRegistry(e));
                }
            } else {
                if (isBiomeReg) { // ADD
                    mutableregistry.register(thisBiomeId.getOrTryBiomeAssignment(registry.getId(e), entry.getKey().getLocation().toString()), entry.getKey(), e, registry.getLifecycleByRegistry(e));
                    // MODIFIED
                } else {
                    mutableregistry.register(registry.getId(e), entry.getKey(), e, registry.getLifecycleByRegistry(e));
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
    @Shadow private static <R extends Registry<?>> void getWorldGenRegistry(DynamicRegistries.Impl dynamicRegistries, RegistryKey<R> key) { throw new IllegalStateException("Mixin failed to shadow getWorldGenRegistry()"); };

    @Shadow private static final Map<RegistryKey<? extends Registry<?>>, DynamicRegistries.CodecHolder<?>> registryCodecMap = Util.make(() -> {
        ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistries.CodecHolder<?>> builder = ImmutableMap.builder();
        put(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, DimensionType.CODEC);
        put(builder, Registry.BIOME_KEY, Biome.CODEC, Biome.PACKET_CODEC);
        put(builder, Registry.CONFIGURED_SURFACE_BUILDER_KEY, ConfiguredSurfaceBuilder.field_237168_a_);
        put(builder, Registry.CONFIGURED_CARVER_KEY, ConfiguredCarver.field_236235_a_);
        put(builder, Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeature.field_242763_a);
        put(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, StructureFeature.field_236267_a_);
        put(builder, Registry.STRUCTURE_PROCESSOR_LIST_KEY, IStructureProcessorType.field_242921_l);
        put(builder, Registry.JIGSAW_POOL_KEY, JigsawPattern.field_236852_a_);
        put(builder, Registry.NOISE_SETTINGS_KEY, DimensionSettings.field_236097_a_);
        return builder.build();
    });

    @Shadow private static final DynamicRegistries.Impl registries = Util.make(() -> {
        DynamicRegistries.Impl dynamicregistries$impl = new DynamicRegistries.Impl();
        DimensionType.registerTypes(dynamicregistries$impl);
        registryCodecMap.keySet().stream().filter((registryKey) -> !registryKey.equals(Registry.DIMENSION_TYPE_KEY)).forEach((registerKey) -> {
            getWorldGenRegistry(dynamicregistries$impl, registerKey);
        });
        return dynamicregistries$impl;
    });
}
