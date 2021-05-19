package com.desagas.biomeidfixer.mixin;

import com.google.common.collect.BiMap;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.OptionalInt;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleRegistry.class)
public final class MixinSimpleRegistry<T> {

    // The Other, Rats, Maholi, AE2, etc are assigned ids here,
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/util/registry/SimpleRegistry;registerOrOverride(Ljava/util/OptionalInt;Lnet/minecraft/util/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Ljava/lang/Object;", cancellable = true)
    public <V extends T> void validateAndRegisterStart(OptionalInt index, RegistryKey<T> registryKey, V value, Lifecycle lifecycle, CallbackInfoReturnable<V> callback) {
        Validate.notNull(registryKey);

        // Variables added by Desagas
        // org.apache.logging.log4j.LogManager.getLogger().debug("Desagas: SimpleEntry - " + registryKey.getRegistryName().getPath().toString());
        boolean biomePath = registryKey.getRegistryName().getPath().equals("worldgen/biome");
        com.desagas.biomeidfixer.Write thisBiomeId = new com.desagas.biomeidfixer.Write();

        // Injecting an id when it is a biome, nothing else.
        if (biomePath) {
            index = OptionalInt.of(thisBiomeId.getOrTryBiomeAssignment(index.isPresent() ? index.getAsInt() : this.nextId, registryKey.location().toString())); // Desagas: use index if available, if not it will find the next int.
        }
        // No other changes.
    }

    @Shadow private int nextId;

    @Mutable @Final @Shadow private final BiMap<RegistryKey<T>, T> keyStorage;
    @Mutable @Final @Shadow private final Object2IntMap<T> toId;
    @Mutable @Final @Shadow private final Map<T, Lifecycle> lifecycles;

    public MixinSimpleRegistry(BiMap<RegistryKey<T>, T> keyToObjectMap, Object2IntMap<T> entryIndexMap, Map<T, Lifecycle> objectToLifecycleMap) {
        this.keyStorage = keyToObjectMap;
        this.toId = entryIndexMap;
        this.lifecycles = objectToLifecycleMap;
    }
}
