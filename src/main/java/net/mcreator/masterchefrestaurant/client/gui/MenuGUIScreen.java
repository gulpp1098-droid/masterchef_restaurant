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
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + -34, this.topPos + -23, 0, 0, 203, 176, 203, 176);
		guiGraphics.blit(IMAGE_1, this.leftPos + 22, this.topPos + -12, 0, 0, 93, 21, 93, 21);
		guiGraphics.blit(IMAGE_2, this.leftPos + -18, this.topPos + 27, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_3, this.leftPos + -18, this.topPos + 63, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_4, this.leftPos + -18, this.topPos + 99, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_5, this.leftPos + 3, this.topPos + 28, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_6, this.leftPos + 4, this.topPos + 63, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_7, this.leftPos + 4, this.topPos + 100, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_8, this.leftPos + 71, this.topPos + 27, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_9, this.leftPos + 71, this.topPos + 63, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_10, this.leftPos + 71, this.topPos + 99, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_11, this.leftPos + 95, this.topPos + 28, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_12, this.leftPos + 95, this.topPos + 64, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_13, this.leftPos + 95, this.topPos + 100, 0, 0, 15, 17, 15, 17);
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
		guiGraphics.drawString(this.font, Food1NameReturnProcedure.execute(entity), -18, 15, -12829636, false);
		guiGraphics.drawString(this.font, Food2NameReturnProcedure.execute(entity), -18, 51, -12829636, false);
		guiGraphics.drawString(this.font, Food3NameReturnProcedure.execute(entity), -18, 87, -12829636, false);
		guiGraphics.drawString(this.font, Food1RewardReturnProcedure.execute(world, entity), 18, 32, -12829636, false);
		guiGraphics.drawString(this.font, Food2RewardReturnProcedure.execute(world, entity), 18, 67, -12829636, false);
		guiGraphics.drawString(this.font, Food3RewardReturnProcedure.execute(world, entity), 18, 105, -12829636, false);
		guiGraphics.drawString(this.font, PageReturnProcedure.execute(entity), 50, 125, -12829636, false);
		guiGraphics.drawString(this.font, Food4NameReturnProcedure.execute(entity), 71, 16, -12829636, false);
		guiGraphics.drawString(this.font, Food5NameReturnProcedure.execute(entity), 71, 51, -12829636, false);
		guiGraphics.drawString(this.font, Food6NameReturnProcedure.execute(entity), 71, 87, -12829636, false);
		guiGraphics.drawString(this.font, Food4RewardReturnProcedure.execute(world, entity), 110, 32, -12829636, false);
		guiGraphics.drawString(this.font, Food5RewardReturnProcedure.execute(world, entity), 110, 67, -12829636, false);
		guiGraphics.drawString(this.font, Food6RewardReturnProcedure.execute(world, entity), 110, 105, -12829636, false);
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
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
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
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_last_page_icon);
	}
}