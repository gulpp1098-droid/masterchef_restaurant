package net.mcreator.omnichef.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class ClientLeaveStateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double restaurantID = 0;
		double leaveAngle = 0;
		ClientSettleLeavingProcedure.execute(world, entity);
		if (world.dayTime() % 24000 >= 18000 || world.dayTime() % 24000 >= 0 && world.dayTime() % 24000 <= 30
				|| new Vec3(x, y, z).distanceTo(new Vec3((entity.getPersistentData().getDouble("DestX")), (entity.getPersistentData().getDouble("DestY")), (entity.getPersistentData().getDouble("DestZ")))) >= 60
				|| world.dayTime() >= entity.getPersistentData().getDouble("despawn_time")) {
			if (entity.getPersistentData().getBoolean("leader")) {
				ClientFinishActiveGroupProcedure.execute(world, entity);
			}
			if (!entity.level().isClientSide())
				entity.discard();
		} else {
			if (!entity.getPersistentData().getBoolean("leave_direction_set")) {
				leaveAngle = Math.toRadians(Math.random() * 360);
				entity.getPersistentData().putDouble("LeaveOffsetX", (Math.cos(leaveAngle) * 100));
				entity.getPersistentData().putDouble("LeaveOffsetZ", (Math.sin(leaveAngle) * 100));
				entity.getPersistentData().putBoolean("leave_direction_set", true);
			}
			if (entity instanceof Mob _entity)
				_entity.getNavigation().moveTo((x + entity.getPersistentData().getDouble("LeaveOffsetX")), y, (z + entity.getPersistentData().getDouble("LeaveOffsetZ")), 1);
		}
	}
}