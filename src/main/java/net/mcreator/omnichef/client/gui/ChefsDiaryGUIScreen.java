package net.mcreator.omnichef.client.gui;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.ChefsDiaryGUIMenu;
import net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryGUIScreen extends AbstractContainerScreen<ChefsDiaryGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_next_page_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/chefs_diary_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("omnichef:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("omnichef:textures/screens/restaurant_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("omnichef:textures/screens/location_icon.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("omnichef:textures/screens/requirements_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("omnichef:textures/screens/open_icon.png");
	private static final ResourceLocation IMAGE_6 = ResourceLocation.parse("omnichef:textures/screens/hammer_icon.png");
	private static final ResourceLocation IMAGE_7 = ResourceLocation.parse("omnichef:textures/screens/spatula_icon.png");
	private static final ResourceLocation IMAGE_8 = ResourceLocation.parse("omnichef:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_9 = ResourceLocation.parse("omnichef:textures/screens/separator_icon.png");

	public ChefsDiaryGUIScreen(ChefsDiaryGUIMenu container, Inventory inventory, Component text) {
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
		guiTools$alphaBlit(guiGraphics, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiTools$orderedLayers : {
			guiTools$alphaBlit(guiGraphics, IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
			guiTools$alphaBlit(guiGraphics, IMAGE_1, this.leftPos + -146, this.topPos + -90, 0, 0, 133, 11, 133, 11);
			guiTools$alphaBlit(guiGraphics, IMAGE_2, this.leftPos + -145, this.topPos + -81, 0, 0, 39, 32, 39, 32);
			guiTools$alphaBlit(guiGraphics, IMAGE_3, this.leftPos + 6, this.topPos + -18, 0, 0, 12, 15, 12, 15);
			guiTools$alphaBlit(guiGraphics, IMAGE_4, this.leftPos + 4, this.topPos + -45, 0, 0, 16, 16, 16, 16);
			guiTools$alphaBlit(guiGraphics, IMAGE_5, this.leftPos + 6, this.topPos + 36, 0, 0, 13, 13, 13, 13);
			guiTools$alphaBlit(guiGraphics, IMAGE_6, this.leftPos + 4, this.topPos + 9, 0, 0, 16, 16, 16, 16);
			guiTools$alphaBlit(guiGraphics, IMAGE_7, this.leftPos + 4, this.topPos + -73, 0, 0, 16, 16, 16, 16);
			guiTools$alphaBlit(guiGraphics, IMAGE_8, this.leftPos + 0, this.topPos + -90, 0, 0, 133, 11, 133, 11);
			guiTools$alphaBlit(guiGraphics, IMAGE_9, this.leftPos + -146, this.topPos + 58, 0, 0, 133, 11, 133, 11);
			if (enhanced_image_button_empty.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty.getX() && mouseY >= enhanced_image_button_empty.getY() && mouseX < enhanced_image_button_empty.getX() + 110 && mouseY < enhanced_image_button_empty.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty.getX(), enhanced_image_button_empty.getY(), 0, 0, 110, 18, 110, 18);
				String guiTools$buttonText = "My Golden Spatula";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty.getX() + 110 / 2.0, enhanced_image_button_empty.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy.getX() && mouseY >= enhanced_image_button_empty_copy.getY() && mouseX < enhanced_image_button_empty_copy.getX() + 110
						&& mouseY < enhanced_image_button_empty_copy.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy.getX(), enhanced_image_button_empty_copy.getY(), 0, 0, 110, 18, 110, 18);
				String guiTools$buttonText = "What My Restaurant Needs";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy.getX() + 110 / 2.0, enhanced_image_button_empty_copy.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy_2.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_2.getX() && mouseY >= enhanced_image_button_empty_copy_2.getY() && mouseX < enhanced_image_button_empty_copy_2.getX() + 110
						&& mouseY < enhanced_image_button_empty_copy_2.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_2.getX(), enhanced_image_button_empty_copy_2.getY(), 0, 0, 110, 18, 110, 18);
				String guiTools$buttonText = "Finding the Right Place";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_2.getX() + 110 / 2.0, enhanced_image_button_empty_copy_2.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy_3.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_3.getX() && mouseY >= enhanced_image_button_empty_copy_3.getY() && mouseX < enhanced_image_button_empty_copy_3.getX() + 110
						&& mouseY < enhanced_image_button_empty_copy_3.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_3.getX(), enhanced_image_button_empty_copy_3.getY(), 0, 0, 110, 18, 110, 18);
				String guiTools$buttonText = "Putting It All Together";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_3.getX() + 110 / 2.0, enhanced_image_button_empty_copy_3.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy_4.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_4.getX() && mouseY >= enhanced_image_button_empty_copy_4.getY() && mouseX < enhanced_image_button_empty_copy_4.getX() + 110
						&& mouseY < enhanced_image_button_empty_copy_4.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_4.getX(), enhanced_image_button_empty_copy_4.getY(), 0, 0, 110, 18, 110, 18);
				String guiTools$buttonText = "Opening Day";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_4.getX() + 110 / 2.0, enhanced_image_button_empty_copy_4.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (red_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= red_bookmark.getX() && mouseY >= red_bookmark.getY() && mouseX < red_bookmark.getX() + 35 && mouseY < red_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, red_bookmark.getX(), red_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (yellow_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_yellow.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= yellow_bookmark.getX() && mouseY >= yellow_bookmark.getY() && mouseX < yellow_bookmark.getX() + 35 && mouseY < yellow_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, yellow_bookmark.getX(), yellow_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (green_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_green.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= green_bookmark.getX() && mouseY >= green_bookmark.getY() && mouseX < green_bookmark.getX() + 35 && mouseY < green_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, green_bookmark.getX(), green_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (gray_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_gray.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= gray_bookmark.getX() && mouseY >= gray_bookmark.getY() && mouseX < gray_bookmark.getX() + 35 && mouseY < gray_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, gray_bookmark.getX(), gray_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (blue_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blue.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= blue_bookmark.getX() && mouseY >= blue_bookmark.getY() && mouseX < blue_bookmark.getX() + 35 && mouseY < blue_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, blue_bookmark.getX(), blue_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/base_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 148 + guiTools$xOffset, this.topPos + -97 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/food_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 149 + guiTools$xOffset, this.topPos + -68 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/clients_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 149 + guiTools$xOffset, this.topPos + -39 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/appliences_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 149 + guiTools$xOffset, this.topPos + -10 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/stats_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 149 + guiTools$xOffset, this.topPos + 19 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
		}
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_gui.label_overview_wip"), -145, -98, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_gui.label_welcome_chef"), -102, -79, -3407872, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_gui.label_basic_tutorial"), 4, -98, -12829636, false);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_this_diary_will_guide_you_throug", "I have always dreamt of becoming a real"), -102, -68, 87, 22, -12829636, false, 1.00F, 0, 0);
		this.guiTools$renderMultilineLabel(guiGraphics,
				menu.getMenuState(0, "gui_tools:multiline:multiline_label_the_basics_of_running_your_own_r", "chef. Now it is finally my time. All I need is a restaurant, a menu, some hungry customers... and preferably no kitchen fires."), -145, -39,
				130, 110, -12829636, false, 1.00F, 0, 0);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_next_page_icon = new ImageButton(this.leftPos + 108, this.topPos + 57, 15, 15,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/next_page_icon.png"), ResourceLocation.parse("omnichef:textures/screens/next_page_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(0, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_next_page_icon);
		enhanced_image_button_empty = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 22, this.topPos + -74, 110, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(1, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty);
		enhanced_image_button_empty_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 22, this.topPos + -47, 110, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(2, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy);
		enhanced_image_button_empty_copy_2 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 22, this.topPos + -20, 110, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(3, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_2);
		enhanced_image_button_empty_copy_3 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 22, this.topPos + 7, 110, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(4, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_3);
		enhanced_image_button_empty_copy_4 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 22, this.topPos + 34, 110, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(5, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_4);
		red_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -101, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(6, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 6, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(red_bookmark);
		yellow_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -72, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_yellow.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_yellow.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(7, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 7, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(yellow_bookmark);
		green_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -43, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_green.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_green.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(8, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 8, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(green_bookmark);
		gray_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -14, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_gray.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_gray.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(9, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 9, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(gray_bookmark);
		blue_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + 15, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blue.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blue.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage(10, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 10, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(blue_bookmark);
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
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_2;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_3;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_4;
	private net.minecraft.client.gui.components.ImageButton red_bookmark;
	private net.minecraft.client.gui.components.ImageButton yellow_bookmark;
	private net.minecraft.client.gui.components.ImageButton green_bookmark;
	private net.minecraft.client.gui.components.ImageButton gray_bookmark;
	private net.minecraft.client.gui.components.ImageButton blue_bookmark;

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