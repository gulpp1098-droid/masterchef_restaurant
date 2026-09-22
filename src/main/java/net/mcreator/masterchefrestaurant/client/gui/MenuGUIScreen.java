package net.mcreator.masterchefrestaurant.client.gui;

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

import net.mcreator.masterchefrestaurant.world.inventory.MenuGUIMenu;
import net.mcreator.masterchefrestaurant.procedures.*;
import net.mcreator.masterchefrestaurant.network.MenuGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class MenuGUIScreen extends AbstractContainerScreen<MenuGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_next_page_icon;
	private ImageButton imagebutton_last_page_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/menu_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatulagui.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/ribbon_icon.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_6 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_7 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_8 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_9 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_10 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_11 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_12 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_13 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");

	public MenuGUIScreen(MenuGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 131;
		this.imageHeight = 142;
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
		guiTools$sizedTextLabelTooltips : {
			String guiTools$sizedLabelText0 = java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food1NameReturnProcedure.execute(entity), "");
			if (true && mouseX >= this.leftPos + -18 && mouseX < this.leftPos + 67 && mouseY >= this.topPos + 16 && mouseY < this.topPos + 26 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText0, 85, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText0), mouseX, mouseY);
			String guiTools$sizedLabelText1 = java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food2NameReturnProcedure.execute(entity), "");
			if (true && mouseX >= this.leftPos + -18 && mouseX < this.leftPos + 67 && mouseY >= this.topPos + 52 && mouseY < this.topPos + 62 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText1, 85, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText1), mouseX, mouseY);
			String guiTools$sizedLabelText2 = java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food3NameReturnProcedure.execute(entity), "");
			if (true && mouseX >= this.leftPos + -18 && mouseX < this.leftPos + 67 && mouseY >= this.topPos + 87 && mouseY < this.topPos + 97 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText2, 85, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText2), mouseX, mouseY);
			String guiTools$sizedLabelText3 = java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food4NameReturnProcedure.execute(entity), "");
			if (true && mouseX >= this.leftPos + 71 && mouseX < this.leftPos + 156 && mouseY >= this.topPos + 16 && mouseY < this.topPos + 26 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText3, 85, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText3), mouseX, mouseY);
			String guiTools$sizedLabelText4 = java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food5NameReturnProcedure.execute(entity), "");
			if (true && mouseX >= this.leftPos + 71 && mouseX < this.leftPos + 156 && mouseY >= this.topPos + 52 && mouseY < this.topPos + 62 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText4, 85, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText4), mouseX, mouseY);
			String guiTools$sizedLabelText5 = java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food6NameReturnProcedure.execute(entity), "");
			if (true && mouseX >= this.leftPos + 71 && mouseX < this.leftPos + 156 && mouseY >= this.topPos + 88 && mouseY < this.topPos + 98 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText5, 85, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText5), mouseX, mouseY);
		}
		guiTools$itemDisplayTooltips : {
			guiTools$itemDisplayTooltip0 : {
				if (!(true))
					break guiTools$itemDisplayTooltip0;
				if (mouseX < this.leftPos + -17 || mouseX >= this.leftPos + -1 || mouseY < this.topPos + 28 || mouseY >= this.topPos + 44)
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
				if (mouseX < this.leftPos + -17 || mouseX >= this.leftPos + -1 || mouseY < this.topPos + 64 || mouseY >= this.topPos + 80)
					break guiTools$itemDisplayTooltip1;
				boolean guiTools$displayMasked1 = false;
				if (guiTools$displayMasked1)
					break guiTools$itemDisplayTooltip1;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack1 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack1 == null || guiTools$tooltipStack1.isEmpty())
					break guiTools$itemDisplayTooltip1;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack1, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip2 : {
				if (!(true))
					break guiTools$itemDisplayTooltip2;
				if (mouseX < this.leftPos + -17 || mouseX >= this.leftPos + -1 || mouseY < this.topPos + 100 || mouseY >= this.topPos + 116)
					break guiTools$itemDisplayTooltip2;
				boolean guiTools$displayMasked2 = false;
				if (guiTools$displayMasked2)
					break guiTools$itemDisplayTooltip2;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack2 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack2 == null || guiTools$tooltipStack2.isEmpty())
					break guiTools$itemDisplayTooltip2;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack2, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip3 : {
				if (!(true))
					break guiTools$itemDisplayTooltip3;
				if (mouseX < this.leftPos + 72 || mouseX >= this.leftPos + 88 || mouseY < this.topPos + 28 || mouseY >= this.topPos + 44)
					break guiTools$itemDisplayTooltip3;
				boolean guiTools$displayMasked3 = false;
				if (guiTools$displayMasked3)
					break guiTools$itemDisplayTooltip3;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack3 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack3 == null || guiTools$tooltipStack3.isEmpty())
					break guiTools$itemDisplayTooltip3;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack3, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip4 : {
				if (!(true))
					break guiTools$itemDisplayTooltip4;
				if (mouseX < this.leftPos + 72 || mouseX >= this.leftPos + 88 || mouseY < this.topPos + 64 || mouseY >= this.topPos + 80)
					break guiTools$itemDisplayTooltip4;
				boolean guiTools$displayMasked4 = false;
				if (guiTools$displayMasked4)
					break guiTools$itemDisplayTooltip4;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack4 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack4 == null || guiTools$tooltipStack4.isEmpty())
					break guiTools$itemDisplayTooltip4;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack4, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip5 : {
				if (!(true))
					break guiTools$itemDisplayTooltip5;
				if (mouseX < this.leftPos + 72 || mouseX >= this.leftPos + 88 || mouseY < this.topPos + 100 || mouseY >= this.topPos + 116)
					break guiTools$itemDisplayTooltip5;
				boolean guiTools$displayMasked5 = false;
				if (guiTools$displayMasked5)
					break guiTools$itemDisplayTooltip5;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack5 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack5 == null || guiTools$tooltipStack5.isEmpty())
					break guiTools$itemDisplayTooltip5;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack5, mouseX, mouseY);
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
			guiTools$alphaBlit(guiGraphics, IMAGE_0, this.leftPos + -34, this.topPos + -23, 0, 0, 203, 176, 203, 176);
			guiTools$alphaBlit(guiGraphics, IMAGE_1, this.leftPos + 22, this.topPos + -12, 0, 0, 93, 21, 93, 21);
			guiTools$alphaBlit(guiGraphics, IMAGE_2, this.leftPos + -18, this.topPos + 27, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_3, this.leftPos + -18, this.topPos + 63, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_4, this.leftPos + -18, this.topPos + 99, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_5, this.leftPos + 3, this.topPos + 28, 0, 0, 15, 17, 15, 17);
			guiTools$alphaBlit(guiGraphics, IMAGE_6, this.leftPos + 4, this.topPos + 63, 0, 0, 15, 17, 15, 17);
			guiTools$alphaBlit(guiGraphics, IMAGE_7, this.leftPos + 4, this.topPos + 100, 0, 0, 15, 17, 15, 17);
			guiTools$alphaBlit(guiGraphics, IMAGE_8, this.leftPos + 71, this.topPos + 27, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_9, this.leftPos + 71, this.topPos + 63, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_10, this.leftPos + 71, this.topPos + 99, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_11, this.leftPos + 95, this.topPos + 28, 0, 0, 15, 17, 15, 17);
			guiTools$alphaBlit(guiGraphics, IMAGE_12, this.leftPos + 95, this.topPos + 64, 0, 0, 15, 17, 15, 17);
			guiTools$alphaBlit(guiGraphics, IMAGE_13, this.leftPos + 95, this.topPos + 100, 0, 0, 15, 17, 15, 17);
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
						guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + -17, this.topPos + 28);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + -17, this.topPos + 28);
				}
				if (!guiTools$displayMasked0)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack0, this.leftPos + -17, this.topPos + 28);
			}
			guiTools$itemDisplay1 : {
				if (!(true))
					break guiTools$itemDisplay1;
				net.minecraft.world.item.ItemStack guiTools$displayStack1 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack1 == null || guiTools$displayStack1.isEmpty())
					break guiTools$itemDisplay1;
				boolean guiTools$displayMasked1 = false;
				if (guiTools$displayMasked1) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + -17, this.topPos + 64);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + -17, this.topPos + 64);
				}
				if (!guiTools$displayMasked1)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack1, this.leftPos + -17, this.topPos + 64);
			}
			guiTools$itemDisplay2 : {
				if (!(true))
					break guiTools$itemDisplay2;
				net.minecraft.world.item.ItemStack guiTools$displayStack2 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack2 == null || guiTools$displayStack2.isEmpty())
					break guiTools$itemDisplay2;
				boolean guiTools$displayMasked2 = false;
				if (guiTools$displayMasked2) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + -17, this.topPos + 100);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + -17, this.topPos + 100);
				}
				if (!guiTools$displayMasked2)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack2, this.leftPos + -17, this.topPos + 100);
			}
			guiTools$itemDisplay3 : {
				if (!(true))
					break guiTools$itemDisplay3;
				net.minecraft.world.item.ItemStack guiTools$displayStack3 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack3 == null || guiTools$displayStack3.isEmpty())
					break guiTools$itemDisplay3;
				boolean guiTools$displayMasked3 = false;
				if (guiTools$displayMasked3) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 72, this.topPos + 28);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 72, this.topPos + 28);
				}
				if (!guiTools$displayMasked3)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack3, this.leftPos + 72, this.topPos + 28);
			}
			guiTools$itemDisplay4 : {
				if (!(true))
					break guiTools$itemDisplay4;
				net.minecraft.world.item.ItemStack guiTools$displayStack4 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack4 == null || guiTools$displayStack4.isEmpty())
					break guiTools$itemDisplay4;
				boolean guiTools$displayMasked4 = false;
				if (guiTools$displayMasked4) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 72, this.topPos + 64);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 72, this.topPos + 64);
				}
				if (!guiTools$displayMasked4)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack4, this.leftPos + 72, this.topPos + 64);
			}
			guiTools$itemDisplay5 : {
				if (!(true))
					break guiTools$itemDisplay5;
				net.minecraft.world.item.ItemStack guiTools$displayStack5 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack5 == null || guiTools$displayStack5.isEmpty())
					break guiTools$itemDisplay5;
				boolean guiTools$displayMasked5 = false;
				if (guiTools$displayMasked5) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + 72, this.topPos + 100);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + 72, this.topPos + 100);
				}
				if (!guiTools$displayMasked5)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack5, this.leftPos + 72, this.topPos + 100);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.menu_gui.label_management"), 34, -9, -1, false);
		guiGraphics.drawString(this.font, Food1RewardReturnProcedure.execute(world, entity), 18, 32, -12829636, false);
		guiGraphics.drawString(this.font, Food2RewardReturnProcedure.execute(world, entity), 18, 67, -12829636, false);
		guiGraphics.drawString(this.font, Food3RewardReturnProcedure.execute(world, entity), 18, 105, -12829636, false);
		guiGraphics.drawString(this.font, PageReturnProcedure.execute(entity), 50, 125, -12829636, false);
		guiGraphics.drawString(this.font, Food4RewardReturnProcedure.execute(world, entity), 110, 32, -12829636, false);
		guiGraphics.drawString(this.font, Food5RewardReturnProcedure.execute(world, entity), 110, 67, -12829636, false);
		guiGraphics.drawString(this.font, Food6RewardReturnProcedure.execute(world, entity), 110, 105, -12829636, false);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food1NameReturnProcedure.execute(entity), ""), -18, 16, 85, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food2NameReturnProcedure.execute(entity), ""), -18, 52, 85, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food3NameReturnProcedure.execute(entity), ""), -18, 87, 85, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food4NameReturnProcedure.execute(entity), ""), 71, 16, 85, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food5NameReturnProcedure.execute(entity), ""), 71, 52, 85, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.Food6NameReturnProcedure.execute(entity), ""), 71, 88, 85, -12829636, false, 1.00F, 2);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_next_page_icon = new ImageButton(this.leftPos + 137, this.topPos + 121, 16, 16,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/next_page_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/next_page_icon.png")), e -> {
					int x = MenuGUIScreen.this.x;
					int y = MenuGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new MenuGUIButtonMessage(0, x, y, z));
						MenuGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_next_page_icon);
		imagebutton_last_page_icon = new ImageButton(this.leftPos + -18, this.topPos + 121, 16, 16,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/last_page_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/last_page_icon.png")), e -> {
					int x = MenuGUIScreen.this.x;
					int y = MenuGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new MenuGUIButtonMessage(1, x, y, z));
						MenuGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_last_page_icon);
	}

	private void guiTools$renderSizedTextLabel(GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int color, boolean shadow, float scale, int overflowMode) {
		if (text == null || scale <= 0.0F || boxWidth <= 0)
			return;
		int textWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		String displayed = overflowMode == 2 ? this.guiTools$ellipsizeSizedText(text, textWidth) : text;
		boolean clip = overflowMode != 0;
		if (clip)
			guiGraphics.enableScissor(this.leftPos + x, this.topPos + y, this.leftPos + x + boxWidth, this.topPos + y + Math.max(1, (int) Math.ceil(this.font.lineHeight * scale)));
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.pose().translate(x, y, 0.0F);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			guiGraphics.drawString(this.font, displayed, 0, 0, color, shadow);
		} finally {
			guiGraphics.pose().popPose();
			if (clip)
				guiGraphics.disableScissor();
		}
	}

	private boolean guiTools$isSizedTextTruncated(String text, int boxWidth, float scale) {
		return text != null && scale > 0.0F && this.font.width(text) > Math.max(1, (int) Math.floor(boxWidth / scale));
	}

	private String guiTools$ellipsizeSizedText(String value, int maxWidth) {
		if (this.font.width(value) <= maxWidth)
			return value;
		String ellipsis = "…";
		if (this.font.width(ellipsis) > maxWidth)
			return "";
		int low = 0, high = value.length();
		while (low < high) {
			int middle = (low + high + 1) >>> 1;
			if (this.font.width(value.substring(0, middle) + ellipsis) <= maxWidth)
				low = middle;
			else
				high = middle - 1;
		}
		return value.substring(0, low).stripTrailing() + ellipsis;
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