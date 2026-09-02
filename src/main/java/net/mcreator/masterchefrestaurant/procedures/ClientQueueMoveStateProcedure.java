package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

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
		boolean Found = false;
		client = entity;
		X = client.getPersistentData().getDouble("DestX");
		Y = client.getPersistentData().getDouble("DestY");
		Z = client.getPersistentData().getDouble("DestZ");
		if (client instanceof Mob _entity)
			_entity.getNavigation().moveTo(X, Y, Z, 1);
		if (new Vec3(X, Y, Z).distanceTo(new Vec3((client.getX()), (client.getY()), (client.getZ()))) < 1.5) {
			if (client instanceof Mob _entity)
				_entity.getNavigation().stop();
			{
				Entity _ent = client;
				double _tx = X;
				double _ty = (Y + 0.1);
				double _tz = Z;
				_ent.teleportTo(_tx, _ty, _tz);
				if (_ent instanceof ServerPlayer _serverPlayer)
					_serverPlayer.connection.teleport(_tx, _ty, _tz, _ent.getYRot(), _ent.getXRot());
			}
			if (client instanceof Mob _mob10)
				_mob10.setNoAi(true);
			client.getPersistentData().putDouble("current_position", (client.getPersistentData().getDouble("current_position") - 1));
			client.getPersistentData().putDouble("stuckCounter", 10);
			client.getPersistentData().putString("state", "queue_wait");
		}
	}
}