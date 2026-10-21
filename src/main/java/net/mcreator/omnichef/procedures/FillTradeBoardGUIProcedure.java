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

public class FillTradeBoardGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonObject offer = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tradeBoard = new com.google.gson.JsonObject();
		com.google.gson.JsonArray offers = new com.google.gson.JsonArray();
		ItemStack displayStack = ItemStack.EMPTY;
		double restaurantIndex = 0;
		double index = 0;
		if (!world.isClientSide()) {
			{
				OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
				_vars.TradeBoardSelectedOffer = -1;
				_vars.markSyncDirty();
			}
			for (int _i1 = 0; _i1 < 7; _i1++) {
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack1 = new ItemStack(Blocks.AIR).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) index), _displayStack1, true);
				}
				index = index + 1;
			}
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
			if (restaurantIndex >= 0) {
				if (EnsureTradeBoardCurrentDayProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
					tradeBoard = GetRestaurantObjectParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"trade_board");
					offers = tradeBoard.get("offers").getAsJsonArray();
					index = 0;
					for (int _i1 = 0; _i1 < 5; _i1++) {
						if (index < offers.size()) {
							offer = offers.get((int) index).getAsJsonObject();
							displayStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((offer.get("output_id").getAsString()).toLowerCase(java.util.Locale.ENGLISH)))).copy();
							displayStack.setCount((int) offer.get("output_count").getAsDouble());
							if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
								ItemStack _displayStack9 = displayStack.copy();
								_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (index + 2)), _displayStack9, true);
							}
						}
						index = index + 1;
					}
				}
			}
		}
	}
}