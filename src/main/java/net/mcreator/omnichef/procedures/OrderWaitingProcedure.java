package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.entity.CriticEntity;
import net.mcreator.omnichef.entity.ClientEntity;

public class OrderWaitingProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity DisplaySpawn = null;
		if (!world.isClientSide()) {
			if ((entity.getPersistentData().getString("state")).equals("order_wait")) {
				if (entity instanceof ClientEntity _datEntSetS)
					_datEntSetS.getEntityData().set(ClientEntity.DATA_DisplayState, "order_wait");
				if (entity instanceof CriticEntity _datEntSetS)
					_datEntSetS.getEntityData().set(CriticEntity.DATA_DisplayState, "order_wait");
			} else {
				if (entity instanceof ClientEntity _datEntSetS)
					_datEntSetS.getEntityData().set(ClientEntity.DATA_DisplayState, "");
				if (entity instanceof CriticEntity _datEntSetS)
					_datEntSetS.getEntityData().set(CriticEntity.DATA_DisplayState, "");
			}
		}
	}
}