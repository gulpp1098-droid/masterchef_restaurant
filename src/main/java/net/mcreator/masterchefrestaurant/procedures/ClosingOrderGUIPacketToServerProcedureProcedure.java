package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class ClosingOrderGUIPacketToServerProcedureProcedure {
	public static void execute(Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		String string = "";
		ItemStack item = ItemStack.EMPTY;
		double amount = 0;
		string = inboundString;
		if (!(string).equals("0 minecraft:air")) {
			item = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(((string.substring(string.indexOf(" ", 0) + 1))).toLowerCase(java.util.Locale.ENGLISH)))).copy();
			amount = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(string.substring(0, string.indexOf(" ", 0)));
			item.setCount((int) amount);
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.OrderSlot0Item = item.copy();
				_vars.markSyncDirty();
			}
		} else {
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.OrderSlot0Item = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("minecraft:air"))).copy();
				_vars.markSyncDirty();
			}
		}
	}
}