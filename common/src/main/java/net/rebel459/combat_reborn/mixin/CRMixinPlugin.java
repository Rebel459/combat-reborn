package net.rebel459.combat_reborn.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.rebel459.combat_reborn.config.CRGeneralConfig;
import net.rebel459.unified.platform.UnifiedPlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class CRMixinPlugin implements IMixinConfigPlugin {

    private boolean registeredConfig = false;

    private boolean hasLegaciesAndLegends;

    @Override
    public void onLoad(String mixinPackage) {
        if (!registeredConfig) {
            AutoConfig.register(CRGeneralConfig.class, GsonConfigSerializer::new);
            registeredConfig = true;
        }
        this.hasLegaciesAndLegends = UnifiedPlatform.get().isModLoaded("legacies_and_legends");
    }

    @Override
    @Nullable
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, @NotNull String mixinClassName) {

        if (mixinClassName.contains("integration.legacies_and_legends.")) return this.hasLegaciesAndLegends;

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    @Nullable
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}