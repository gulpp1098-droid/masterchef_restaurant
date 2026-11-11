package net.mcreator.omnichef.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.ChefsDiaryFoodGUIMenu;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryFoodGUIScreen extends AbstractContainerScreen<ChefsDiaryFoodGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/chefs_diary_food_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/chefsdiary2.png");

	public ChefsDiaryFoodGUIScreen(ChefsDiaryFoodGUIMenu container, Inventory inventory, Component text) {
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
			if (enhanced_image_button_empty.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty.getX() && mouseY >= enhanced_image_button_empty.getY() && mouseX < enhanced_image_button_empty.getX() + 40 && mouseY < enhanced_image_button_empty.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty.getX(), enhanced_image_button_empty.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 0";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty.getX() + 40 / 2.0, enhanced_image_button_empty.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
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
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy.getX() && mouseY >= enhanced_image_button_empty_copy.getY() && mouseX < enhanced_image_button_empty_copy.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy.getX(), enhanced_image_button_empty_copy.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 1";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy.getX() + 40 / 2.0, enhanced_image_button_empty_copy.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
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
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_2.getX() && mouseY >= enhanced_image_button_empty_copy_2.getY() && mouseX < enhanced_image_button_empty_copy_2.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy_2.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_2.getX(), enhanced_image_button_empty_copy_2.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 2";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_2.getX() + 40 / 2.0, enhanced_image_button_empty_copy_2.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
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
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_3.getX() && mouseY >= enhanced_image_button_empty_copy_3.getY() && mouseX < enhanced_image_button_empty_copy_3.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy_3.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_3.getX(), enhanced_image_button_empty_copy_3.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 3";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_3.getX() + 40 / 2.0, enhanced_image_button_empty_copy_3.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
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
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_4.getX() && mouseY >= enhanced_image_button_empty_copy_4.getY() && mouseX < enhanced_image_button_empty_copy_4.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy_4.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_4.getX(), enhanced_image_button_empty_copy_4.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 4";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_4.getX() + 40 / 2.0, enhanced_image_button_empty_copy_4.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy_5.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_5.getX() && mouseY >= enhanced_image_button_empty_copy_5.getY() && mouseX < enhanced_image_button_empty_copy_5.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy_5.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_5.getX(), enhanced_image_button_empty_copy_5.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 5";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_5.getX() + 40 / 2.0, enhanced_image_button_empty_copy_5.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy_6.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_6.getX() && mouseY >= enhanced_image_button_empty_copy_6.getY() && mouseX < enhanced_image_button_empty_copy_6.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy_6.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_6.getX(), enhanced_image_button_empty_copy_6.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 6";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_6.getX() + 40 / 2.0, enhanced_image_button_empty_copy_6.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy_7.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_7.getX() && mouseY >= enhanced_image_button_empty_copy_7.getY() && mouseX < enhanced_image_button_empty_copy_7.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy_7.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_7.getX(), enhanced_image_button_empty_copy_7.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 7";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_7.getX() + 40 / 2.0, enhanced_image_button_empty_copy_7.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy_8.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_8.getX() && mouseY >= enhanced_image_button_empty_copy_8.getY() && mouseX < enhanced_image_button_empty_copy_8.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy_8.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_8.getX(), enhanced_image_button_empty_copy_8.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 8";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_8.getX() + 40 / 2.0, enhanced_image_button_empty_copy_8.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
						guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
								-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					} finally {
						guiGraphics.pose().popPose();
					}
				}
			}
			if (enhanced_image_button_empty_copy_9.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_empty_copy_9.getX() && mouseY >= enhanced_image_button_empty_copy_9.getY() && mouseX < enhanced_image_button_empty_copy_9.getX() + 40
						&& mouseY < enhanced_image_button_empty_copy_9.getY() + 18;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_empty_copy_9.getX(), enhanced_image_button_empty_copy_9.getY(), 0, 0, 40, 18, 40, 18);
				String guiTools$buttonText = "Tier 9";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					try {
						guiGraphics.pose().translate(enhanced_image_button_empty_copy_9.getX() + 40 / 2.0, enhanced_image_button_empty_copy_9.getY() + 18 / 2.0, 0);
						guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_gui.label_food_wip"), -145, -98, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_gui.label_food_wip_copy"), 4, -98, -12829636, false);
		this.guiTools$renderMultilineLabel(guiGraphics,
				menu.getMenuState(0, "gui_tools:multiline:multiline_label_food_is_categoried_into_multiple",
						"I have started sorting every dish I know by how demanding it is to prepare. Simple ingredients and quick meals belong in the lower tiers, while recipes with more ingredients and preparation steps sit higher. "),
				-145, -87, 130, 155, -12829636, false, 1.00F, 0, 0);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_highertier_food_also_provides_m", "Every recipe I have discovered, filed neatly for once."), 4, -87, 130, 40, -12829636, false, 1.00F,
				0, 0);
	}

	@Override
	public void init() {
		super.init();
		enhanced_image_button_empty = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 49, this.topPos + -39, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(0, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty);
		enhanced_image_button_empty_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 5, this.topPos + -14, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(1, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy);
		enhanced_image_button_empty_copy_2 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 49, this.topPos + -14, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(2, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_2);
		enhanced_image_button_empty_copy_3 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 93, this.topPos + -14, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(3, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_3);
		enhanced_image_button_empty_copy_4 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 5, this.topPos + 13, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(4, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_4);
		enhanced_image_button_empty_copy_5 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 49, this.topPos + 13, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(5, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_5);
		enhanced_image_button_empty_copy_6 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 93, this.topPos + 13, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(6, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 6, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_6);
		enhanced_image_button_empty_copy_7 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 5, this.topPos + 39, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(7, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 7, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_7);
		enhanced_image_button_empty_copy_8 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 49, this.topPos + 39, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(8, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 8, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_8);
		enhanced_image_button_empty_copy_9 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 93, this.topPos + 39, 40, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/chefs_diary_button.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(9, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 9, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_9);
		red_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -101, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(10, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 10, x, y, z);
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
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(11, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 11, x, y, z);
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
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(12, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 12, x, y, z);
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
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(13, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 13, x, y, z);
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
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage(14, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 14, x, y, z);
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
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_5;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_6;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_7;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_8;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_9;
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