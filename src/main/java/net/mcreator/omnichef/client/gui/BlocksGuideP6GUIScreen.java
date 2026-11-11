package net.mcreator.omnichef.client.gui;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.BlocksGuideP6GUIMenu;
import net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class BlocksGuideP6GUIScreen extends AbstractContainerScreen<BlocksGuideP6GUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_last_page_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/blocks_guide_p_6_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("omnichef:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("omnichef:textures/screens/separator_icon.png");

	public BlocksGuideP6GUIScreen(BlocksGuideP6GUIMenu container, Inventory inventory, Component text) {
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
		guiTools$itemDisplayTooltips : {
			guiTools$itemDisplayTooltip1 : {
				if (!(true))
					break guiTools$itemDisplayTooltip1;
				if (mouseX < this.leftPos + 25 || mouseX >= this.leftPos + 49 || mouseY < this.topPos + -31 || mouseY >= this.topPos + -7)
					break guiTools$itemDisplayTooltip1;
				boolean guiTools$displayMasked1 = false;
				if (guiTools$displayMasked1)
					break guiTools$itemDisplayTooltip1;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack1 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$tooltipStack1 == null || guiTools$tooltipStack1.isEmpty())
					break guiTools$itemDisplayTooltip1;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack1, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip2 : {
				if (!(true))
					break guiTools$itemDisplayTooltip2;
				if (mouseX < this.leftPos + 25 || mouseX >= this.leftPos + 49 || mouseY < this.topPos + -1 || mouseY >= this.topPos + 23)
					break guiTools$itemDisplayTooltip2;
				boolean guiTools$displayMasked2 = false;
				if (guiTools$displayMasked2)
					break guiTools$itemDisplayTooltip2;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack2 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$tooltipStack2 == null || guiTools$tooltipStack2.isEmpty())
					break guiTools$itemDisplayTooltip2;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack2, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip3 : {
				if (!(true))
					break guiTools$itemDisplayTooltip3;
				if (mouseX < this.leftPos + 55 || mouseX >= this.leftPos + 79 || mouseY < this.topPos + -1 || mouseY >= this.topPos + 23)
					break guiTools$itemDisplayTooltip3;
				boolean guiTools$displayMasked3 = false;
				if (guiTools$displayMasked3)
					break guiTools$itemDisplayTooltip3;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack3 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$tooltipStack3 == null || guiTools$tooltipStack3.isEmpty())
					break guiTools$itemDisplayTooltip3;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack3, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip4 : {
				if (!(true))
					break guiTools$itemDisplayTooltip4;
				if (mouseX < this.leftPos + 85 || mouseX >= this.leftPos + 109 || mouseY < this.topPos + -1 || mouseY >= this.topPos + 23)
					break guiTools$itemDisplayTooltip4;
				boolean guiTools$displayMasked4 = false;
				if (guiTools$displayMasked4)
					break guiTools$itemDisplayTooltip4;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack4 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$tooltipStack4 == null || guiTools$tooltipStack4.isEmpty())
					break guiTools$itemDisplayTooltip4;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack4, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip5 : {
				if (!(true))
					break guiTools$itemDisplayTooltip5;
				if (mouseX < this.leftPos + 85 || mouseX >= this.leftPos + 109 || mouseY < this.topPos + -31 || mouseY >= this.topPos + -7)
					break guiTools$itemDisplayTooltip5;
				boolean guiTools$displayMasked5 = false;
				if (guiTools$displayMasked5)
					break guiTools$itemDisplayTooltip5;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack5 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$tooltipStack5 == null || guiTools$tooltipStack5.isEmpty())
					break guiTools$itemDisplayTooltip5;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack5, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip6 : {
				if (!(true))
					break guiTools$itemDisplayTooltip6;
				if (mouseX < this.leftPos + 55 || mouseX >= this.leftPos + 79 || mouseY < this.topPos + -31 || mouseY >= this.topPos + -7)
					break guiTools$itemDisplayTooltip6;
				boolean guiTools$displayMasked6 = false;
				if (guiTools$displayMasked6)
					break guiTools$itemDisplayTooltip6;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack6 = menu.getMenuState(3, "107", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack6 == null || guiTools$tooltipStack6.isEmpty())
					break guiTools$itemDisplayTooltip6;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack6, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiTools$alphaBlit(guiGraphics, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiTools$orderedLayers : {
			guiTools$alphaBlit(guiGraphics, IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
			guiTools$alphaBlit(guiGraphics, IMAGE_1, this.leftPos + 1, this.topPos + -90, 0, 0, 133, 11, 133, 11);
			guiTools$alphaBlit(guiGraphics, IMAGE_2, this.leftPos + 1, this.topPos + 40, 0, 0, 133, 11, 133, 11);
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 92;
				int guiTools$visibleHeight = 92;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/crafting_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 21 + guiTools$xOffset, this.topPos + -65 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 92, 92);
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
			guiTools$itemDisplay0 : {
				if (!(true))
					break guiTools$itemDisplay0;
				net.minecraft.world.item.ItemStack guiTools$displayStack0 = menu.getMenuState(3, "104", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack0 == null || guiTools$displayStack0.isEmpty())
					break guiTools$itemDisplay0;
				boolean guiTools$displayMasked0 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 58), (1.0F - 1.0f) * (this.topPos + 54), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked0) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + 58, this.topPos + 54);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + 58, this.topPos + 54);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay1 : {
				if (!(true))
					break guiTools$itemDisplay1;
				net.minecraft.world.item.ItemStack guiTools$displayStack1 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$displayStack1 == null || guiTools$displayStack1.isEmpty())
					break guiTools$itemDisplay1;
				boolean guiTools$displayMasked1 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.5f) * (this.leftPos + 25), (1.0F - 1.5f) * (this.topPos + -31), 0.0F);
					guiGraphics.pose().scale(1.5f, 1.5f, 1.0F);
					if (guiTools$displayMasked1) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + 25, this.topPos + -31);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + 25, this.topPos + -31);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay2 : {
				if (!(true))
					break guiTools$itemDisplay2;
				net.minecraft.world.item.ItemStack guiTools$displayStack2 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$displayStack2 == null || guiTools$displayStack2.isEmpty())
					break guiTools$itemDisplay2;
				boolean guiTools$displayMasked2 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.5f) * (this.leftPos + 25), (1.0F - 1.5f) * (this.topPos + -1), 0.0F);
					guiGraphics.pose().scale(1.5f, 1.5f, 1.0F);
					if (guiTools$displayMasked2) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 25, this.topPos + -1);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 25, this.topPos + -1);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay3 : {
				if (!(true))
					break guiTools$itemDisplay3;
				net.minecraft.world.item.ItemStack guiTools$displayStack3 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$displayStack3 == null || guiTools$displayStack3.isEmpty())
					break guiTools$itemDisplay3;
				boolean guiTools$displayMasked3 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.5f) * (this.leftPos + 55), (1.0F - 1.5f) * (this.topPos + -1), 0.0F);
					guiGraphics.pose().scale(1.5f, 1.5f, 1.0F);
					if (guiTools$displayMasked3) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 55, this.topPos + -1);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 55, this.topPos + -1);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay4 : {
				if (!(true))
					break guiTools$itemDisplay4;
				net.minecraft.world.item.ItemStack guiTools$displayStack4 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$displayStack4 == null || guiTools$displayStack4.isEmpty())
					break guiTools$itemDisplay4;
				boolean guiTools$displayMasked4 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.5f) * (this.leftPos + 85), (1.0F - 1.5f) * (this.topPos + -1), 0.0F);
					guiGraphics.pose().scale(1.5f, 1.5f, 1.0F);
					if (guiTools$displayMasked4) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 85, this.topPos + -1);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 85, this.topPos + -1);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay5 : {
				if (!(true))
					break guiTools$itemDisplay5;
				net.minecraft.world.item.ItemStack guiTools$displayStack5 = menu.getMenuState(3, "0", new net.minecraft.world.item.ItemStack(Blocks.OAK_PLANKS, 1));
				if (guiTools$displayStack5 == null || guiTools$displayStack5.isEmpty())
					break guiTools$itemDisplay5;
				boolean guiTools$displayMasked5 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.5f) * (this.leftPos + 85), (1.0F - 1.5f) * (this.topPos + -31), 0.0F);
					guiGraphics.pose().scale(1.5f, 1.5f, 1.0F);
					if (guiTools$displayMasked5) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + 85, this.topPos + -31);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + 85, this.topPos + -31);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay6 : {
				if (!(true))
					break guiTools$itemDisplay6;
				net.minecraft.world.item.ItemStack guiTools$displayStack6 = menu.getMenuState(3, "107", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack6 == null || guiTools$displayStack6.isEmpty())
					break guiTools$itemDisplay6;
				boolean guiTools$displayMasked6 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.5f) * (this.leftPos + 55), (1.0F - 1.5f) * (this.topPos + -31), 0.0F);
					guiGraphics.pose().scale(1.5f, 1.5f, 1.0F);
					if (guiTools$displayMasked6) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack6, this.leftPos + 55, this.topPos + -31);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack6, this.leftPos + 55, this.topPos + -31);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.blocks_guide_p_6_gui.label_service_table"), -145, -98, -12829636, false);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_customers_will_gladly_come_and_e",
				"Once the food is ready, I gather everything one guest ordered at my Preparation Station and put their meal together.\nThen I can carry the whole serving to their table. I need to get the amounts right. Adding extra food might seem generous, but a meal that does not match the order leaves my guest less satisfied."),
				-145, -85, 130, 41, -12829636, false, 1.00F, 0, 0);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_last_page_icon = new ImageButton(this.leftPos + -141, this.topPos + 57, 15, 15,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png"), ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png")), e -> {
					int x = BlocksGuideP6GUIScreen.this.x;
					int y = BlocksGuideP6GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideP6GUIButtonMessage(0, x, y, z));
						BlocksGuideP6GUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_last_page_icon);
		red_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -101, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png")), e -> {
					int x = BlocksGuideP6GUIScreen.this.x;
					int y = BlocksGuideP6GUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage(1, x, y, z));
						net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
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
					int x = BlocksGuideP6GUIScreen.this.x;
					int y = BlocksGuideP6GUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage(2, x, y, z));
						net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
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
					int x = BlocksGuideP6GUIScreen.this.x;
					int y = BlocksGuideP6GUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage(3, x, y, z));
						net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
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
					int x = BlocksGuideP6GUIScreen.this.x;
					int y = BlocksGuideP6GUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage(4, x, y, z));
						net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
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
					int x = BlocksGuideP6GUIScreen.this.x;
					int y = BlocksGuideP6GUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage(5, x, y, z));
						net.mcreator.omnichef.network.BlocksGuideP6GUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
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