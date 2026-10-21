package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.omnichef.world.inventory.TradeBoardGUIMenu;
import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModMenus;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class TryExecuteTradeBoardOfferProcedure {
	public static boolean execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return false;
		File restaurantsFile = new File("");
		com.google.gson.JsonObject root = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tradeBoard = new com.google.gson.JsonObject();
		com.google.gson.JsonObject offer = new com.google.gson.JsonObject();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonArray offers = new com.google.gson.JsonArray();
		ItemStack inputStack = ItemStack.EMPTY;
		ItemStack requiredStack = ItemStack.EMPTY;
		ItemStack rewardStack = ItemStack.EMPTY;
		double restaurantIndex = 0;
		double offerIndex = 0;
		double inputCount = 0;
		double outputCount = 0;
		boolean saveSuccessful = false;
		if (world.isClientSide() || !(entity instanceof Player _plr1 && _plr1.containerMenu instanceof TradeBoardGUIMenu) || !((getItemStackFromItemStackSlot(1, itemstack)).getItem() == Blocks.AIR.asItem())) {
			return false;
		}
		offerIndex = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).TradeBoardSelectedOffer;
		if (offerIndex < 0) {
			return false;
		}
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
		if (restaurantIndex < 0) {
			return false;
		}
		restaurantsFile = new File(OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(restaurantsFile));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				root = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				restaurants = root.get("restaurants").getAsJsonArray();
				restaurant = restaurants.get((int) restaurantIndex).getAsJsonObject();
				tradeBoard = restaurant.get("trade_board").getAsJsonObject();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!tradeBoard.has("day") || !tradeBoard.has("offers")) {
			return false;
		}
		if (!(tradeBoard.get("day").getAsDouble() == entity.getData(OmnichefModVariables.PLAYER_VARIABLES).TradeBoardViewedDay) && !(tradeBoard.get("day").getAsDouble() >= Math.floor(world.dayTime() / 24000d))) {
			FillTradeBoardGUIProcedure.execute(world, entity);
			return false;
		}
		offers = tradeBoard.get("offers").getAsJsonArray();
		if (offerIndex >= offers.size()) {
			return false;
		}
		offer = offers.get((int) offerIndex).getAsJsonObject();
		if (offer.get("used").getAsBoolean()) {
			return false;
		}
		inputCount = offer.get("input_count").getAsDouble();
		outputCount = offer.get("output_count").getAsDouble();
		requiredStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((offer.get("input_id").getAsString()).toLowerCase(java.util.Locale.ENGLISH)))).copy();
		rewardStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((offer.get("output_id").getAsString()).toLowerCase(java.util.Locale.ENGLISH)))).copy();
		rewardStack.setCount((int) outputCount);
		inputStack = (getItemStackFromItemStackSlot(0, itemstack)).copy();
		if (rewardStack.getItem() == Blocks.AIR.asItem() || requiredStack.getItem() == Blocks.AIR.asItem()) {
			return false;
		}
		if (requiredStack.getCount() > requiredStack.getMaxStackSize() || rewardStack.getCount() > rewardStack.getMaxStackSize() || inputStack.getCount() > inputStack.getMaxStackSize() || requiredStack.getCount() < 1 || rewardStack.getCount() < 1
				|| inputStack.getCount() < 1) {
			return false;
		}
		if (!(requiredStack.getItem() == inputStack.getItem())) {
			return false;
		}
		if (inputStack.getCount() < inputCount) {
			return false;
		}
		root.addProperty("used", true);
		saveSuccessful = SaveJsonAtomicallyProcedure.execute(root, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
		if (!saveSuccessful) {
			return false;
		}
		if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
			_menu.getSlots().get(0).remove((int) inputCount);
			ItemStack _setstack43 = rewardStack.copy();
			_setstack43.setCount(1);
			_menu.getSlots().get(1).set(_setstack43);
			_player.containerMenu.broadcastChanges();
		}
		FillTradeBoardGUIProcedure.execute(world, entity);
		return true;
	}

	private static ItemStack getItemStackFromItemStackSlot(int slotID, ItemStack itemStack) {
		IItemHandler itemHandler = itemStack.getCapability(Capabilities.ItemHandler.ITEM, null);
		if (itemHandler != null)
			return itemHandler.getStackInSlot(slotID).copy();
		return ItemStack.EMPTY;
	}
}