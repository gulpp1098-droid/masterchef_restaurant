package net.mcreator.omnichef.procedures;

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

import net.mcreator.omnichef.world.inventory.ChefsDiaryItemDetailsGUIMenu;
import net.mcreator.omnichef.network.OmnichefModVariables;

import io.netty.buffer.Unpooled;

public class ChefsDiarySelectItemProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, double slotIndex) {
		if (entity == null)
			return;
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		double foodIndex = 0;
		if (!world.isClientSide()) {
			foodArray = GetFoodDataFromGroupTierProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodTier, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodGroup);
			foodIndex = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage * 18 + slotIndex;
			if (foodIndex >= 0 && foodIndex < foodArray.size()) {
				foodObject = foodArray.get((int) foodIndex).getAsJsonObject();
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.DiarySelectedItem = foodObject.get("id").getAsString();
					_vars.DiaryRecipeIndex = 0;
					_vars.DiaryDependentPage = 0;
					_vars.markSyncDirty();
				}
				if (entity instanceof ServerPlayer _ent) {
					BlockPos _bpos = BlockPos.containing(x, y, z);
					_ent.openMenu(new MenuProvider() {
						@Override
						public Component getDisplayName() {
							return Component.literal("ChefsDiaryItemDetailsGUI");
						}

						@Override
						public boolean shouldTriggerClientSideContainerClosingOnOpen() {
							return false;
						}

						@Override
						public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
							return new ChefsDiaryItemDetailsGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
						}
					}, _bpos);
				}
			}
		}
	}
}