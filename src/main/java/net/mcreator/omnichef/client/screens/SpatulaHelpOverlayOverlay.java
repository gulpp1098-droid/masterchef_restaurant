package net.mcreator.omnichef.client.screens;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

import net.mcreator.omnichef.procedures.ShowSpatulaInstructionsProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class SpatulaHelpOverlayOverlay {
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
		if (ShowSpatulaInstructionsProcedure.execute(entity)) {

			guiTools$renderOverlayMultilineLabel(event.getGuiGraphics(), "RMB on block - Add new restaurant area\nRMB + shift - Quit area editor mode  ", 9, 32, 102, 40, -1, false, 0.75F, 0, 0);
		}
	}

	private static final java.util.Map<String, java.util.List<java.util.List<String>>> guiTools$overlayMultilineCache = new java.util.HashMap<>();

	private static void guiTools$renderOverlayMultilineLabel(net.minecraft.client.gui.GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int boxHeight, int color, boolean shadow, float scale, int overflowMode, int alignment) {
		if (text == null || scale <= 0.0F || boxWidth <= 0 || boxHeight <= 0)
			return;
		net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int lineStep = font.lineHeight + 1;
		String cacheKey = text + "\u0000" + wrapWidth;
		java.util.List<java.util.List<String>> paragraphs = guiTools$overlayMultilineCache.computeIfAbsent(cacheKey,
				key -> java.util.Arrays.stream(text.replace("\r", "").split("\n", -1)).map(paragraph -> guiTools$wrapOverlayMultilineText(font, paragraph, wrapWidth)).toList());
		if (guiTools$overlayMultilineCache.size() > 64)
			guiTools$overlayMultilineCache.clear();
		boolean clip = overflowMode != 0;
		if (clip)
			guiGraphics.enableScissor(x, y, x + boxWidth, y + boxHeight);
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.pose().translate(x, y, 0.0F);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			int currentY = 0;
			for (java.util.List<String> lines : paragraphs) {
				for (int index = 0; index < lines.size(); index++) {
					String line = lines.get(index);
					int remaining = wrapWidth - font.width(line);
					if (alignment == 3 && index < lines.size() - 1 && remaining > 0 && line.contains(" ")) {
						String[] words = line.split(" ");
						int advance = 0;
						for (int word = 0; word < words.length; word++) {
							int currentX = advance + (int) Math.round((double) remaining * word / (words.length - 1));
							guiGraphics.drawString(font, words[word], currentX, currentY, color, shadow);
							advance += font.width(words[word] + " ");
						}
					} else {
						int currentX = alignment == 1 ? remaining : alignment == 2 ? remaining / 2 : 0;
						guiGraphics.drawString(font, line, currentX, currentY, color, shadow);
					}
					currentY += lineStep;
				}
			}
		} finally {
			guiGraphics.pose().popPose();
			if (clip)
				guiGraphics.disableScissor();
		}
	}

	private static java.util.List<String> guiTools$wrapOverlayMultilineText(net.minecraft.client.gui.Font font, String text, int wrapWidth) {
		java.util.List<String> lines = new java.util.ArrayList<>();
		for (String paragraph : text.replace("\r", "").split("\n", -1)) {
			if (paragraph.isEmpty()) {
				lines.add("");
				continue;
			}
			StringBuilder line = new StringBuilder();
			for (String word : paragraph.split("\s+")) {
				String candidate = line.isEmpty() ? word : line + " " + word;
				if (!line.isEmpty() && font.width(candidate) > wrapWidth) {
					lines.add(line.toString());
					line.setLength(0);
					line.append(word);
				} else {
					line.setLength(0);
					line.append(candidate);
				}
			}
			lines.add(line.toString());
		}
		return java.util.List.copyOf(lines);
	}
}