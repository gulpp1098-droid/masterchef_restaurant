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
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + -1, this.topPos + 0, 0, 0, 176, 234, 176, 234);
		guiGraphics.blit(IMAGE_1, this.leftPos + 143, this.topPos + 43, 0, 0, 22, 44, 22, 44);
		guiGraphics.blit(IMAGE_2, this.leftPos + 120, this.topPos + 43, 0, 0, 22, 44, 22, 44);
		guiGraphics.blit(IMAGE_3, this.leftPos + 97, this.topPos + 43, 0, 0, 22, 44, 22, 44);
		guiGraphics.blit(IMAGE_4, this.leftPos + 74, this.topPos + 43, 0, 0, 22, 44, 22, 44);
		guiGraphics.blit(IMAGE_5, this.leftPos + 51, this.topPos + 43, 0, 0, 22, 44, 22, 44);
		guiGraphics.blit(IMAGE_6, this.leftPos + 42, this.topPos + 9, 0, 0, 93, 21, 93, 21);
		guiGraphics.blit(IMAGE_7, this.leftPos + 23, this.topPos + 30, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_8, this.leftPos + 37, this.topPos + 92, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_9, this.leftPos + 39, this.topPos + 98, 0, 0, 14, 8, 14, 8);
		guiGraphics.blit(IMAGE_10, this.leftPos + 19, this.topPos + 44, 0, 0, 20, 27, 20, 27);
		if (OrdersCheckboxesCheckSlot1Procedure.execute(world, entity)) {
			guiGraphics.blit(IMAGE_11, this.leftPos + 54, this.topPos + 67, 0, 0, 16, 16, 16, 16);
		}
		if (OrdersCheckboxesCheckSlot2Procedure.execute(entity)) {
			guiGraphics.blit(IMAGE_12, this.leftPos + 77, this.topPos + 67, 0, 0, 16, 16, 16, 16);
		}
		if (OrdersCheckboxesCheckSlot3Procedure.execute(entity)) {
			guiGraphics.blit(IMAGE_13, this.leftPos + 100, this.topPos + 67, 0, 0, 16, 16, 16, 16);
		}
		if (OrdersCheckboxesCheckSlot4Procedure.execute(entity)) {
			guiGraphics.blit(IMAGE_14, this.leftPos + 123, this.topPos + 67, 0, 0, 16, 16, 16, 16);
		}
		if (OrdersCheckboxesCheckSlot5Procedure.execute(entity)) {
			guiGraphics.blit(IMAGE_15, this.leftPos + 146, this.topPos + 67, 0, 0, 16, 16, 16, 16);
		}
		guiGraphics.blit(SPRITE_0, this.leftPos + 23, this.topPos + 75, 0, Mth.clamp((int) PatianceReturnProcedure.execute(entity) * 11, 0, 33), 11, 11, 11, 44);
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
}