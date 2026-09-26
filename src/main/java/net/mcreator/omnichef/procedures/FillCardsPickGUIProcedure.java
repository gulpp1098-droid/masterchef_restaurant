package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModMenus;

public class FillCardsPickGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		String Food1 = "";
		String Food2 = "";
		String Food3 = "";
		Entity player = null;
		double restaurantID = 0;
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		player = entity;
		restaurantID = player.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
			ItemStack _displayStack0 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack0, true);
			ItemStack _displayStack1 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack1, true);
			ItemStack _displayStack2 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(2), _displayStack2, true);
		}
		pendingCards = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
				OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
		if (pendingCards.size() > 0) {
			if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack6 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((pendingCards.get((int) 0).getAsString()).toLowerCase(java.util.Locale.ENGLISH)))).copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack6, true);
			}
		}
		if (pendingCards.size() > 1) {
			if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack10 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((pendingCards.get((int) 1).getAsString()).toLowerCase(java.util.Locale.ENGLISH)))).copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack10, true);
			}
		}
		if (pendingCards.size() > 2) {
			if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack14 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((pendingCards.get((int) 2).getAsString()).toLowerCase(java.util.Locale.ENGLISH)))).copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString(2), _displayStack14, true);
			}
		}
	}
}