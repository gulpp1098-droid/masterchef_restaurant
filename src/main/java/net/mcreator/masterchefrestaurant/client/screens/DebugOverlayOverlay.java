package net.mcreator.masterchefrestaurant.client.screens;

import org.checkerframework.checker.units.qual.h;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

import net.mcreator.masterchefrestaurant.procedures.DebugReturnProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class DebugOverlayOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getGuiGraphics().guiWidth();
		int h = event.getGuiGraphics().guiHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		if (true) {
			String debugText = DebugReturnProcedure.execute(entity);
String[] lines = debugText.split("\n");

for (int i = 0; i < lines.length; i++) {
    event.getGuiGraphics().drawString(
        Minecraft.getInstance().font,
        lines[i],
        w / 2 - 202,
        h / 2 - 108 + i * 10,
        -1,
        false
    );
}
		}
	}
}