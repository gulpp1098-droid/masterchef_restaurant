package net.mcreator.masterchefrestaurant.procedures;

import org.spongepowered.asm.mixin.injection.Group;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.world.inventory.RestaurantManagementGUIMenu;
import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

import io.netty.buffer.Unpooled;

public class CreatingNewRestaurantProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity Owner = null;
		boolean IsNameAccepted = false;
		String Restaurant_Name = "";
		String NewRestaurant_Name = "";
		double index = 0;
		File ListOfRestaurants = new File("");
		File MenuFile = new File("");
		File ClientsFile = new File("");
		com.google.gson.JsonArray Restaurant_Array = new com.google.gson.JsonArray();
		com.google.gson.JsonArray NewRestaurantArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray LocationArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray Menu = new com.google.gson.JsonArray();
		com.google.gson.JsonArray menus_array = new com.google.gson.JsonArray();
		com.google.gson.JsonArray ClientsGroup = new com.google.gson.JsonArray();
		com.google.gson.JsonArray ClientsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Restaurants = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject NewRestaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantMenuData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject menus = new com.google.gson.JsonObject();
		com.google.gson.JsonObject ClientsRestaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Group = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantsClient = new com.google.gson.JsonObject();
		com.google.gson.JsonObject newClientRestaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject ClientRestaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject dailyStatsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject lastDayStatsObject = new com.google.gson.JsonObject();
		if (!world.isClientSide()) {
			Owner = entity;
			NewRestaurant_Name = (Owner instanceof Player _entity1 && _entity1.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu1) ? _menu1.getMenuState(0, "Restaurant_Name", "") : "";
			IsNameAccepted = true;
			if ((NewRestaurant_Name).length() < 3 || (NewRestaurant_Name).length() >= 30) {
				if (Owner instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("Restaurant name has to be between 3 and 30 characters"), false);
				IsNameAccepted = false;
			} else if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
				if (Owner instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("You are already owning restaurant!"), false);
				IsNameAccepted = false;
			} else if (NewRestaurant_Name.contains("!") || NewRestaurant_Name.contains("@") || NewRestaurant_Name.contains("%") || NewRestaurant_Name.contains("^") || NewRestaurant_Name.contains("(") || NewRestaurant_Name.contains(")")
					|| NewRestaurant_Name.contains("=") || NewRestaurant_Name.contains("\\") || NewRestaurant_Name.contains("/") || NewRestaurant_Name.contains("<") || NewRestaurant_Name.contains(">")) {
				if (Owner instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("You cannot use some special characters!"), false);
				IsNameAccepted = false;
			} else {
				Restaurant_Array = GetRestaurantsListArrayProcedure.execute(world);
				index = 0;
				for (int index0 = 0; index0 < (int) Restaurant_Array.size(); index0++) {
					Restaurant = Restaurant_Array.get((int) index).getAsJsonObject();
					Restaurant_Name = Restaurant.get("name").getAsString();
					if ((NewRestaurant_Name).equals(Restaurant_Name)) {
						if (Owner instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("Restaurant with this name already exists!"), false);
						IsNameAccepted = false;
						break;
					}
					index = index + 1;
				}
			}
			if (IsNameAccepted) {
				NewRestaurant.addProperty("ID", ((int) MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantID));
				NewRestaurant.addProperty("name", NewRestaurant_Name);
				NewRestaurant.addProperty("owner", (Owner.getStringUUID()));
				NewRestaurant.addProperty("ownerName", (Owner.getDisplayName().getString()));
				{
					MasterchefRestaurantModVariables.PlayerVariables _vars = Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
					_vars.owner = Owner.getDisplayName().getString();
					_vars.markSyncDirty();
				}
				NewRestaurant.addProperty("level", 0);
				NewRestaurant.addProperty("reputation", 0);
				NewRestaurant.add("coowners", LocationArray);
				NewRestaurant.add("locations", LocationArray);
				NewRestaurant.addProperty("open", false);
				NewRestaurant.addProperty("active_groups", 0);
				NewRestaurant.addProperty("close_time", 0);
				NewRestaurant.add("tables", LocationArray);
				NewRestaurant.addProperty("reception", "");
				if (world.dayTime() % 24000 < 9000) {
					NewRestaurant.addProperty("creation_menu_day", (Math.floor(world.dayTime() / 24000d) - 1));
				} else {
					NewRestaurant.addProperty("creation_menu_day", Math.floor(world.dayTime() / 24000d));
				}
				NewRestaurant.addProperty("last_day_open", (-1));
				NewRestaurant.add("menu", LocationArray);
				dailyStatsObject.addProperty("customers_served_fully", 0);
				dailyStatsObject.addProperty("customers_served", 0);
				dailyStatsObject.addProperty("customers_lost", 0);
				dailyStatsObject.addProperty("coins_earned", 0);
				dailyStatsObject.addProperty("reputation", 0);
				lastDayStatsObject.addProperty("customers_served_fully", 0);
				lastDayStatsObject.addProperty("customers_served", 0);
				lastDayStatsObject.addProperty("customers_lost", 0);
				lastDayStatsObject.addProperty("coins_earned", 0);
				lastDayStatsObject.addProperty("reputation", 0);
				NewRestaurant.add("daily_stats", dailyStatsObject);
				NewRestaurant.add("last_day_stats", lastDayStatsObject);
				ListOfRestaurants = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name);
				{
					try {
						BufferedReader bufferedReader = new BufferedReader(new FileReader(ListOfRestaurants));
						StringBuilder jsonstringbuilder = new StringBuilder();
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							jsonstringbuilder.append(line);
						}
						bufferedReader.close();
						Restaurants = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
						NewRestaurantArray = Restaurants.get("restaurants").getAsJsonArray();
						NewRestaurantArray.add(NewRestaurant);
						Restaurants.add("restaurants", NewRestaurantArray);
						{
							com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
							try {
								FileWriter fileWriter = new FileWriter(ListOfRestaurants);
								fileWriter.write(mainGSONBuilderVariable.toJson(Restaurants));
								fileWriter.close();
							} catch (IOException exception) {
								exception.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				{
					MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
					_vars.Restaurant_ID = MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantID;
					_vars.markSyncDirty();
				}
				GenerateRestaurantMenuProcedure.execute(world, MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantID, 0);
				if (Owner instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("Restaurant was created!"), false);
				MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantID = MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantID + 1;
				MasterchefRestaurantModVariables.MapVariables.get(world).markSyncDirty();
				if (entity instanceof ServerPlayer _ent) {
					BlockPos _bpos = BlockPos.containing(x, y, z);
					_ent.openMenu(new MenuProvider() {
						@Override
						public Component getDisplayName() {
							return Component.literal("RestaurantManagementGUI");
						}

						@Override
						public boolean shouldTriggerClientSideContainerClosingOnOpen() {
							return false;
						}

						@Override
						public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
							return new RestaurantManagementGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
						}
					}, _bpos);
				}
			} else {
				if (Owner instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("ERROR: Restaurant was NOT created!"), false);
			}
		}
	}
}