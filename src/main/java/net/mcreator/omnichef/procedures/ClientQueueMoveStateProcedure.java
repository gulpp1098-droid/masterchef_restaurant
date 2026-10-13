package net.mcreator.omnichef.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class ClientQueueMoveStateProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		double RugNumber = 0;
		double X = 0;
		double Y = 0;
		double Z = 0;
		double DestX = 0;
		double DestY = 0;
		double DestZ = 0;
		double DX = 0;
		double DZ = 0;
		double speed = 0;
		double yaw = 0;
		double distance = 0;
		boolean Found = false;
		client = entity;
		if (client instanceof Mob _entity)
			_entity.getNavigation().stop();
		if (client instanceof Mob _mob1)
			_mob1.setNoAi(true);
		DX = (client.getPersistentData().getDouble("DestX") + 0.5) - client.getX();
		DZ = (client.getPersistentData().getDouble("DestZ") + 0.5) - client.getZ();
		distance = Math.sqrt(DX * DX + DZ * DZ);
		if (distance > 0.05) {
			speed = Math.min(0.06, distance * 0.4);
			if ((client.getPersistentData().getString("state")).equals("queue_move")) {
				yaw = Math.toDegrees(Math.atan2(DZ, DX)) - 90;
				{
					Entity _ent = client;
					_ent.setYRot((float) yaw);
					_ent.setXRot(0);
					_ent.setYBodyRot(_ent.getYRot());
					_ent.setYHeadRot(_ent.getYRot());
					_ent.yRotO = _ent.getYRot();
					_ent.xRotO = _ent.getXRot();
					if (_ent instanceof LivingEntity _entity) {
						_entity.yBodyRotO = _entity.getYRot();
						_entity.yHeadRotO = _entity.getYRot();
					}
				}
			}
			client.setDeltaMovement(new Vec3(((DX / distance) * speed), (client.getDeltaMovement().y()), ((DZ / distance) * speed)));
		} else {
			client.setDeltaMovement(new Vec3(0, (client.getDeltaMovement().y()), 0));
			if ((client.getPersistentData().getString("state")).equals("queue_move")) {
				client.getPersistentData().putDouble("current_position", (client.getPersistentData().getDouble("current_position") - 1));
				client.getPersistentData().putDouble("stuckCounter", 10);
				client.getPersistentData().putString("state", "queue_wait");
			}
		}
	}
}