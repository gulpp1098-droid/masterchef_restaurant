package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;

public class SpatulaInHandOnTickProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("SelectingRestaurantLocation")
					|| itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("RelocatingRestaurant")) {
				SpatulaShowingAreaToSelectProcedure.execute(world, entity, itemstack);
			}
			if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("ShowRestaurantArea") && itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("SpawnArea")) {
				ShowingClaimedAreaOfRestaurantProcedure.execute(world, entity, itemstack);
			}
		}
	}
}