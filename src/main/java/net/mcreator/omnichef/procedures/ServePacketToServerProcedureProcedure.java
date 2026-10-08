package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModMenus;

import java.util.UUID;

public class ServePacketToServerProcedureProcedure {
	public static void execute(LevelAccessor world, Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		boolean found = false;
		double indexString = 0;
		double oryginalLength = 0;
		double newLength = 0;
		String food_delivered = "";
		String newFoodDelivery = "";
		String substringUUID = "";
		String item = "";
		String deliveredString = "";
		String dummyString = "";
		String clientUUID = "";
		Entity player = null;
		Entity client = null;
		ItemStack dishStack = ItemStack.EMPTY;
		player = entity;
		clientUUID = inboundString;
		if (CanUseCurrentClientOrderSessionProcedure.execute(world, entity) && (clientUUID).equals(player.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientUUID)) {
			client = world instanceof ServerLevel _level0 ? getEntityFromUUID(_level0, clientUUID) : null;
			if (client != null) {
				dishStack = (player instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu1 ? _menu1.getSlots().get(0).getItem() : ItemStack.EMPTY).copy();
				if (ServePreparedDishToClientProcedure.execute(world, client, dishStack)) {
					if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
						_menu.getSlots().get(0).remove(1);
						_player.containerMenu.broadcastChanges();
					}
				}
			}
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}