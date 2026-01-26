package net.legacy.combat_reborn.util;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.legacy.combat_reborn.registry.CRDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class QuiverContents implements TooltipComponent {
	public static QuiverContents empty(String type) {
        return new QuiverContents(List.of(), type);
    };
    public static final Codec<QuiverContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(contents -> contents.type),
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(contents -> contents.items)
    ).apply(instance, (type, items) -> {
        try {
            Fraction weight = computeContentWeight(items, type);
            return new QuiverContents(items, weight, -1, type);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Excessive total quiver weight");
        }
    }));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuiverContents> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            contents -> contents.type,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            contents -> contents.items,
            (type, items) -> {
                Fraction weight = computeContentWeight(items, type);
                return new QuiverContents(items, weight, -1, type);
            }
    );
	private static final Fraction BUNDLE_IN_BUNDLE_WEIGHT = Fraction.getFraction(1, 16);
	private static final int NO_STACK_INDEX = -1;
	public static final int NO_SELECTED_ITEM_INDEX = -1;
	public final List<ItemStack> items;
	final Fraction weight;
	final int selectedItem;
    public final String type;

	QuiverContents(List<ItemStack> list, Fraction fraction, int i, String type) {
		this.items = list;
		this.weight = fraction;
		this.selectedItem = i;
        this.type = type;
	}

	private static DataResult<QuiverContents> checkAndCreate(List<ItemStack> list, String type) {
		try {
			Fraction fraction = computeContentWeight(list, type);
			return DataResult.success(new QuiverContents(list, fraction, -1, type));
		} catch (ArithmeticException var2) {
			return DataResult.error(() -> "Excessive total bundle weight");
		}
	}

	public QuiverContents(List<ItemStack> list, String type) {
		this(list, computeContentWeight(list, type), -1, type);
	}

	private static Fraction computeContentWeight(List<ItemStack> list, String type) {
		Fraction fraction = Fraction.ZERO;

		for (ItemStack itemStack : list) {
			fraction = fraction.add(getWeight(itemStack, type).multiplyBy(Fraction.getFraction(itemStack.getCount(), 1)));
		}

		return fraction;
	}

	static Fraction getWeight(ItemStack itemStack, String type) {
		QuiverContents quiverContents = itemStack.get(CRDataComponents.QUIVER_CONTENTS);
		if (quiverContents != null) {
			return BUNDLE_IN_BUNDLE_WEIGHT.add(quiverContents.weight());
		} else {
			List<BeehiveBlockEntity.Occupant> list = itemStack.getOrDefault(DataComponents.BEES, Bees.EMPTY).bees();
            int denominator = itemStack.getMaxStackSize();
            if (itemStack.is(ItemTags.ARROWS)) denominator *= QuiverHelper.getStorage(type);
			return !list.isEmpty() ? Fraction.ONE : Fraction.getFraction(1, denominator);
		}
	}

	public static boolean canItemBeInBundle(ItemStack itemStack) {
		return !itemStack.isEmpty() && itemStack.getItem().canFitInsideContainerItems() && itemStack.is(ItemTags.ARROWS);
	}

	public int getNumberOfItemsToShow() {
		int i = this.size();
		int j = i > 12 ? 11 : 12;
		int k = i % 4;
		int l = k == 0 ? 0 : 4 - k;
		return Math.min(i, j - l);
	}

	public ItemStack getItemUnsafe(int i) {
		return this.items.get(i);
	}

	public Stream<ItemStack> itemCopyStream() {
		return this.items.stream().map(ItemStack::copy);
	}

	public Iterable<ItemStack> items() {
		return this.items;
	}

	public Iterable<ItemStack> itemsCopy() {
		return Lists.transform(this.items, ItemStack::copy);
	}

	public int size() {
		return this.items.size();
	}

	public Fraction weight() {
		return this.weight;
	}

	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	public int getSelectedItem() {
		return this.selectedItem;
	}

	public boolean hasSelectedItem() {
		return this.selectedItem != -1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return !(object instanceof QuiverContents quiverContents)
				? false
				: this.weight.equals(quiverContents.weight) && ItemStack.listMatches(this.items, quiverContents.items);
		}
	}

	public int hashCode() {
		return ItemStack.hashStackList(this.items);
	}

	public String toString() {
		return "BundleContents" + this.items;
	}

	public static class Mutable {
		private final List<ItemStack> items;
		private Fraction weight;
        private int selectedItem;
        private String type;

        private int selectedSlot;

		public Mutable(QuiverContents quiverContents) {
			this.items = new ArrayList(quiverContents.items);
			this.weight = quiverContents.weight;
            this.selectedItem = quiverContents.selectedItem;
            this.type = quiverContents.type;
		}

		public QuiverContents.Mutable clearItems() {
			this.items.clear();
			this.weight = Fraction.ZERO;
			this.selectedItem = -1;
			return this;
		}

		private int findStackIndex(ItemStack itemStack) {
			if (!itemStack.isStackable()) {
				return -1;
			} else {
				for (int i = 0; i < this.items.size(); i++) {
					if (ItemStack.isSameItemSameComponents((ItemStack)this.items.get(i), itemStack)) {
						return i;
					}
				}

				return -1;
			}
		}

		private int getMaxAmountToAdd(ItemStack itemStack) {
			Fraction fraction = Fraction.ONE.subtract(this.weight);
			return Math.max(fraction.divideBy(QuiverContents.getWeight(itemStack, this.type)).intValue(), 0);
		}

        public int tryInsert(ItemStack itemStack) {
            if (!QuiverContents.canItemBeInBundle(itemStack)) {
                return 0;
            }

            int addedTotal = 0;
            int maxPerStack = itemStack.getMaxStackSize();

            for (ItemStack existing : this.items) {
                if (ItemStack.isSameItemSameComponents(existing, itemStack) && existing.getCount() < maxPerStack) {
                    int space = maxPerStack - existing.getCount();
                    int maxForWeight = this.getMaxAmountToAdd(itemStack);
                    int toAdd = Math.min(space, maxForWeight);
                    toAdd = Math.min(toAdd, itemStack.getCount());

                    if (toAdd <= 0) {
                        return addedTotal;
                    }

                    this.weight = this.weight.add(QuiverContents.getWeight(itemStack, this.type).multiplyBy(Fraction.getFraction(toAdd, 1)));
                    existing.grow(toAdd);
                    itemStack.shrink(toAdd);
                    addedTotal += toAdd;

                    if (itemStack.isEmpty()) {
                        return addedTotal;
                    }
                }
            }

            while (!itemStack.isEmpty()) {
                int maxForWeight = this.getMaxAmountToAdd(itemStack);
                int toAdd = Math.min(maxPerStack, maxForWeight);
                toAdd = Math.min(toAdd, itemStack.getCount());

                if (toAdd <= 0) {
                    break;
                }

                this.weight = this.weight.add(QuiverContents.getWeight(itemStack, this.type).multiplyBy(Fraction.getFraction(toAdd, 1)));

                ItemStack newStack = itemStack.split(toAdd);
                this.items.addFirst(newStack);

                addedTotal += toAdd;
            }

            return addedTotal;
        }

		public int tryTransfer(Slot slot, Player player) {
			ItemStack itemStack = slot.getItem();
			int i = this.getMaxAmountToAdd(itemStack);
			return QuiverContents.canItemBeInBundle(itemStack) ? this.tryInsert(slot.safeTake(itemStack.getCount(), i, player)) : 0;
		}

		public void toggleSelectedItem(int i) {
			this.selectedItem = this.selectedItem != i && !this.indexIsOutsideAllowedBounds(i) ? i : -1;
		}

		private boolean indexIsOutsideAllowedBounds(int i) {
			return i < 0 || i >= this.items.size();
		}

        @Nullable
        public ItemStack removeOne(ItemStack quiverStack) {
            if (this.items.isEmpty()) {
                return null;
            }

            Integer rawSelected = quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT);
            int selectedSlot = (rawSelected != null && rawSelected >= 0 && rawSelected < this.items.size()) ? rawSelected : 0;

            ItemStack removed = this.items.remove(selectedSlot).copy();

            this.weight = this.weight.subtract(QuiverContents.getWeight(removed, this.type)
                    .multiplyBy(Fraction.getFraction(removed.getCount(), 1)));

            if (this.items.isEmpty()) {
                quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, -1);
            } else if (selectedSlot >= this.items.size()) {
                quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, this.items.size() - 1);
            } else {
                quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, selectedSlot);
            }

            return removed;
        }

        public ItemStack getSelectedStack(ItemStack quiverStack) {
            this.selectedSlot = 0;
            if (this.items.isEmpty()) {
                return null;
            }

            Integer rawSelected = quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT);
            this.selectedSlot = (rawSelected != null && rawSelected >= 0 && rawSelected < this.items.size()) ? rawSelected : 0;

            ItemStack selectedStack = this.items.get(this.selectedSlot);
            if (selectedStack.isEmpty()) {
                return null;
            }
            return selectedStack;
        }

        public boolean consumeOne(ItemStack quiverStack) {
            ItemStack selectedStack = getSelectedStack(quiverStack);
            if (selectedStack == null) return false;

            Fraction singleWeight = QuiverContents.getWeight(selectedStack, this.type);
            this.weight = this.weight.subtract(singleWeight);

            selectedStack.shrink(1);

            if (selectedStack.isEmpty()) {
                this.items.remove(this.selectedSlot);
                if (this.items.isEmpty()) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, -1);
                } else if (this.selectedSlot >= this.items.size()) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, this.items.size() - 1);
                } else {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, this.selectedSlot);
                }
            }

            return true;
        }

		public Fraction weight() {
			return this.weight;
		}

		public QuiverContents toImmutable() {
			return new QuiverContents(List.copyOf(this.items), this.weight, this.selectedItem, this.type);
		}
	}
}