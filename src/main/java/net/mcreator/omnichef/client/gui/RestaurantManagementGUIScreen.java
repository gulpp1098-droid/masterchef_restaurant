package net.mcreator.omnichef.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.RestaurantManagementGUIMenu;
import net.mcreator.omnichef.procedures.OpenCloseReturnProcedure;
import net.mcreator.omnichef.procedures.MaxTablesReturnProcedure;
import net.mcreator.omnichef.procedures.MaxQueueReturnProcedure;
import net.mcreator.omnichef.procedures.MaxLocationsReturnProcedure;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class RestaurantManagementGUIScreen extends AbstractContainerScreen<RestaurantManagementGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/restaurant_management_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/spatulagui.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("omnichef:textures/screens/spatulasidegui.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("omnichef:textures/screens/ribbon_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("omnichef:textures/screens/spatula_icon.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("omnichef:textures/screens/servicetable_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("omnichef:textures/screens/localization_icon.png");
	private static final ResourceLocation IMAGE_6 = ResourceLocation.parse("omnichef:textures/screens/queue_rug_item.png");
	private static final ResourceLocation IMAGE_7 = ResourceLocation.parse("omnichef:textures/screens/ribbonsmall_icon.png");
	private static final ResourceLocation SPRITE_0 = ResourceLocation.parse("omnichef:textures/screens/signopenclose_sprite.png");

	public RestaurantManagementGUIScreen(RestaurantManagementGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 0;
		this.imageHeight = 0;
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
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + -136, this.topPos + -94, 0, 0, 203, 176, 203, 176);
		guiGraphics.blit(IMAGE_1, this.leftPos + 72, this.topPos + -94, 0, 0, 91, 176, 91, 176);
		guiGraphics.blit(IMAGE_2, this.leftPos + -81, this.topPos + -83, 0, 0, 93, 21, 93, 21);
		guiGraphics.blit(IMAGE_3, this.leftPos + -72, this.topPos + -84, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_4, this.leftPos + 85, this.topPos + -52, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_5, this.leftPos + 86, this.topPos + 10, 0, 0, 15, 14, 15, 14);
		guiGraphics.blit(IMAGE_6, this.leftPos + 85, this.topPos + -21, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_7, this.leftPos + 85, this.topPos + -83, 0, 0, 65, 20, 65, 20);
		guiGraphics.blit(SPRITE_0, this.leftPos + -107, this.topPos + 10, 0, Mth.clamp((int) OpenCloseReturnProcedure.execute(entity) * 39, 0, 39), 57, 39, 57, 78);
		RenderSystem.disableBlend();
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.restaurant_management_gui.label_management"), -56, -80, -1, false);
		guiGraphics.drawString(this.font, MaxTablesReturnProcedure.execute(entity), 108, -49, -12829636, false);
		guiGraphics.drawString(this.font, MaxQueueReturnProcedure.execute(entity), 108, -17, -12829636, false);
		guiGraphics.drawString(this.font, MaxLocationsReturnProcedure.execute(entity), 108, 13, -12829636, false);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_first_linesecond_line", java.util.Objects.toString(net.mcreator.omnichef.procedures.MenuNameReturnProcedure.execute(entity), "")), 97,
				-82, 42, 13, -1, false, 0.75F, 0, 2);
	}

	@Override
	public void init() {
		super.init();
		enhanced_image_button_button_icon2 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + -118, this.topPos + -59, 165, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/button_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/button_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage(0, x, y, z));
						net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/button_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Create / Delete Restaurant";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -16777216, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_button_icon2);
		enhanced_image_button_button_icon2_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + -118, this.topPos + -36, 81, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage(1, x, y, z));
						net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Set Area";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -16777216, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_button_icon2_copy);
		enhanced_image_button_button_icon2_copy_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + -34, this.topPos + -36, 81, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage(2, x, y, z));
						net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Relocate Area";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -16777216, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_button_icon2_copy_copy);
		enhanced_image_button_button_icon2_copy_copy_2 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + -118, this.topPos + -13, 81, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage(3, x, y, z));
						net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Open / Close";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -16777216, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_button_icon2_copy_copy_2);
		enhanced_image_button_button_icon2_copy_copy_2_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + -34, this.topPos + -13, 81, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage(4, x, y, z));
						net.mcreator.omnichef.network.RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/openclosebutton_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Recipes Info";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -16777216, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_button_icon2_copy_copy_2_copy);
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
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_button_icon2;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_button_icon2_copy;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_button_icon2_copy_copy;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_button_icon2_copy_copy_2;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_button_icon2_copy_copy_2_copy;

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
}