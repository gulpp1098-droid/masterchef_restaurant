package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Display;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModItems;

import java.util.Comparator;

public class OrderWaitingProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity DisplaySpawn = null;
		if ((entity.getPersistentData().getString("state")).equals("order_wait")) {
			if (!(!((findEntityInWorldRange(world, Display.ItemDisplay.class, x, (y + 3), z, 1)) == null))) {
				DisplaySpawn = world instanceof ServerLevel _level3 ? EntityType.ITEM_DISPLAY.spawn(_level3, BlockPos.containing(x, y + 3, z), MobSpawnType.MOB_SUMMONED) : null;
				if (DisplaySpawn != null) {
					DisplaySpawn.setNoGravity(true);
					{
						Entity _ent = DisplaySpawn;
						if (!_ent.level().isClientSide() && _ent.getServer() != null) {
							_ent.getServer().getCommands()
									.performPrefixedCommand(
											new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4, _ent.getName().getString(),
													_ent.getDisplayName(), _ent.level().getServer(), _ent),
											("data modify entity @s item set value {id:\"" + "" + BuiltInRegistries.ITEM.getKey(MasterchefRestaurantModItems.BELL.get()).toString() + "\",count:1}"));
						}
					}
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
					DisplaySpawn.getPersistentData().putString("client", (entity.getStringUUID()));
				}
			}
		} else {
			if (!((findEntityInWorldRange(world, Display.ItemDisplay.class, x, (y + 3), z, 1)) == null)
					&& ((findEntityInWorldRange(world, Display.ItemDisplay.class, x, (y + 3), z, 1)).getPersistentData().getString("client")).equals(entity.getStringUUID())) {
				if (!(findEntityInWorldRange(world, Display.ItemDisplay.class, x, (y + 3), z, 1)).level().isClientSide())
					(findEntityInWorldRange(world, Display.ItemDisplay.class, x, (y + 3), z, 1)).discard();
			}
		}
	}

	private static Entity findEntityInWorldRange(LevelAccessor world, Class<? extends Entity> clazz, double x, double y, double z, double range) {
		return (Entity) world.getEntitiesOfClass(clazz, AABB.ofSize(new Vec3(x, y, z), range, range, range), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z))).findFirst().orElse(null);
	}
}