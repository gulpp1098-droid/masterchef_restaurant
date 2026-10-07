package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.init.OmnichefModItems;

public class PrepareDishButtonClickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArray = new com.google.gson.JsonArray();
		ItemStack slotItem = ItemStack.EMPTY;
		ItemStack preparedDish = ItemStack.EMPTY;
		String foodID = "";
		double slotIndex = 0;
		boolean valid = false;
		boolean hadFood = false;
		if (!world.isClientSide()) {
			database = ReadFoodDatabaseProcedure.execute(world);
			valid = true;
			if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), 6).copy()).getItem() == Blocks.AIR.asItem()) {
				for (int _i1 = 0; _i1 < 6; _i1++) {
					slotItem = (itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slotIndex).copy()).copy();
					if (!(slotItem.getItem() == Blocks.AIR.asItem())) {
						hadFood = true;
						foodID = BuiltInRegistries.ITEM.getKey(slotItem.getItem()).toString();
						if (IsFoodInDatabaseProcedure.execute(database, foodID)) {
							foodArray.add(foodID);
							foodAmountArray.add((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slotIndex).getCount()));
						} else {
							valid = false;
						}
					}
					slotIndex = slotIndex + 1;
				}
				if (hadFood && valid) {
					preparedDish = new ItemStack(OmnichefModItems.PREPARED_DISH.get()).copy();
					{
						final String _tagName = "food";
						final String _tagValue = ("" + foodArray);
						CustomData.update(DataComponents.CUSTOM_DATA, preparedDish, tag -> tag.putString(_tagName, _tagValue));
					}
					{
						final String _tagName = "food_amount";
						final String _tagValue = ("" + foodAmountArray);
						CustomData.update(DataComponents.CUSTOM_DATA, preparedDish, tag -> tag.putString(_tagName, _tagValue));
					}
					if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.ItemHandler.BLOCK, BlockPos.containing(x, y, z), null) instanceof IItemHandlerModifiable _itemHandlerModifiable) {
						ItemStack _setstack = preparedDish.copy();
						_setstack.setCount(1);
						_itemHandlerModifiable.setStackInSlot(6, _setstack);
					}
					slotIndex = 0;
					for (int _i1 = 0; _i1 < 6; _i1++) {
						if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.ItemHandler.BLOCK, BlockPos.containing(x, y, z), null) instanceof IItemHandlerModifiable _itemHandlerModifiable) {
							ItemStack _setstack = new ItemStack(Blocks.AIR).copy();
							_setstack.setCount(1);
							_itemHandlerModifiable.setStackInSlot((int) slotIndex, _setstack);
						}
						slotIndex = slotIndex + 1;
					}
				}
			}
		}
	}

	private static ItemStack itemFromBlockInventory(LevelAccessor world, BlockPos pos, int slot) {
		if (world instanceof ILevelExtension ext) {
			IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
			if (itemHandler != null)
				return itemHandler.getStackInSlot(slot);
		}
		return ItemStack.EMPTY;
	}
}