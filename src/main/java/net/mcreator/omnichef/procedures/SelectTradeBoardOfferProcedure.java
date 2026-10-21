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

public class SelectTradeBoardOfferProcedure {
	public static void execute(LevelAccessor world, Entity entity, double offerIndex) {
		if (entity == null)
			return;
		com.google.gson.JsonObject tradeBoard = new com.google.gson.JsonObject();
		com.google.gson.JsonObject offer = new com.google.gson.JsonObject();
		com.google.gson.JsonArray offers = new com.google.gson.JsonArray();
		ItemStack inputStack = ItemStack.EMPTY;
		ItemStack outputStack = ItemStack.EMPTY;
		double restaurantIndex = 0;
		if (!world.isClientSide()) {
			{
				OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
				_vars.TradeBoardSelectedOffer = -1;
				_vars.markSyncDirty();
			}
			if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack1 = new ItemStack(Blocks.AIR).copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack1, true);
				ItemStack _displayStack2 = new ItemStack(Blocks.AIR).copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack2, true);
			}
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
			if (restaurantIndex >= 0) {
				tradeBoard = GetRestaurantObjectParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"trade_board");
				offers = tradeBoard.get("offers").getAsJsonArray();
				if (offerIndex >= 0 && offerIndex < offers.size()) {
					offer = offers.get((int) offerIndex).getAsJsonObject();
					inputStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((offer.get("input_id").getAsString()).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					inputStack.setCount((int) offer.get("input_count").getAsDouble());
					outputStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((offer.get("output_id").getAsString()).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					outputStack.setCount((int) offer.get("output_count").getAsDouble());
					if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
						ItemStack _displayStack14 = inputStack.copy();
						_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack14, true);
						ItemStack _displayStack15 = outputStack.copy();
						_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack15, true);
					}
					{
						OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
						_vars.TradeBoardSelectedOffer = offerIndex;
						_vars.markSyncDirty();
					}
					TradeBoardInputChangedProcedure.execute(world, entity);
				}
			}
		}
	}
}