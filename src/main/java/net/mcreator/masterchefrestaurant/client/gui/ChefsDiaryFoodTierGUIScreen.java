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

import net.mcreator.masterchefrestaurant.world.inventory.ChefsDiaryFoodTierGUIMenu;
import net.mcreator.masterchefrestaurant.network.ChefsDiaryFoodTierGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryFoodTierGUIScreen extends AbstractContainerScreen<ChefsDiaryFoodTierGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private ImageButton imagebutton_last_page_icon;
	private ImageButton imagebutton_next_page_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefs_diary_food_tier_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/bookmarks.png");

	public ChefsDiaryFoodTierGUIScreen(ChefsDiaryFoodTierGUIMenu container, Inventory inventory, Component text) {
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
			guiTools$itemDisplayTooltip0 : {
				if (!(true))
					break guiTools$itemDisplayTooltip0;
				if (mouseX < this.leftPos + -128 || mouseX >= this.leftPos + -112 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip0;
				boolean guiTools$displayMasked0 = !(false);
				if (guiTools$displayMasked0)
					break guiTools$itemDisplayTooltip0;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack0 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack0 == null || guiTools$tooltipStack0.isEmpty())
					break guiTools$itemDisplayTooltip0;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack0, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip1 : {
				if (!(true))
					break guiTools$itemDisplayTooltip1;
				if (mouseX < this.leftPos + -86 || mouseX >= this.leftPos + -70 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip1;
				boolean guiTools$displayMasked1 = !(false);
				if (guiTools$displayMasked1)
					break guiTools$itemDisplayTooltip1;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack1 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack1 == null || guiTools$tooltipStack1.isEmpty())
					break guiTools$itemDisplayTooltip1;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack1, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip2 : {
				if (!(true))
					break guiTools$itemDisplayTooltip2;
				if (mouseX < this.leftPos + -44 || mouseX >= this.leftPos + -28 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip2;
				boolean guiTools$displayMasked2 = !(false);
				if (guiTools$displayMasked2)
					break guiTools$itemDisplayTooltip2;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack2 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack2 == null || guiTools$tooltipStack2.isEmpty())
					break guiTools$itemDisplayTooltip2;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack2, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip3 : {
				if (!(true))
					break guiTools$itemDisplayTooltip3;
				if (mouseX < this.leftPos + -128 || mouseX >= this.leftPos + -112 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip3;
				boolean guiTools$displayMasked3 = !(false);
				if (guiTools$displayMasked3)
					break guiTools$itemDisplayTooltip3;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack3 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack3 == null || guiTools$tooltipStack3.isEmpty())
					break guiTools$itemDisplayTooltip3;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack3, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip4 : {
				if (!(true))
					break guiTools$itemDisplayTooltip4;
				if (mouseX < this.leftPos + -86 || mouseX >= this.leftPos + -70 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip4;
				boolean guiTools$displayMasked4 = !(false);
				if (guiTools$displayMasked4)
					break guiTools$itemDisplayTooltip4;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack4 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack4 == null || guiTools$tooltipStack4.isEmpty())
					break guiTools$itemDisplayTooltip4;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack4, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip5 : {
				if (!(true))
					break guiTools$itemDisplayTooltip5;
				if (mouseX < this.leftPos + -44 || mouseX >= this.leftPos + -28 || mouseY < this.topPos + -39 || mouseY >= this.topPos + -23)
					break guiTools$itemDisplayTooltip5;
				boolean guiTools$displayMasked5 = !(false);
				if (guiTools$displayMasked5)
					break guiTools$itemDisplayTooltip5;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack5 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack5 == null || guiTools$tooltipStack5.isEmpty())
					break guiTools$itemDisplayTooltip5;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack5, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip6 : {
				if (!(true))
					break guiTools$itemDisplayTooltip6;
				if (mouseX < this.leftPos + -128 || mouseX >= this.leftPos + -112 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip6;
				boolean guiTools$displayMasked6 = !(false);
				if (guiTools$displayMasked6)
					break guiTools$itemDisplayTooltip6;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack6 = menu.getMenuState(3, "7", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack6 == null || guiTools$tooltipStack6.isEmpty())
					break guiTools$itemDisplayTooltip6;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack6, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip7 : {
				if (!(true))
					break guiTools$itemDisplayTooltip7;
				if (mouseX < this.leftPos + -86 || mouseX >= this.leftPos + -70 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip7;
				boolean guiTools$displayMasked7 = !(false);
				if (guiTools$displayMasked7)
					break guiTools$itemDisplayTooltip7;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack7 = menu.getMenuState(3, "8", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack7 == null || guiTools$tooltipStack7.isEmpty())
					break guiTools$itemDisplayTooltip7;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack7, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip8 : {
				if (!(true))
					break guiTools$itemDisplayTooltip8;
				if (mouseX < this.leftPos + -44 || mouseX >= this.leftPos + -28 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip8;
				boolean guiTools$displayMasked8 = !(false);
				if (guiTools$displayMasked8)
					break guiTools$itemDisplayTooltip8;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack8 = menu.getMenuState(3, "9", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack8 == null || guiTools$tooltipStack8.isEmpty())
					break guiTools$itemDisplayTooltip8;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack8, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip9 : {
				if (!(true))
					break guiTools$itemDisplayTooltip9;
				if (mouseX < this.leftPos + 17 || mouseX >= this.leftPos + 33 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip9;
				boolean guiTools$displayMasked9 = !(false);
				if (guiTools$displayMasked9)
					break guiTools$itemDisplayTooltip9;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack9 = menu.getMenuState(3, "10", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack9 == null || guiTools$tooltipStack9.isEmpty())
					break guiTools$itemDisplayTooltip9;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack9, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip10 : {
				if (!(true))
					break guiTools$itemDisplayTooltip10;
				if (mouseX < this.leftPos + 58 || mouseX >= this.leftPos + 74 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip10;
				boolean guiTools$displayMasked10 = !(false);
				if (guiTools$displayMasked10)
					break guiTools$itemDisplayTooltip10;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack10 = menu.getMenuState(3, "11", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack10 == null || guiTools$tooltipStack10.isEmpty())
					break guiTools$itemDisplayTooltip10;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack10, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip11 : {
				if (!(true))
					break guiTools$itemDisplayTooltip11;
				if (mouseX < this.leftPos + 99 || mouseX >= this.leftPos + 115 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip11;
				boolean guiTools$displayMasked11 = !(false);
				if (guiTools$displayMasked11)
					break guiTools$itemDisplayTooltip11;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack11 = menu.getMenuState(3, "12", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack11 == null || guiTools$tooltipStack11.isEmpty())
					break guiTools$itemDisplayTooltip11;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack11, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip12 : {
				if (!(true))
					break guiTools$itemDisplayTooltip12;
				if (mouseX < this.leftPos + 17 || mouseX >= this.leftPos + 33 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip12;
				boolean guiTools$displayMasked12 = !(false);
				if (guiTools$displayMasked12)
					break guiTools$itemDisplayTooltip12;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack12 = menu.getMenuState(3, "13", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack12 == null || guiTools$tooltipStack12.isEmpty())
					break guiTools$itemDisplayTooltip12;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack12, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip13 : {
				if (!(true))
					break guiTools$itemDisplayTooltip13;
				if (mouseX < this.leftPos + 58 || mouseX >= this.leftPos + 74 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip13;
				boolean guiTools$displayMasked13 = !(false);
				if (guiTools$displayMasked13)
					break guiTools$itemDisplayTooltip13;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack13 = menu.getMenuState(3, "14", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack13 == null || guiTools$tooltipStack13.isEmpty())
					break guiTools$itemDisplayTooltip13;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack13, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip14 : {
				if (!(true))
					break guiTools$itemDisplayTooltip14;
				if (mouseX < this.leftPos + 99 || mouseX >= this.leftPos + 115 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip14;
				boolean guiTools$displayMasked14 = !(false);
				if (guiTools$displayMasked14)
					break guiTools$itemDisplayTooltip14;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack14 = menu.getMenuState(3, "15", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack14 == null || guiTools$tooltipStack14.isEmpty())
					break guiTools$itemDisplayTooltip14;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack14, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip15 : {
				if (!(true))
					break guiTools$itemDisplayTooltip15;
				if (mouseX < this.leftPos + 17 || mouseX >= this.leftPos + 33 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip15;
				boolean guiTools$displayMasked15 = !(false);
				if (guiTools$displayMasked15)
					break guiTools$itemDisplayTooltip15;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack15 = menu.getMenuState(3, "16", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack15 == null || guiTools$tooltipStack15.isEmpty())
					break guiTools$itemDisplayTooltip15;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack15, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip16 : {
				if (!(true))
					break guiTools$itemDisplayTooltip16;
				if (mouseX < this.leftPos + 58 || mouseX >= this.leftPos + 74 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip16;
				boolean guiTools$displayMasked16 = !(false);
				if (guiTools$displayMasked16)
					break guiTools$itemDisplayTooltip16;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack16 = menu.getMenuState(3, "17", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack16 == null || guiTools$tooltipStack16.isEmpty())
					break guiTools$itemDisplayTooltip16;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack16, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip17 : {
				if (!(true))
					break guiTools$itemDisplayTooltip17;
				if (mouseX < this.leftPos + 99 || mouseX >= this.leftPos + 115 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip17;
				boolean guiTools$displayMasked17 = !(false);
				if (guiTools$displayMasked17)
					break guiTools$itemDisplayTooltip17;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack17 = menu.getMenuState(3, "18", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack17 == null || guiTools$tooltipStack17.isEmpty())
					break guiTools$itemDisplayTooltip17;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack17, mouseX, mouseY);
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
			guiTools$alphaBlit(guiGraphics, IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
			guiTools$alphaBlit(guiGraphics, IMAGE_1, this.leftPos + 141, this.topPos + -101, 0, 0, 35, 140, 35, 140);
			guiTools$itemDisplay0 : {
				if (!(true))
					break guiTools$itemDisplay0;
				net.minecraft.world.item.ItemStack guiTools$displayStack0 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack0 == null || guiTools$displayStack0.isEmpty())
					break guiTools$itemDisplay0;
				boolean guiTools$displayMasked0 = !(false);
				if (guiTools$displayMasked0) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + -128, this.topPos + -80);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + -128, this.topPos + -80);
				}
				if (!guiTools$displayMasked0)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack0, this.leftPos + -128, this.topPos + -80);
			}
			guiTools$itemDisplay1 : {
				if (!(true))
					break guiTools$itemDisplay1;
				net.minecraft.world.item.ItemStack guiTools$displayStack1 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack1 == null || guiTools$displayStack1.isEmpty())
					break guiTools$itemDisplay1;
				boolean guiTools$displayMasked1 = !(false);
				if (guiTools$displayMasked1) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + -86, this.topPos + -80);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + -86, this.topPos + -80);
				}
				if (!guiTools$displayMasked1)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack1, this.leftPos + -86, this.topPos + -80);
			}
			guiTools$itemDisplay2 : {
				if (!(true))
					break guiTools$itemDisplay2;
				net.minecraft.world.item.ItemStack guiTools$displayStack2 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack2 == null || guiTools$displayStack2.isEmpty())
					break guiTools$itemDisplay2;
				boolean guiTools$displayMasked2 = !(false);
				if (guiTools$displayMasked2) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + -44, this.topPos + -80);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + -44, this.topPos + -80);
				}
				if (!guiTools$displayMasked2)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack2, this.leftPos + -44, this.topPos + -80);
			}
			guiTools$itemDisplay3 : {
				if (!(true))
					break guiTools$itemDisplay3;
				net.minecraft.world.item.ItemStack guiTools$displayStack3 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack3 == null || guiTools$displayStack3.isEmpty())
					break guiTools$itemDisplay3;
				boolean guiTools$displayMasked3 = !(false);
				if (guiTools$displayMasked3) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + -128, this.topPos + -37);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + -128, this.topPos + -37);
				}
				if (!guiTools$displayMasked3)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack3, this.leftPos + -128, this.topPos + -37);
			}
			guiTools$itemDisplay4 : {
				if (!(true))
					break guiTools$itemDisplay4;
				net.minecraft.world.item.ItemStack guiTools$displayStack4 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack4 == null || guiTools$displayStack4.isEmpty())
					break guiTools$itemDisplay4;
				boolean guiTools$displayMasked4 = !(false);
				if (guiTools$displayMasked4) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + -86, this.topPos + -37);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + -86, this.topPos + -37);
				}
				if (!guiTools$displayMasked4)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack4, this.leftPos + -86, this.topPos + -37);
			}
			guiTools$itemDisplay5 : {
				if (!(true))
					break guiTools$itemDisplay5;
				net.minecraft.world.item.ItemStack guiTools$displayStack5 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack5 == null || guiTools$displayStack5.isEmpty())
					break guiTools$itemDisplay5;
				boolean guiTools$displayMasked5 = !(false);
				if (guiTools$displayMasked5) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + -44, this.topPos + -39);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + -44, this.topPos + -39);
				}
				if (!guiTools$displayMasked5)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack5, this.leftPos + -44, this.topPos + -39);
			}
			guiTools$itemDisplay6 : {
				if (!(true))
					break guiTools$itemDisplay6;
				net.minecraft.world.item.ItemStack guiTools$displayStack6 = menu.getMenuState(3, "7", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack6 == null || guiTools$displayStack6.isEmpty())
					break guiTools$itemDisplay6;
				boolean guiTools$displayMasked6 = !(false);
				if (guiTools$displayMasked6) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack6, this.leftPos + -128, this.topPos + 10);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack6, this.leftPos + -128, this.topPos + 10);
				}
				if (!guiTools$displayMasked6)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack6, this.leftPos + -128, this.topPos + 10);
			}
			guiTools$itemDisplay7 : {
				if (!(true))
					break guiTools$itemDisplay7;
				net.minecraft.world.item.ItemStack guiTools$displayStack7 = menu.getMenuState(3, "8", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack7 == null || guiTools$displayStack7.isEmpty())
					break guiTools$itemDisplay7;
				boolean guiTools$displayMasked7 = !(false);
				if (guiTools$displayMasked7) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack7, this.leftPos + -86, this.topPos + 10);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack7, this.leftPos + -86, this.topPos + 10);
				}
				if (!guiTools$displayMasked7)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack7, this.leftPos + -86, this.topPos + 10);
			}
			guiTools$itemDisplay8 : {
				if (!(true))
					break guiTools$itemDisplay8;
				net.minecraft.world.item.ItemStack guiTools$displayStack8 = menu.getMenuState(3, "9", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack8 == null || guiTools$displayStack8.isEmpty())
					break guiTools$itemDisplay8;
				boolean guiTools$displayMasked8 = !(false);
				if (guiTools$displayMasked8) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack8, this.leftPos + -44, this.topPos + 10);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack8, this.leftPos + -44, this.topPos + 10);
				}
				if (!guiTools$displayMasked8)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack8, this.leftPos + -44, this.topPos + 10);
			}
			guiTools$itemDisplay9 : {
				if (!(true))
					break guiTools$itemDisplay9;
				net.minecraft.world.item.ItemStack guiTools$displayStack9 = menu.getMenuState(3, "10", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack9 == null || guiTools$displayStack9.isEmpty())
					break guiTools$itemDisplay9;
				boolean guiTools$displayMasked9 = !(false);
				if (guiTools$displayMasked9) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack9, this.leftPos + 17, this.topPos + -80);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack9, this.leftPos + 17, this.topPos + -80);
				}
				if (!guiTools$displayMasked9)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack9, this.leftPos + 17, this.topPos + -80);
			}
			guiTools$itemDisplay10 : {
				if (!(true))
					break guiTools$itemDisplay10;
				net.minecraft.world.item.ItemStack guiTools$displayStack10 = menu.getMenuState(3, "11", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack10 == null || guiTools$displayStack10.isEmpty())
					break guiTools$itemDisplay10;
				boolean guiTools$displayMasked10 = !(false);
				if (guiTools$displayMasked10) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack10, this.leftPos + 58, this.topPos + -80);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack10, this.leftPos + 58, this.topPos + -80);
				}
				if (!guiTools$displayMasked10)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack10, this.leftPos + 58, this.topPos + -80);
			}
			guiTools$itemDisplay11 : {
				if (!(true))
					break guiTools$itemDisplay11;
				net.minecraft.world.item.ItemStack guiTools$displayStack11 = menu.getMenuState(3, "12", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack11 == null || guiTools$displayStack11.isEmpty())
					break guiTools$itemDisplay11;
				boolean guiTools$displayMasked11 = !(false);
				if (guiTools$displayMasked11) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack11, this.leftPos + 99, this.topPos + -80);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack11, this.leftPos + 99, this.topPos + -80);
				}
				if (!guiTools$displayMasked11)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack11, this.leftPos + 99, this.topPos + -80);
			}
			guiTools$itemDisplay12 : {
				if (!(true))
					break guiTools$itemDisplay12;
				net.minecraft.world.item.ItemStack guiTools$displayStack12 = menu.getMenuState(3, "13", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack12 == null || guiTools$displayStack12.isEmpty())
					break guiTools$itemDisplay12;
				boolean guiTools$displayMasked12 = !(false);
				if (guiTools$displayMasked12) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack12, this.leftPos + 17, this.topPos + -37);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack12, this.leftPos + 17, this.topPos + -37);
				}
				if (!guiTools$displayMasked12)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack12, this.leftPos + 17, this.topPos + -37);
			}
			guiTools$itemDisplay13 : {
				if (!(true))
					break guiTools$itemDisplay13;
				net.minecraft.world.item.ItemStack guiTools$displayStack13 = menu.getMenuState(3, "14", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack13 == null || guiTools$displayStack13.isEmpty())
					break guiTools$itemDisplay13;
				boolean guiTools$displayMasked13 = !(false);
				if (guiTools$displayMasked13) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack13, this.leftPos + 58, this.topPos + -37);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack13, this.leftPos + 58, this.topPos + -37);
				}
				if (!guiTools$displayMasked13)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack13, this.leftPos + 58, this.topPos + -37);
			}
			guiTools$itemDisplay14 : {
				if (!(true))
					break guiTools$itemDisplay14;
				net.minecraft.world.item.ItemStack guiTools$displayStack14 = menu.getMenuState(3, "15", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack14 == null || guiTools$displayStack14.isEmpty())
					break guiTools$itemDisplay14;
				boolean guiTools$displayMasked14 = !(false);
				if (guiTools$displayMasked14) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack14, this.leftPos + 99, this.topPos + -37);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack14, this.leftPos + 99, this.topPos + -37);
				}
				if (!guiTools$displayMasked14)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack14, this.leftPos + 99, this.topPos + -37);
			}
			guiTools$itemDisplay15 : {
				if (!(true))
					break guiTools$itemDisplay15;
				net.minecraft.world.item.ItemStack guiTools$displayStack15 = menu.getMenuState(3, "16", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack15 == null || guiTools$displayStack15.isEmpty())
					break guiTools$itemDisplay15;
				boolean guiTools$displayMasked15 = !(false);
				if (guiTools$displayMasked15) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack15, this.leftPos + 17, this.topPos + 10);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack15, this.leftPos + 17, this.topPos + 10);
				}
				if (!guiTools$displayMasked15)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack15, this.leftPos + 17, this.topPos + 10);
			}
			guiTools$itemDisplay16 : {
				if (!(true))
					break guiTools$itemDisplay16;
				net.minecraft.world.item.ItemStack guiTools$displayStack16 = menu.getMenuState(3, "17", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack16 == null || guiTools$displayStack16.isEmpty())
					break guiTools$itemDisplay16;
				boolean guiTools$displayMasked16 = !(false);
				if (guiTools$displayMasked16) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack16, this.leftPos + 58, this.topPos + 10);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack16, this.leftPos + 58, this.topPos + 10);
				}
				if (!guiTools$displayMasked16)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack16, this.leftPos + 58, this.topPos + 10);
			}
			guiTools$itemDisplay17 : {
				if (!(true))
					break guiTools$itemDisplay17;
				net.minecraft.world.item.ItemStack guiTools$displayStack17 = menu.getMenuState(3, "18", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack17 == null || guiTools$displayStack17.isEmpty())
					break guiTools$itemDisplay17;
				boolean guiTools$displayMasked17 = !(false);
				if (guiTools$displayMasked17) {
					com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
					try {
						guiGraphics.renderFakeItem(guiTools$displayStack17, this.leftPos + 99, this.topPos + 10);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack17, this.leftPos + 99, this.topPos + 10);
				}
				if (!guiTools$displayMasked17)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack17, this.leftPos + 99, this.topPos + 10);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food_wip"), -135, -98, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1"), -133, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy"), -91, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2"), -49, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy"), -133, -12, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_2"), -91, -12, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_3"), -49, -12, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_4"), -133, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_5"), -91, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_6"), -49, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7"), 10, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy"), 51, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_2"), 92, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_3"), 10, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_4"), 51, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_5"), 92, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_6"), 10, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_7"), 51, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_8"), 92, 35, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png")), e -> {
					int x = ChefsDiaryFoodTierGUIScreen.this.x;
					int y = ChefsDiaryFoodTierGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodTierGUIButtonMessage(0, x, y, z));
						ChefsDiaryFoodTierGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_base_icon);
		imagebutton_food_icon = new ImageButton(this.leftPos + 148, this.topPos + -68, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/food_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/food_icon.png")), e -> {
					int x = ChefsDiaryFoodTierGUIScreen.this.x;
					int y = ChefsDiaryFoodTierGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodTierGUIButtonMessage(1, x, y, z));
						ChefsDiaryFoodTierGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_food_icon);
		imagebutton_clients_icon = new ImageButton(this.leftPos + 149, this.topPos + -39, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/clients_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/clients_icon.png")), e -> {
					int x = ChefsDiaryFoodTierGUIScreen.this.x;
					int y = ChefsDiaryFoodTierGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodTierGUIButtonMessage(2, x, y, z));
						ChefsDiaryFoodTierGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_clients_icon);
		imagebutton_appliences_icon = new ImageButton(this.leftPos + 148, this.topPos + -11, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/appliences_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/appliences_icon.png")), e -> {
					int x = ChefsDiaryFoodTierGUIScreen.this.x;
					int y = ChefsDiaryFoodTierGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodTierGUIButtonMessage(3, x, y, z));
						ChefsDiaryFoodTierGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_appliences_icon);
		imagebutton_stats_icon = new ImageButton(this.leftPos + 147, this.topPos + 17, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/stats_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/stats_icon.png")), e -> {
					int x = ChefsDiaryFoodTierGUIScreen.this.x;
					int y = ChefsDiaryFoodTierGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodTierGUIButtonMessage(4, x, y, z));
						ChefsDiaryFoodTierGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
		imagebutton_last_page_icon = new ImageButton(this.leftPos + -141, this.topPos + 57, 16, 16,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/last_page_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/last_page_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_last_page_icon);
		imagebutton_next_page_icon = new ImageButton(this.leftPos + 108, this.topPos + 57, 16, 16,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/next_page_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/next_page_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_next_page_icon);
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