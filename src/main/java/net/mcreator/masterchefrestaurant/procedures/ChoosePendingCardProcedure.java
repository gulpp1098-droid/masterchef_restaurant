package net.mcreator.masterchefrestaurant.procedures;

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

import io.netty.buffer.Unpooled;

public class ChoosePendingCardProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, double indexCard) {
		if (entity == null)
			return;
		double restaurantID = 0;
		double restaurantIndex = 0;
		double remaining = 0;
		double cardIndex = 0;
		String selectedFood = "";
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockOptions = new com.google.gson.JsonArray();
		if (!world.isClientSide()) {
			restaurantID = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID;
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
			pendingCards = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "pending_cards", MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
			cardIndex = indexCard;
			if (cardIndex >= 0 && cardIndex < pendingCards.size()) {
				selectedFood = pendingCards.get((int) cardIndex).getAsString();
				ModifyRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked", MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, selectedFood);
				RemoveRestaurantArrayParameterIndexProcedure.execute(restaurantIndex, "restaurants", "unlock_options", MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, selectedFood);
				remaining = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "starter_unlocks_remaining");
				ModifyRestaurantNumberParameterProcedure.execute(remaining - 1, restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "starter_unlocks_remaining");
				if (remaining - 1 > 0) {
					GeneratePendingCardsProcedure.execute(world, restaurantID);
					FillCardsPickGUIProcedure.execute(world, entity);
				} else {
					ModifyRestaurantWholeArrayParameterProcedure.execute(unlockOptions, restaurantIndex, "restaurants", "pending_cards", MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantFood_File_Name,
							MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
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
				}
			}
		}
	}
}