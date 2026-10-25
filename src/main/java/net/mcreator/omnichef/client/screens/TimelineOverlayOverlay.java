package net.mcreator.omnichef.client.screens;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

import net.mcreator.omnichef.procedures.ReturnTimelineOpenRestaurantProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class TimelineOverlayOverlay {
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
		if (ReturnTimelineOpenRestaurantProcedure.execute(world, entity)) {
			guiTools$orderedLayers : {
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 140;
					int guiTools$visibleHeight = 10;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/timelineempty_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, w / 2 + -69 + guiTools$xOffset, 8 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 140, 10);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = Math.max(0, Math.min(120, (int) Math.round(net.mcreator.omnichef.procedures.ReturnTimelineProgressProcedure.execute(world, entity))));
					int guiTools$visibleHeight = 2;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/timelinefull_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, w / 2 + -59 + guiTools$xOffset, 12 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 120, 2);
				}
				if (true) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.omnichef.procedures.ReturnTimelineProgressProcedure.execute(world, entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 5;
					int guiTools$visibleHeight = 12;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/slider_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, w / 2 + -61 + guiTools$xOffset, 7 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 5, 12);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 20;
					int guiTools$visibleHeight = 18;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/moon_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, w / 2 + 71 + guiTools$xOffset, 4 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 20, 18);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 20;
					int guiTools$visibleHeight = 18;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openrestaurant_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, w / 2 + -89 + guiTools$xOffset, 4 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 20, 18);
				}
				if (net.mcreator.omnichef.procedures.Marker1VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.omnichef.procedures.Marker1SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture(java.util.Objects.toString(net.mcreator.omnichef.procedures.CriticMarker1VisibilityProcedure.execute(entity), ""),
							net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, w / 2 + -64 + guiTools$xOffset, 19 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.omnichef.procedures.Marker2VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.omnichef.procedures.Marker2SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture(java.util.Objects.toString(net.mcreator.omnichef.procedures.CriticMarker2VisibilityProcedure.execute(entity), ""),
							net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, w / 2 + -64 + guiTools$xOffset, 19 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.omnichef.procedures.Marker3VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.omnichef.procedures.Marker3SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture(java.util.Objects.toString(net.mcreator.omnichef.procedures.CriticMarker3VisibilityProcedure.execute(entity), ""),
							net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, w / 2 + -64 + guiTools$xOffset, 19 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
			}
		}
	}

	private static net.minecraft.resources.ResourceLocation guiTools$dynamicTexture(String value, net.minecraft.resources.ResourceLocation fallback) {
		if (value == null || value.isBlank())
			return fallback;
		try {
			String texture = value.trim().replace('\\', '/');
			if (texture.indexOf(':') >= 0)
				return net.minecraft.resources.ResourceLocation.parse(texture);
			while (texture.startsWith("/"))
				texture = texture.substring(1);
			if (texture.startsWith("textures/screens/"))
				texture = texture.substring("textures/screens/".length());
			if (!texture.endsWith(".png"))
				texture += ".png";
			return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("omnichef", "textures/screens/" + texture);
		} catch (RuntimeException ignored) {
			return fallback;
		}
	}

	private static void guiTools$alphaBlit(net.minecraft.client.gui.GuiGraphics graphics, net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		boolean wasBlending = org.lwjgl.opengl.GL11.glIsEnabled(org.lwjgl.opengl.GL11.GL_BLEND);
		com.mojang.blaze3d.systems.RenderSystem.enableBlend();
		com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
		try {
			graphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
		} finally {
			if (!wasBlending)
				com.mojang.blaze3d.systems.RenderSystem.disableBlend();
		}
	}
}