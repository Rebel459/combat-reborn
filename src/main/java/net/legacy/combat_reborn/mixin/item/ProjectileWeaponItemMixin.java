package net.legacy.combat_reborn.mixin.item;

import net.legacy.combat_reborn.registry.CRDataComponents;
import net.legacy.combat_reborn.util.QuiverContents;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.legacy.combat_reborn.util.QuiverInterface;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {

    @Inject(at = @At("TAIL"), method = "useAmmo")
    private static void quiver(ItemStack itemStack, ItemStack itemStack2, LivingEntity livingEntity, boolean bl, CallbackInfoReturnable<ItemStack> cir) {
        if (!(livingEntity instanceof Player player) || !(player instanceof QuiverInterface quiverInterface)) return;
        ItemStack quiverStack = quiverInterface.getQuiver();
        if (quiverStack != null && !cir.getReturnValue().has(DataComponents.INTANGIBLE_PROJECTILE) && !player.hasInfiniteMaterials()) {
            QuiverContents contents = quiverStack.get(CRDataComponents.QUIVER_CONTENTS);
            if (contents != null) {
                QuiverContents.Mutable mutable = new QuiverContents.Mutable(contents);
                if (mutable.consumeOne(quiverStack)) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
                    player.containerMenu.sendAllDataToRemote();
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "draw")
    private static void quiver(ItemStack itemStack, ItemStack itemStack2, LivingEntity livingEntity, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (livingEntity instanceof Player player) {
            ItemStack stack = QuiverHelper.getStack(player);
            stack.applyComponents(DataComponentPatch.builder()
                    .set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(false), List.of(), List.of()))
                    .build()
            );
        }
    }

    @Inject(at = @At("HEAD"), method = "shoot", cancellable = true)
    private void quiver(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, LivingEntity target, CallbackInfo ci) {
        if (shooter instanceof Player player && QuiverHelper.getStack(player) != null) {
            ProjectileWeaponItem item = ProjectileWeaponItem.class.cast(this);

            ItemStack stack = QuiverHelper.getStack(player);

            float accuracy = QuiverHelper.getAccuracy(stack);
            float power = QuiverHelper.getPower(stack);

            float f = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
            float g = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float)(projectileItems.size() - 1);
            float h = (float)((projectileItems.size() - 1) % 2) * g / 2.0F;
            float i = 1.0F;

            for(int j = 0; j < projectileItems.size(); ++j) {
                ItemStack itemStack = projectileItems.get(j);
                if (!itemStack.isEmpty()) {
                    float k = h + i * (float)((j + 1) / 2) * g;
                    i = -i;
                    int l = j;
                    Projectile.spawnProjectile(item.createProjectile(level, shooter, weapon, itemStack, isCrit), level, itemStack, (projectile) -> {
                        if (item instanceof BowItem bowItem) bowItem.shootProjectile(shooter, projectile, l, velocity * power, inaccuracy / accuracy, k, target);
                        else if (item instanceof CrossbowItem crossbowItem) crossbowItem.shootProjectile(shooter, projectile, l, velocity * power, inaccuracy / accuracy, k, target);
                    });
                    weapon.hurtAndBreak(item.getDurabilityUse(itemStack), shooter, hand);
                    if (weapon.isEmpty()) {
                        break;
                    }
                    player.addTag("damaged_accessory");
                    ci.cancel();
                }
            }
        }
    }
}