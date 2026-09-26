package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.network.MenuPacketToServerMessage;
import net.mcreator.omnichef.OmnichefMod;

public class FillOrderSlotsProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		OmnichefMod.LOGGER.info("Client send: " + entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientUUID);
		if (world.isClientSide())
			PacketDistributor.sendToServer(new MenuPacketToServerMessage(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientUUID));
	}
}