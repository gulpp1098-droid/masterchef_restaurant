package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.entity.CriticEntity;
import net.mcreator.omnichef.entity.ClientEntity;

public class OrderWaitingProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			if ((entity.getPersistentData().getString("state")).equals("order_wait") || (entity.getPersistentData().getString("state")).equals("order_pick") || (entity.getPersistentData().getString("state")).equals("food_wait")
					|| (entity.getPersistentData().getString("state")).equals("group_wait")) {
				if (entity instanceof ClientEntity _datEntSetS)
					_datEntSetS.getEntityData().set(ClientEntity.DATA_DisplayState, (entity.getPersistentData().getString("state")));
				if (entity instanceof CriticEntity _datEntSetS)
					_datEntSetS.getEntityData().set(CriticEntity.DATA_DisplayState, (entity.getPersistentData().getString("state")));
			} else {
				if (entity instanceof ClientEntity _datEntSetS)
					_datEntSetS.getEntityData().set(ClientEntity.DATA_DisplayState, "");
				if (entity instanceof CriticEntity _datEntSetS)
					_datEntSetS.getEntityData().set(CriticEntity.DATA_DisplayState, "");
			}
		}
	}
}