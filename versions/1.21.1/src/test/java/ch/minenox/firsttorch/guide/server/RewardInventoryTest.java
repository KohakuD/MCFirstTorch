package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class RewardInventoryTest {
    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    // Native 1.21.1 ItemStack fixtures with explicit stack-size components.
    private static ItemStack stack(Item item, int count, int limit) {
        ItemStack stack = new ItemStack(item, count);
        stack.set(DataComponents.MAX_STACK_SIZE, limit);
        return stack;
    }

    @Test void mergesExistingStackBeforeUsingEmptySlot() {
        List<ItemStack> slots = new ArrayList<>(List.of(stack(Items.BREAD, 60, 64), ItemStack.EMPTY));
        assertTrue(RewardClaims.insert(slots, stack(Items.BREAD, 1, 64), 5));
        assertEquals(64, slots.get(0).getCount());
        assertEquals(1, slots.get(1).getCount());
    }

    @Test void fullInventoryRejectsWholeBatchPlan() {
        List<ItemStack> slots = new ArrayList<>(List.of(stack(Items.BREAD, 64, 64)));
        assertFalse(RewardClaims.insert(slots, stack(Items.BREAD, 1, 64), 1));
        assertEquals(64, slots.getFirst().getCount());
    }

    @Test void consecutiveRewardsCompeteForSameAvailableSlots() {
        List<ItemStack> slots = new ArrayList<>(List.of(ItemStack.EMPTY));
        assertTrue(RewardClaims.insert(slots, stack(Items.BREAD, 1, 64), 64));
        assertFalse(RewardClaims.insert(slots, stack(Items.OAK_LOG, 1, 64), 1));
        assertTrue(slots.getFirst().is(Items.BREAD));
    }

    @Test void respectsNonStackableAndSmallerStackLimits() {
        List<ItemStack> slots = new ArrayList<>(List.of(ItemStack.EMPTY, ItemStack.EMPTY));
        assertTrue(RewardClaims.insert(slots, stack(Items.WOODEN_PICKAXE, 1, 1), 2));
        assertEquals(1, slots.get(0).getCount());
        assertEquals(1, slots.get(1).getCount());
        assertFalse(RewardClaims.insert(slots, stack(Items.WOODEN_PICKAXE, 1, 1), 1));
        slots = new ArrayList<>(List.of(ItemStack.EMPTY));
        assertFalse(RewardClaims.insert(slots, stack(Items.ENDER_PEARL, 1, 16), 17));
        assertEquals(16, slots.getFirst().getCount());
    }
}
