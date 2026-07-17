package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

import java.util.regex.Pattern;
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
		substringUUID = inboundString.substring((int) (inboundString.lastIndexOf(":") + 1));
		item = inboundString.substring((int) inboundString.indexOf(" ") + " ".length(), (int) inboundString.lastIndexOf(":"));
		if ((world instanceof ServerLevel _level2 ? getEntityFromUUID(_level2, substringUUID) : null) != null) {
			deliveredString = (world instanceof ServerLevel _level3 ? getEntityFromUUID(_level3, substringUUID) : null).getPersistentData().getString("food_delivered");
			oryginalLength = (deliveredString).length();
			dummyString = deliveredString.replace(",", "");
			newLength = (dummyString).length();
			indexString = 0;
			if (!item.contains("minecraft:air")) {
				String[] _array9 = ((world instanceof ServerLevel _level5 ? getEntityFromUUID(_level5, substringUUID) : null).getPersistentData().getString("food")).split(Pattern.quote(","));
				if (_array9.length != 0) {
					for (String stringiterator : _array9) {
						if ((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\""))).equals(item)) {
							if (!(newLength == indexString)) {
								if ((deliveredString.substring((int) (indexString * 2), (int) (indexString * 2 + 1))).equals("0")) {
									found = true;
									break;
								}
							} else {
								if ((deliveredString.substring((int) (indexString * 2))).equals("0")) {
									found = true;
									break;
								}
							}
						}
						indexString = indexString + 1;
					}
				} else {
					String stringiterator = ((world instanceof ServerLevel _level5 ? getEntityFromUUID(_level5, substringUUID) : null).getPersistentData().getString("food"));
					for (int _yourmother = 0; _yourmother < 1; _yourmother++) {
						if ((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\""))).equals(item)) {
							if (!(newLength == indexString)) {
								if ((deliveredString.substring((int) (indexString * 2), (int) (indexString * 2 + 1))).equals("0")) {
									found = true;
									break;
								}
							} else {
								if ((deliveredString.substring((int) (indexString * 2))).equals("0")) {
									found = true;
									break;
								}
							}
						}
						indexString = indexString + 1;
					}
				}
			}
		}
		if (found) {
			if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
				_menu.getSlots().get(0).remove(1);
				_player.containerMenu.broadcastChanges();
			}
			newFoodDelivery = deliveredString.substring(0, (int) (indexString * 2)) + "1" + deliveredString.substring((int) (indexString * 2 + 1));
			(world instanceof ServerLevel _level11 ? getEntityFromUUID(_level11, substringUUID) : null).getPersistentData().putString("food_delivered", newFoodDelivery);
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