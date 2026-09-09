package net.mcreator.masterchefrestaurant.client.screens;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

import net.mcreator.masterchefrestaurant.procedures.ReturnTimelineOpenRestaurantProcedure;

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
		if (ReturnTimelineOpenRestaurantProcedure.execute(entity)) {

			guiTools$dynamicImages : {
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 140;
					int guiTools$visibleHeight = 10;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/timelineempty_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -69 + guiTools$xOffset, h / 2 + -112 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 140, 10);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = Math.max(0, Math.min(120, (int) Math.round(net.mcreator.masterchefrestaurant.procedures.ReturnTimelineProgressProcedure.execute(world, entity))));
					int guiTools$visibleHeight = 2;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/timelinefull_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -59 + guiTools$xOffset, h / 2 + -108 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 120, 2);
				}
				if (true) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.ReturnTimelineProgressProcedure.execute(world, entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 5;
					int guiTools$visibleHeight = 12;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/slider_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -61 + guiTools$xOffset, h / 2 + -113 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 5, 12);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 20;
					int guiTools$visibleHeight = 18;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/moon_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + 71 + guiTools$xOffset, h / 2 + -116 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 20, 18);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 20;
					int guiTools$visibleHeight = 18;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/openrestaurant_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -89 + guiTools$xOffset, h / 2 + -116 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 20, 18);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker1VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker1SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker2VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker2SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker3VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker3SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker4VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker4SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker5VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker5SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker6VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker6SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker7VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker7SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker8VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker8SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker9VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker9SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker10VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker10SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker11VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker11SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker12VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker12SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker13VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker13SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker14VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker14SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker15VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker15SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker16VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker16SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker17VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker17SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker18VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker18SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker19VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker19SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
				}
				if (net.mcreator.masterchefrestaurant.procedures.Marker20VisibilityProcedure.execute(entity)) {
					int guiTools$xOffset = (int) Math.round(net.mcreator.masterchefrestaurant.procedures.Marker20SetXProcedure.execute(entity));
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 17;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/marker_icon.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						event.getGuiGraphics().blit(guiTools$image, w / 2 + -64 + guiTools$xOffset, h / 2 + -101 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 17);
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
			return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("masterchef_restaurant", "textures/screens/" + texture);
		} catch (RuntimeException ignored) {
			return fallback;
		}
	}
}