package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.network.CheckboxPacketToServerMessage;

public class OrdersCheckboxesCheckProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world.isClientSide())
			PacketDistributor.sendToServer(new CheckboxPacketToServerMessage(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientUUID));
	}
}