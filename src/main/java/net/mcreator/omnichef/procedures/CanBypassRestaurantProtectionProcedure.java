package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

public class CanBypassRestaurantProtectionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof Player _plr ? _plr.getAbilities().instabuild : false) && (entity.hasPermissions(2) || entity.hasPermissions(3) || entity.hasPermissions(4));
	}
}