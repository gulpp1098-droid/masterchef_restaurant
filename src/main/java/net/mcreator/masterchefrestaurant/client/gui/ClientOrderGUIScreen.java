package net.mcreator.masterchefrestaurant.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.masterchefrestaurant.world.inventory.ClientOrderGUIMenu;
import net.mcreator.masterchefrestaurant.procedures.*;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ClientOrderGUIScreen extends AbstractContainerScreen<ClientOrderGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/client_order_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/ordergui.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/foodframe_icon.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/foodframe_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("masterchef_restaurant:textures/screens/foodframe_icon.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("masterchef_restaurant:textures/screens/foodframe_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("masterchef_restaurant:textures/screens/foodframe_icon.png");
	private static final ResourceLocation IMAGE_6 = ResourceLocation.parse("masterchef_restaurant:textures/screens/ribbon_icon.png");
	private static final ResourceLocation IMAGE_7 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_8 = ResourceLocation.parse("masterchef_restaurant:textures/screens/orderplace_icon.png");
	private static final ResourceLocation IMAGE_9 = ResourceLocation.parse("masterchef_restaurant:textures/screens/plate_icon.png");
	private static final ResourceLocation IMAGE_10 = ResourceLocation.parse("masterchef_restaurant:textures/screens/client_icon.png");
	private static final ResourceLocation IMAGE_11 = ResourceLocation.parse("masterchef_restaurant:textures/screens/tick_icon.png");
	private static final ResourceLocation IMAGE_12 = ResourceLocation.parse("masterchef_restaurant:textures/screens/tick_icon.png");
	private static final ResourceLocation IMAGE_13 = ResourceLocation.parse("masterchef_restaurant:textures/screens/tick_icon.png");
	private static final ResourceLocation IMAGE_14 = ResourceLocation.parse("masterchef_restaurant:textures/screens/tick_icon.png");
	private static final ResourceLocation IMAGE_15 = ResourceLocation.parse("masterchef_restaurant:textures/screens/tick_icon.png");
	private static final ResourceLocation SPRITE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/patiance_sprite.png");

	public ClientOrderGUIScreen(ClientOrderGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 234;
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
			guiTools$itemDisplayTooltip0 : {
				if (!(true))
					break guiTools$itemDisplayTooltip0;
				if (mouseX < this.leftPos + 54 || mouseX >= this.leftPos + 70 || mouseY < this.topPos + 49 || mouseY >= this.topPos + 65)
					break guiTools$itemDisplayTooltip0;
				boolean guiTools$displayMasked0 = false;
				if (guiTools$displayMasked0)
					break guiTools$itemDisplayTooltip0;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack0 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack0 == null || guiTools$tooltipStack0.isEmpty())
					break guiTools$itemDisplayTooltip0;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack0, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip1 : {
				if (!(true))
					break guiTools$itemDisplayTooltip1;
				if (mouseX < this.leftPos + 77 || mouseX >= this.leftPos + 93 || mouseY < this.topPos + 49 || mouseY >= this.topPos + 65)
					break guiTools$itemDisplayTooltip1;
				boolean guiTools$displayMasked1 = false;
				if (guiTools$displayMasked1)
					break guiTools$itemDisplayTooltip1;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack1 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack1 == null || guiTools$tooltipStack1.isEmpty())
					break guiTools$itemDisplayTooltip1;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack1, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip2 : {
				if (!(true))
					break guiTools$itemDisplayTooltip2;
				if (mouseX < this.leftPos + 100 || mouseX >= this.leftPos + 116 || mouseY < this.topPos + 49 || mouseY >= this.topPos + 65)
					break guiTools$itemDisplayTooltip2;
				boolean guiTools$displayMasked2 = false;
				if (guiTools$displayMasked2)
					break guiTools$itemDisplayTooltip2;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack2 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack2 == null || guiTools$tooltipStack2.isEmpty())
					break guiTools$itemDisplayTooltip2;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack2, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip3 : {
				if (!(true))
					break guiTools$itemDisplayTooltip3;
				if (mouseX < this.leftPos + 123 || mouseX >= this.leftPos + 139 || mouseY < this.topPos + 49 || mouseY >= this.topPos + 65)
					break guiTools$itemDisplayTooltip3;
				boolean guiTools$displayMasked3 = false;
				if (guiTools$displayMasked3)
					break guiTools$itemDisplayTooltip3;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack3 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack3 == null || guiTools$tooltipStack3.isEmpty())
					break guiTools$itemDisplayTooltip3;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack3, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip4 : {
				if (!(true))
					break guiTools$itemDisplayTooltip4;
				if (mouseX < this.leftPos + 146 || mouseX >= this.leftPos + 162 || mouseY < this.topPos + 49 || mouseY >= this.topPos + 65)
					break guiTools$itemDisplayTooltip4;
				boolean guiTools$displayMasked4 = false;
				if (guiTools$displayMasked4)
					break guiTools$itemDisplayTooltip4;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack4 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack4 == null || guiTools$tooltipStack4.isEmpty())
					break guiTools$itemDisplayTooltip4;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack4, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiTools$alphaBlit(guiGraphics, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiTools$orderedImages : {
			guiTools$alphaBlit(guiGraphics, IMAGE_0, this.leftPos + -1, this.topPos + 0, 0, 0, 176, 234, 176, 234);
			guiTools$alphaBlit(guiGraphics, IMAGE_1, this.leftPos + 143, this.topPos + 43, 0, 0, 22, 44, 22, 44);
			guiTools$alphaBlit(guiGraphics, IMAGE_2, this.leftPos + 120, this.topPos + 43, 0, 0, 22, 44, 22, 44);
			guiTools$alphaBlit(guiGraphics, IMAGE_3, this.leftPos + 97, this.topPos + 43, 0, 0, 22, 44, 22, 44);
			guiTools$alphaBlit(guiGraphics, IMAGE_4, this.leftPos + 74, this.topPos + 43, 0, 0, 22, 44, 22, 44);
			guiTools$alphaBlit(guiGraphics, IMAGE_5, this.leftPos + 51, this.topPos + 43, 0, 0, 22, 44, 22, 44);
			guiTools$alphaBlit(guiGraphics, IMAGE_6, this.leftPos + 42, this.topPos + 9, 0, 0, 93, 21, 93, 21);
			guiTools$alphaBlit(guiGraphics, IMAGE_7, this.leftPos + 23, this.topPos + 30, 0, 0, 133, 11, 133, 11);
			guiTools$alphaBlit(guiGraphics, IMAGE_8, this.leftPos + 37, this.topPos + 92, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_9, this.leftPos + 39, this.topPos + 98, 0, 0, 14, 8, 14, 8);
			guiTools$alphaBlit(guiGraphics, IMAGE_10, this.leftPos + 19, this.topPos + 44, 0, 0, 20, 27, 20, 27);
			if (OrdersCheckboxesCheckSlot1Procedure.execute(entity)) {
				guiTools$alphaBlit(guiGraphics, IMAGE_11, this.leftPos + 54, this.topPos + 67, 0, 0, 16, 16, 16, 16);
			}
			if (OrdersCheckboxesCheckSlot2Procedure.execute(entity)) {
				guiTools$alphaBlit(guiGraphics, IMAGE_12, this.leftPos + 77, this.topPos + 67, 0, 0, 16, 16, 16, 16);
			}
			if (OrdersCheckboxesCheckSlot3Procedure.execute(entity)) {
				guiTools$alphaBlit(guiGraphics, IMAGE_13, this.leftPos + 100, this.topPos + 67, 0, 0, 16, 16, 16, 16);
			}
			if (OrdersCheckboxesCheckSlot4Procedure.execute(entity)) {
				guiTools$alphaBlit(guiGraphics, IMAGE_14, this.leftPos + 123, this.topPos + 67, 0, 0, 16, 16, 16, 16);
			}
			if (OrdersCheckboxesCheckSlot5Procedure.execute(entity)) {
				guiTools$alphaBlit(guiGraphics, IMAGE_15, this.leftPos + 146, this.topPos + 67, 0, 0, 16, 16, 16, 16);
			}
			guiTools$alphaBlit(guiGraphics, SPRITE_0, this.leftPos + 23, this.topPos + 75, 0, Mth.clamp((int) PatianceReturnProcedure.execute(entity) * 11, 0, 33), 11, 11, 11, 44);
			guiTools$itemDisplay0 : {
				if (!(true))
					break guiTools$itemDisplay0;
				net.minecraft.world.item.ItemStack guiTools$displayStack0 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack0 == null || guiTools$displayStack0.isEmpty())
					break guiTools$itemDisplay0;
				boolean guiTools$displayMasked0 = false;
				if (guiTools$displayMasked0) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + 54, this.topPos + 49);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + 54, this.topPos + 49);
				}
				if (!guiTools$displayMasked0)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack0, this.leftPos + 54, this.topPos + 49);
			}
			guiTools$itemDisplay1 : {
				if (!(true))
					break guiTools$itemDisplay1;
				net.minecraft.world.item.ItemStack guiTools$displayStack1 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack1 == null || guiTools$displayStack1.isEmpty())
					break guiTools$itemDisplay1;
				boolean guiTools$displayMasked1 = false;
				if (guiTools$displayMasked1) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + 77, this.topPos + 49);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + 77, this.topPos + 49);
				}
				if (!guiTools$displayMasked1)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack1, this.leftPos + 77, this.topPos + 49);
			}
			guiTools$itemDisplay2 : {
				if (!(true))
					break guiTools$itemDisplay2;
				net.minecraft.world.item.ItemStack guiTools$displayStack2 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack2 == null || guiTools$displayStack2.isEmpty())
					break guiTools$itemDisplay2;
				boolean guiTools$displayMasked2 = false;
				if (guiTools$displayMasked2) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 100, this.topPos + 49);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 100, this.topPos + 49);
				}
				if (!guiTools$displayMasked2)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack2, this.leftPos + 100, this.topPos + 49);
			}
			guiTools$itemDisplay3 : {
				if (!(true))
					break guiTools$itemDisplay3;
				net.minecraft.world.item.ItemStack guiTools$displayStack3 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack3 == null || guiTools$displayStack3.isEmpty())
					break guiTools$itemDisplay3;
				boolean guiTools$displayMasked3 = false;
				if (guiTools$displayMasked3) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 123, this.topPos + 49);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 123, this.topPos + 49);
				}
				if (!guiTools$displayMasked3)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack3, this.leftPos + 123, this.topPos + 49);
			}
			guiTools$itemDisplay4 : {
				if (!(true))
					break guiTools$itemDisplay4;
				net.minecraft.world.item.ItemStack guiTools$displayStack4 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack4 == null || guiTools$displayStack4.isEmpty())
					break guiTools$itemDisplay4;
				boolean guiTools$displayMasked4 = false;
				if (guiTools$displayMasked4) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 146, this.topPos + 49);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 146, this.topPos + 49);
				}
				if (!guiTools$displayMasked4)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack4, this.leftPos + 146, this.topPos + 49);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.client_order_gui.label_client_order"), 58, 12, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.client_order_gui.label_serve_dish"), 60, 97, -16777216, false);
	}

	@Override
	public void init() {
		super.init();
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