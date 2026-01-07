package net.legacy.combat_reborn.item;

import net.legacy.combat_reborn.registry.CRDataComponents;
import net.legacy.combat_reborn.util.QuiverContents;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

public class QuiverItem extends Item {
    public static final int MAX_SHOWN_GRID_ITEMS_X = 4;
    public static final int MAX_SHOWN_GRID_ITEMS_Y = 3;
    public static final int MAX_SHOWN_GRID_ITEMS = 12;
    public static final int OVERFLOWING_MAX_SHOWN_GRID_ITEMS = 11;
    private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);
    private static final int TICKS_AFTER_FIRST_THROW = 10;
    private static final int TICKS_BETWEEN_THROWS = 2;
    private static final int TICKS_MAX_THROW_DURATION = 200;

    public QuiverItem(Item.Properties properties) {
        super(properties);
    }

    public static float getFullnessDisplay(ItemStack itemStack) {
        QuiverContents quiverContents = itemStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.getEmpty(QuiverHelper.getType(itemStack)));
        return quiverContents.weight().floatValue();
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        QuiverContents quiverContents = itemStack.get(CRDataComponents.QUIVER_CONTENTS);
        if (quiverContents == null) {
            return false;
        } else {
            ItemStack itemStack2 = slot.getItem();
            QuiverContents.Mutable mutable = new QuiverContents.Mutable(quiverContents);
            if (clickAction == ClickAction.PRIMARY && !itemStack2.isEmpty()) {
                if (mutable.tryTransfer(slot, player) > 0) {
                    playInsertSound(player);
                } else {
                    playInsertFailSound(player);
                }

                itemStack.set(CRDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
                this.broadcastChangesOnContainerMenu(player);
                return true;
            } else if (clickAction == ClickAction.SECONDARY && itemStack2.isEmpty()) {
                ItemStack itemStack3 = mutable.removeOne(itemStack);
                if (itemStack3 != null) {
                    ItemStack itemStack4 = slot.safeInsert(itemStack3);
                    if (itemStack4.getCount() > 0) {
                        mutable.tryInsert(itemStack4);
                    } else {
                        playRemoveOneSound(player);
                    }
                }

                itemStack.set(CRDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
                this.broadcastChangesOnContainerMenu(player);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack itemStack, ItemStack itemStack2, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (clickAction == ClickAction.PRIMARY && itemStack2.isEmpty()) {
            toggleSelectedItem(itemStack, -1);
            return false;
        } else {
            QuiverContents quiverContents = itemStack.get(CRDataComponents.QUIVER_CONTENTS);
            if (quiverContents == null) {
                return false;
            } else {
                QuiverContents.Mutable mutable = new QuiverContents.Mutable(quiverContents);
                if (clickAction == ClickAction.PRIMARY && !itemStack2.isEmpty()) {
                    if (slot.allowModification(player) && mutable.tryInsert(itemStack2) > 0) {
                        playInsertSound(player);
                    } else {
                        playInsertFailSound(player);
                    }

                    itemStack.set(CRDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
                    this.broadcastChangesOnContainerMenu(player);
                    return true;
                } else if (clickAction == ClickAction.SECONDARY && itemStack2.isEmpty()) {
                    if (slot.allowModification(player)) {
                        ItemStack itemStack3 = mutable.removeOne(itemStack);
                        if (itemStack3 != null) {
                            playRemoveOneSound(player);
                            slotAccess.set(itemStack3);
                        }
                    }

                    itemStack.set(CRDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
                    this.broadcastChangesOnContainerMenu(player);
                    return true;
                } else {
                    toggleSelectedItem(itemStack, -1);
                    return false;
                }
            }
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        QuiverContents quiverContents = itemStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.getEmpty(QuiverHelper.getType(itemStack)));
        return quiverContents.weight().compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        QuiverContents quiverContents = itemStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.getEmpty(QuiverHelper.getType(itemStack)));
        return Math.min(1 + Mth.mulAndTruncate(quiverContents.weight(), 12), 13);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        QuiverContents quiverContents = itemStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.getEmpty(QuiverHelper.getType(itemStack)));
        return quiverContents.weight().compareTo(Fraction.ONE) >= 0 ? FULL_BAR_COLOR : BAR_COLOR;
    }

    public static void toggleSelectedItem(ItemStack itemStack, int i) {
        QuiverContents quiverContents = itemStack.get(CRDataComponents.QUIVER_CONTENTS);
        if (quiverContents != null) {
            QuiverContents.Mutable mutable = new QuiverContents.Mutable(quiverContents);
            mutable.toggleSelectedItem(i);
            itemStack.set(CRDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
        }
    }

    public static boolean hasSelectedItem(ItemStack itemStack) {
        QuiverContents quiverContents = itemStack.get(CRDataComponents.QUIVER_CONTENTS);
        return quiverContents != null && quiverContents.getSelectedItem() != -1;
    }

    public static int getSelectedItem(ItemStack itemStack) {
        QuiverContents quiverContents = itemStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.getEmpty(QuiverHelper.getType(itemStack)));
        return quiverContents.getSelectedItem();
    }

    public static ItemStack getSelectedItemStack(ItemStack itemStack) {
        QuiverContents quiverContents = itemStack.get(CRDataComponents.QUIVER_CONTENTS);
        return quiverContents != null && quiverContents.getSelectedItem() != -1 ? quiverContents.getItemUnsafe(quiverContents.getSelectedItem()) : ItemStack.EMPTY;
    }

    public static int getNumberOfItemsToShow(ItemStack itemStack) {
        QuiverContents quiverContents = itemStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.getEmpty(QuiverHelper.getType(itemStack)));
        return quiverContents.getNumberOfItemsToShow();
    }

    private boolean dropContent(ItemStack itemStack, Player player) {
        QuiverContents quiverContents = itemStack.get(CRDataComponents.QUIVER_CONTENTS);
        if (quiverContents != null && !quiverContents.isEmpty()) {
            Optional<ItemStack> optional = removeOneItemFromBundle(itemStack, player, quiverContents);
            if (optional.isPresent()) {
                player.drop((ItemStack)optional.get(), true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static Optional<ItemStack> removeOneItemFromBundle(ItemStack itemStack, Player player, QuiverContents quiverContents) {
        QuiverContents.Mutable mutable = new QuiverContents.Mutable(quiverContents);
        ItemStack itemStack2 = mutable.removeOne(itemStack);
        if (itemStack2 != null) {
            playRemoveOneSound(player);
            itemStack.set(CRDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
            return Optional.of(itemStack2);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack quiverStack) {
        TooltipDisplay display = quiverStack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        if (!display.shows(CRDataComponents.QUIVER_CONTENTS)) {
            return Optional.empty();
        }

        QuiverContents contents = quiverStack.get(CRDataComponents.QUIVER_CONTENTS);
        return contents != null ? Optional.of(new Tooltip(quiverStack)) : Optional.empty();
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        QuiverContents quiverContents = itemEntity.getItem().get(CRDataComponents.QUIVER_CONTENTS);
        if (quiverContents != null) {
            itemEntity.getItem().set(CRDataComponents.QUIVER_CONTENTS, QuiverContents.getEmpty(QuiverHelper.getType(itemEntity.getItem())));
            ItemUtils.onContainerDestroyed(itemEntity, quiverContents.itemsCopy());
        }
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }

    private static void playDropContentsSound(Level level, Entity entity) {
        level.playSound(
                null, entity.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F
        );
    }

    public void broadcastChangesOnContainerMenu(Player player) {
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;
        if (abstractContainerMenu != null) {
            abstractContainerMenu.slotsChanged(player.getInventory());
        }
    }

    public record Tooltip(ItemStack quiver) implements TooltipComponent { }
}