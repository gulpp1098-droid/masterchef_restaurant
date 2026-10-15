package net.mcreator.omnichef.client.screens;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

import net.mcreator.omnichef.procedures.ShouldShowRestaurantProgressProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class ProgressBarRestaurantOverlay {
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
		if (ShouldShowRestaurantProgressProcedure.execute(entity)) {
			guiTools$orderedLayers : {
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 99;
					int guiTools$visibleHeight = 48;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/background_exp.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, 0 + guiTools$xOffset, h - 48 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 99, 48);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 78;
					int guiTools$visibleHeight = 8;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/progress_bar.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, 17 + guiTools$xOffset, h - 38 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 78, 8);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = Math.max(0, Math.min(76, (int) Math.round(net.mcreator.omnichef.procedures.ReturnReputationBarWidthProcedure.execute(entity))));
					int guiTools$visibleHeight = 6;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/reputation_bar.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, 18 + guiTools$xOffset, h - 37 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 76, 6);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 78;
					int guiTools$visibleHeight = 8;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/progress_bar.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, 17 + guiTools$xOffset, h - 17 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 78, 8);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = Math.max(0, Math.min(76, (int) Math.round(net.mcreator.omnichef.procedures.ReturnRecipeBarWidthProcedure.execute(entity))));
					int guiTools$visibleHeight = 6;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/recipe_bar.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, 18 + guiTools$xOffset, h - 16 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 76, 6);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 11;
					int guiTools$visibleHeight = 10;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/star_reputation.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, 5 + guiTools$xOffset, h - 40 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 11, 10);
				}
				if (true) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 9;
					int guiTools$visibleHeight = 10;
					net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/recipe.png"));
					if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
						guiTools$alphaBlit(event.getGuiGraphics(), guiTools$image, 6 + guiTools$xOffset, h - 18 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 9, 10);
				}

				if (true)
					guiTools$renderOverlaySizedTextLabel(event.getGuiGraphics(), java.util.Objects.toString(net.mcreator.omnichef.procedures.ReturnProgressReputationTextProcedure.execute(entity), ""), 34, h - 37, 44, -1, false, 0.75F, 0);
				if (true)
					guiTools$renderOverlaySizedTextLabel(event.getGuiGraphics(), java.util.Objects.toString(net.mcreator.omnichef.procedures.ReturnProgressDiscoveryTextProcedure.execute(entity), ""), 39, h - 16, 44, -1, false, 0.75F, 0);
				if (true)
					guiTools$renderOverlaySizedTextLabel(event.getGuiGraphics(), "REPUTATION", 18, h - 44, 52, -1, false, 0.75F, 2);
				if (true)
					guiTools$renderOverlaySizedTextLabel(event.getGuiGraphics(), java.util.Objects.toString(net.mcreator.omnichef.procedures.ReturnProgressLevelTextProcedure.execute(entity), ""), 67, h - 44, 29, -1, false, 0.75F, 0);
				if (true)
					guiTools$renderOverlaySizedTextLabel(event.getGuiGraphics(), "RECIPE DISCOVERY", 18, h - 23, 52, -1, false, 0.75F, 0);
				if (net.mcreator.omnichef.procedures.ShouldShowNewRecipeProcedure.execute(entity))
					guiTools$renderOverlaySizedTextLabel(event.getGuiGraphics(), "NEW!", 4, h - 8, 52, -16711936, false, 0.75F, 0);
			}
		}
	}

	private static void guiTools$renderOverlaySizedTextLabel(net.minecraft.client.gui.GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int color, boolean shadow, float scale, int overflowMode) {
		if (text == null || scale <= 0.0F || boxWidth <= 0)
			return;
		net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
		int textWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		String displayed = overflowMode == 2 ? guiTools$ellipsizeOverlaySizedText(font, text, textWidth) : text;
		boolean clip = overflowMode != 0;
		if (clip)
			guiGraphics.enableScissor(x, y, x + boxWidth, y + Math.max(1, (int) Math.ceil(font.lineHeight * scale)));
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.pose().translate(x, y, 0.0F);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			guiGraphics.drawString(font, displayed, 0, 0, color, shadow);
		} finally {
			guiGraphics.pose().popPose();
			if (clip)
				guiGraphics.disableScissor();
		}
	}

	private static String guiTools$ellipsizeOverlaySizedText(net.minecraft.client.gui.Font font, String value, int maxWidth) {
		if (font.width(value) <= maxWidth)
			return value;
		String ellipsis = "…";
		if (font.width(ellipsis) > maxWidth)
			return "";
		int low = 0, high = value.length();
		while (low < high) {
			int middle = (low + high + 1) >>> 1;
			if (font.width(value.substring(0, middle) + ellipsis) <= maxWidth)
				low = middle;
			else
				high = middle - 1;
		}
		return value.substring(0, low).stripTrailing() + ellipsis;
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