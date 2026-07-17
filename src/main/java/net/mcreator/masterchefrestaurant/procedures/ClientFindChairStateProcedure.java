package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class ClientFindChairStateProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		Entity chair = null;
		Entity client = null;
		client = entity;
		if (!(new Vec3((client.getPersistentData().getDouble("DestX")), (client.getPersistentData().getDouble("DestY")), (client.getPersistentData().getDouble("DestZ"))).distanceTo(new Vec3((client.getX()), (client.getY()), (client.getZ()))) <= 1)) {
			if (client instanceof Mob _entity)
				_entity.getNavigation().moveTo((client.getPersistentData().getDouble("DestX")), (client.getPersistentData().getDouble("DestY")), (client.getPersistentData().getDouble("DestZ")), 1);
		}
	}
}