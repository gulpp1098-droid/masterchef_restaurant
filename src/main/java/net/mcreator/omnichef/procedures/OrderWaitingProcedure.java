package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class OrderWaitingProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity DisplaySpawn = null;
		if ((entity.getPersistentData().getString("state")).equals("order_wait") && !entity.getPersistentData().getBoolean("stateDisplayCreated")) {
			DisplaySpawn = world instanceof ServerLevel _level2 ? EntityType.ITEM_DISPLAY.spawn(_level2, BlockPos.containing(x, y + 2.5, z), MobSpawnType.MOB_SUMMONED) : null;
			if (DisplaySpawn != null) {
				DisplaySpawn.setNoGravity(true);
				{
					Entity _ent = DisplaySpawn;
					if (!_ent.level().isClientSide() && _ent.getServer() != null) {
						_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
								_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "/data modify entity @s billboard set value \"vertical\"");
					}
				}
				{
					Entity _ent = DisplaySpawn;
					if (!_ent.level().isClientSide() && _ent.getServer() != null) {
						_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
								_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "data modify entity @s transformation.scale set value [0.5f,0.5f,0.5f]");
					}
				}
				DisplaySpawn.getPersistentData().putString("clientUUID", (entity.getStringUUID()));
				entity.getPersistentData().putBoolean("stateDisplayCreated", true);
			}
		}
	}
}