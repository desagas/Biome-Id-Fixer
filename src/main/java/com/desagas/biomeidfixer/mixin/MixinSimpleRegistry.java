package com.desagas.biomeidfixer.mixin;

import com.desagas.biomeidfixer.WriteJSON;
import com.google.common.collect.BiMap;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.IOException;
import java.util.Map;
import java.util.OptionalInt;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleRegistry.class)
public final class MixinSimpleRegistry<T> {

    // TODO Inject code only where needed, there is no reason to write out the entire original code.

    // The Other, Rats, Maholi, AE2, etc are assigned ids here
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/util/registry/SimpleRegistry;validateAndRegister(Ljava/util/OptionalInt;Lnet/minecraft/util/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Ljava/lang/Object;", cancellable = true)
    public <V extends T> void validateAndRegisterStart(OptionalInt index, RegistryKey<T> registryKey, V value, Lifecycle lifecycle, CallbackInfoReturnable<V> callback) {
        Validate.notNull(registryKey);
        Validate.notNull((T)value);
        T t = this.keyToObjectMap.get(registryKey);
        int i;
        if (t == null) {
            if (index.isPresent()) {
                i = index.getAsInt();
            } else {
                i = this.nextFreeId;

//                if (registryKey.getRegistryName().getPath() == "worldgen/biome") {
//                    LOGGER0.info(new StringBuilder().append("Desagas assigned id " + i).append(" to ").append(registryKey.getLocation()));
//                }
            }
        } else {
            i = this.entryIndexMap.getInt(t);
            if (index.isPresent() && index.getAsInt() != i) {
                throw new IllegalStateException("ID mismatch");
            }

            // Send the biome values to be stored.

            this.entryIndexMap.removeInt(t);
            this.objectToLifecycleMap.remove(t);
        }

        if (registryKey.getRegistryName().getPath() == "worldgen/biome") {
            new com.desagas.biomeidfixer.WriteJSON().getOrTryBiomeAssignment(i, registryKey.getLocation().toString());
        }

        callback.setReturnValue(this.register(i, registryKey, value, lifecycle, false));

    }

    @Shadow @Final protected static Logger LOGGER0;
    @Shadow private final BiMap<RegistryKey<T>, T> keyToObjectMap;
    @Shadow private int nextFreeId;
    @Shadow private final Object2IntMap<T> entryIndexMap;
    @Shadow private final Map<T, Lifecycle> objectToLifecycleMap;
    @Shadow private <V extends T> V register(int index, RegistryKey<T> registryKey, V value, Lifecycle lifecycle, boolean logDuplicateKeys) { throw new IllegalStateException("Mixin failed to shadow register()"); }

    public MixinSimpleRegistry(BiMap<RegistryKey<T>, T> keyToObjectMap, Object2IntMap<T> entryIndexMap, Map<T, Lifecycle> objectToLifecycleMap) {
        this.keyToObjectMap = keyToObjectMap;
        this.entryIndexMap = entryIndexMap;
        this.objectToLifecycleMap = objectToLifecycleMap;
    }
}
