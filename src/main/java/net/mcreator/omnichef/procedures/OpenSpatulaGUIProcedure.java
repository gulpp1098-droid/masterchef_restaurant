package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.world.inventory.RestaurantManagementGUIMenu;
import net.mcreator.omnichef.world.inventory.CardsPickGUIMenu;
import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModItems;

import io.netty.buffer.Unpooled;

public class OpenSpatulaGUIProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		double restaurantIndex = 0;
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		if (!world.isClientSide()) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == OmnichefModItems.SPATULA_GOLDEN.get()) {
				if (IsSpatulaInSelectingLocationStateProcedure.execute(itemstack)) {
					restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
					if (restaurantIndex >= 0) {
						pendingCards = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
						if (pendingCards.size() > 0) {
							if (entity instanceof ServerPlayer _ent) {
								BlockPos _bpos = BlockPos.containing(x, y, z);
								_ent.openMenu(new MenuProvider() {
									@Override
									public Component getDisplayName() {
										return Component.literal("CardsPickGUI");
									}

									@Override
									public boolean shouldTriggerClientSideContainerClosingOnOpen() {
										return false;
									}

									@Override
									public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
										return new CardsPickGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
									}
								}, _bpos);
							}
						} else {
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
					} else {
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
}