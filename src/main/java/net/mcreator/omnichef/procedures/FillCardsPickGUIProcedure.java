package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;

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
		ItemStack card1 = ItemStack.EMPTY;
		ItemStack card2 = ItemStack.EMPTY;
		ItemStack card3 = ItemStack.EMPTY;
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
			Food1 = pendingCards.get((int) 0).getAsString();
			card1 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food1).toLowerCase(java.util.Locale.ENGLISH)))).copy();
			{
				final String _tagName = "RecipeTier";
				final double _tagValue = GetFoodTierFromIDProcedure.execute(world, Food1);
				CustomData.update(DataComponents.CUSTOM_DATA, card1, tag -> tag.putDouble(_tagName, _tagValue));
			}
			if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack7 = card1.copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack7, true);
			}
		}
		if (pendingCards.size() > 1) {
			Food2 = pendingCards.get((int) 1).getAsString();
			card2 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food2).toLowerCase(java.util.Locale.ENGLISH)))).copy();
			{
				final String _tagName = "RecipeTier";
				final double _tagValue = GetFoodTierFromIDProcedure.execute(world, Food2);
				CustomData.update(DataComponents.CUSTOM_DATA, card2, tag -> tag.putDouble(_tagName, _tagValue));
			}
			if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack12 = card2.copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack12, true);
			}
		}
		if (pendingCards.size() > 2) {
			Food3 = pendingCards.get((int) 2).getAsString();
			card3 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food3).toLowerCase(java.util.Locale.ENGLISH)))).copy();
			{
				final String _tagName = "RecipeTier";
				final double _tagValue = GetFoodTierFromIDProcedure.execute(world, Food3);
				CustomData.update(DataComponents.CUSTOM_DATA, card3, tag -> tag.putDouble(_tagName, _tagValue));
			}
			if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack17 = card3.copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString(2), _displayStack17, true);
			}
		}
	}
}