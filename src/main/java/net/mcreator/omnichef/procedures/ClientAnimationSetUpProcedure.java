package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.entity.ClientEntity;

public class ClientAnimationSetUpProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		client = entity;
		if (client.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
			if (client instanceof ClientEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ClientEntity.DATA_Walk, true);
			if (client instanceof ClientEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ClientEntity.DATA_Sit, false);
			if (client instanceof ClientEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ClientEntity.DATA_Idle, false);
			if (client instanceof ClientEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ClientEntity.DATA_Eat, false);
		} else if (client.isPassenger()) {
			if ((client.getPersistentData().getString("state")).equals("food_eat")) {
				if (client instanceof ClientEntity _datEntSetL)
					_datEntSetL.getEntityData().set(ClientEntity.DATA_Walk, false);
				if (client instanceof ClientEntity _datEntSetL)
					_datEntSetL.getEntityData().set(ClientEntity.DATA_Sit, false);
				if (client instanceof ClientEntity _datEntSetL)
					_datEntSetL.getEntityData().set(ClientEntity.DATA_Idle, false);
				if (client instanceof ClientEntity _datEntSetL)
					_datEntSetL.getEntityData().set(ClientEntity.DATA_Eat, true);
			} else {
				if (client instanceof ClientEntity _datEntSetL)
					_datEntSetL.getEntityData().set(ClientEntity.DATA_Walk, false);
				if (client instanceof ClientEntity _datEntSetL)
					_datEntSetL.getEntityData().set(ClientEntity.DATA_Sit, true);
				if (client instanceof ClientEntity _datEntSetL)
					_datEntSetL.getEntityData().set(ClientEntity.DATA_Idle, false);
				if (client instanceof ClientEntity _datEntSetL)
					_datEntSetL.getEntityData().set(ClientEntity.DATA_Eat, false);
			}
		} else {
			if (client instanceof ClientEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ClientEntity.DATA_Walk, false);
			if (client instanceof ClientEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ClientEntity.DATA_Sit, false);
			if (client instanceof ClientEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ClientEntity.DATA_Idle, true);
			if (client instanceof ClientEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ClientEntity.DATA_Eat, false);
		}
	}
}