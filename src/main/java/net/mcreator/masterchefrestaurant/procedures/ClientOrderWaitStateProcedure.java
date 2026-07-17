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

import net.mcreator.masterchefrestaurant.world.inventory.ClientOrderGUIMenu;
import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import io.netty.buffer.Unpooled;

public class ClientOrderWaitStateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		Entity client = null;
		client = entity;
		if ((client.getPersistentData().getString("state")).equals("food_wait") || (client.getPersistentData().getString("state")).equals("order_wait")) {
			client.getPersistentData().putString("state", "food_wait");
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = sourceentity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.CurrentClientUUID = entity.getStringUUID();
				_vars.markSyncDirty();
			}
			if (sourceentity instanceof ServerPlayer _ent) {
				BlockPos _bpos = BlockPos.containing(x, y, z);
				_ent.openMenu(new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("ClientOrderGUI");
					}

					@Override
					public boolean shouldTriggerClientSideContainerClosingOnOpen() {
						return false;
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						return new ClientOrderGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
					}
				}, _bpos);
			}
		}
	}
}