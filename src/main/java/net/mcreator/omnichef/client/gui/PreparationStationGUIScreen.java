package net.mcreator.omnichef.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.PreparationStationGUIMenu;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class PreparationStationGUIScreen extends AbstractContainerScreen<PreparationStationGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/preparation_station_gui.png");

	public PreparationStationGUIScreen(PreparationStationGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;
		menuStateUpdateActive = false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiTools$alphaBlit(guiGraphics, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
		guiTools$orderedLayers : {
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 176;
				int guiTools$visibleHeight = 79;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/preparation_station_background.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 0 + guiTools$xOffset, this.topPos + 0 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 176, 79);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 185;
				int guiTools$visibleHeight = 94;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/border_preparation_station.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -4 + guiTools$xOffset, this.topPos + -9 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 185, 94);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 77;
				int guiTools$visibleHeight = 65;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/preparation_station_plate.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 10 + guiTools$xOffset, this.topPos + 5 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 77, 65);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 22;
				int guiTools$visibleHeight = 22;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/preparation_arrow_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 90 + guiTools$xOffset, this.topPos + 25 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 22, 22);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/prepslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 130 + guiTools$xOffset, this.topPos + 27 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/plateslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 14 + guiTools$xOffset, this.topPos + 13 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/plateslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 40 + guiTools$xOffset, this.topPos + 4 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/plateslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 66 + guiTools$xOffset, this.topPos + 13 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/plateslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 14 + guiTools$xOffset, this.topPos + 42 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/plateslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 40 + guiTools$xOffset, this.topPos + 53 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/plateslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 66 + guiTools$xOffset, this.topPos + 42 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 60;
				int guiTools$visibleHeight = 100;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/info_note_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 187 + guiTools$xOffset, this.topPos + -4 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 60, 100);
			}
		}
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_chefs_reminderone_guest_one_d",
				"One guest, one dish.\nEverything in the input slots gets packed together \u2014 including whole stacks!\nCheck the amounts before pressing Prepare."), 194, 30, 52, 47, -12829636, false, 0.50F, 0, 0);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_chefs_reminder", "Chef\u2019s \nreminder"), 194, 7, 120, 40, -13434880, false, 1.00F, 0, 0);
	}

	@Override
	public void init() {
		super.init();
		enhanced_image_button_openclosebutton_icon = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 112, this.topPos + 51, 56, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/preparation_station_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/preparation_station_button.png")), e -> {
					int x = PreparationStationGUIScreen.this.x;
					int y = PreparationStationGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.PreparationStationGUIButtonMessage(0, x, y, z));
						net.mcreator.omnichef.network.PreparationStationGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/preparation_station_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Prepare";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_openclosebutton_icon);
	}

	private final java.util.Map<String, java.util.List<java.util.List<String>>> guiTools$multilineCache = new java.util.HashMap<>();

	private void guiTools$renderMultilineLabel(GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int boxHeight, int color, boolean shadow, float scale, int overflowMode, int alignment) {
		if (text == null || scale <= 0.0F || boxWidth <= 0 || boxHeight <= 0)
			return;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int lineStep = this.font.lineHeight + 1;
		String cacheKey = text + "\u0000" + wrapWidth;
		java.util.List<java.util.List<String>> paragraphs = this.guiTools$multilineCache.computeIfAbsent(cacheKey,
				key -> java.util.Arrays.stream(text.replace("\r", "").split("\n", -1)).map(paragraph -> this.guiTools$wrapMultilineText(paragraph, wrapWidth)).toList());
		if (this.guiTools$multilineCache.size() > 64)
			this.guiTools$multilineCache.clear();
		boolean clip = overflowMode != 0;
		if (clip)
			guiGraphics.enableScissor(this.leftPos + x, this.topPos + y, this.leftPos + x + boxWidth, this.topPos + y + boxHeight);
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.pose().translate(x, y, 0.0F);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			int currentY = 0;
			for (java.util.List<String> lines : paragraphs) {
				for (int index = 0; index < lines.size(); index++) {
					String line = lines.get(index);
					int remaining = wrapWidth - this.font.width(line);
					if (alignment == 3 && index < lines.size() - 1 && remaining > 0 && line.contains(" ")) {
						String[] words = line.split(" ");
						int advance = 0;
						for (int word = 0; word < words.length; word++) {
							int currentX = advance + (int) Math.round((double) remaining * word / (words.length - 1));
							guiGraphics.drawString(this.font, words[word], currentX, currentY, color, shadow);
							advance += this.font.width(words[word] + " ");
						}
					} else {
						int currentX = alignment == 1 ? remaining : alignment == 2 ? remaining / 2 : 0;
						guiGraphics.drawString(this.font, line, currentX, currentY, color, shadow);
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

	private java.util.List<String> guiTools$wrapMultilineText(String text, int wrapWidth) {
		java.util.List<String> lines = new java.util.ArrayList<>();
		for (String paragraph : text.replace("\r", "").split("\n", -1)) {
			if (paragraph.isEmpty()) {
				lines.add("");
				continue;
			}
			StringBuilder line = new StringBuilder();
			for (String word : paragraph.split("\s+")) {
				String candidate = line.isEmpty() ? word : line + " " + word;
				if (!line.isEmpty() && this.font.width(candidate) > wrapWidth) {
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

	private static final boolean guiTools$enhancedImageButton = true;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_openclosebutton_icon;

	private static net.minecraft.resources.ResourceLocation guiTools$buttonTexture(String value, net.minecraft.resources.ResourceLocation fallback) {
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