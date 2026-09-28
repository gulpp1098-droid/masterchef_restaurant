package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;

import net.mcreator.omnichef.init.OmnichefModItems;

public class LevelUpAnimationReceivedByClientProcedure {
	public static void execute(LevelAccessor world) {
		if (world.isClientSide())
			Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(OmnichefModItems.STAR.get()));
	}
}