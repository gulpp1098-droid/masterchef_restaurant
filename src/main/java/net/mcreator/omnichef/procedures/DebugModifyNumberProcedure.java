package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import net.mcreator.omnichef.network.OmnichefModVariables;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class DebugModifyNumberProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;
		double restaurantID = 0;
		double newValue = 0;
		double index = 0;
		String parameterName = "";
		if (!world.isClientSide()) {
			if (Level.OVERWORLD == (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))) {
				if (OmnichefModVariables.MapVariables.get(world).ModDataReady) {
					restaurantID = DoubleArgumentType.getDouble(arguments, "restaurant_ID");
					index = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
					newValue = (int) (DoubleArgumentType.getDouble(arguments, "value"));
					parameterName = StringArgumentType.getString(arguments, "parameter_name");
					if (index >= 0) {
						if ((parameterName).equals("level")) {
							if (newValue >= 0 && newValue <= OmnichefModVariables.MapVariables.get(world).MaxRestaurantLevel) {
								ModifyRestaurantNumberParameterProcedure.execute(newValue, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
										parameterName);
							} else {
								if (entity instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("Wrong arguments"), false);
							}
						} else if ((parameterName).equals("reputation")) {
							if (newValue >= 0) {
								ModifyRestaurantNumberParameterProcedure.execute(newValue, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
										parameterName);
							} else {
								if (entity instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("Wrong arguments"), false);
							}
						} else {
							if (entity instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("Only level and reputation allowed"), false);
						}
					} else {
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("Restaurant not found"), false);
					}
				}
			}
		}
	}
}