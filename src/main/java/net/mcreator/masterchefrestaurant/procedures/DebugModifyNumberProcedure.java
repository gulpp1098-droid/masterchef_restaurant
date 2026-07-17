package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSourceStack;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class DebugModifyNumberProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments) {
		ModifyRestaurantNumberParameterProcedure.execute(DoubleArgumentType.getDouble(arguments, "value"), RestaurantIndexSearchByIDProcedure.execute(world, DoubleArgumentType.getDouble(arguments, "restaurant_ID")), "restaurants",
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, StringArgumentType.getString(arguments, "parameter_name"));
	}
}