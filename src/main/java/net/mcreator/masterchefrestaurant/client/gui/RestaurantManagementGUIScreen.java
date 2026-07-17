package net.mcreator.masterchefrestaurant.client.gui;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.masterchefrestaurant.world.inventory.RestaurantManagementGUIMenu;
import net.mcreator.masterchefrestaurant.procedures.OpenCloseReturnProcedure;
import net.mcreator.masterchefrestaurant.procedures.MaxTablesReturnProcedure;
import net.mcreator.masterchefrestaurant.procedures.MaxQueueReturnProcedure;
import net.mcreator.masterchefrestaurant.procedures.MaxLocationsReturnProcedure;
import net.mcreator.masterchefrestaurant.network.RestaurantManagementGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class RestaurantManagementGUIScreen extends AbstractContainerScreen<RestaurantManagementGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_button_icon;
	private ImageButton imagebutton_button_icon1;
	private ImageButton imagebutton_button_icon2;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/restaurant_management_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatulagui.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/ribbon_icon.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatula_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("masterchef_restaurant:textures/screens/servicetable_icon.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("masterchef_restaurant:textures/screens/localization_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("masterchef_restaurant:textures/screens/queue_rug_item.png");
	private static final ResourceLocation SPRITE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/signopenclose_sprite.png");

	public RestaurantManagementGUIScreen(RestaurantManagementGUIMenu container, Inventory inventory, Component text) {
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
		guiGraphics.blit(IMAGE_2, this.leftPos + 31, this.topPos + -13, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_3, this.leftPos + -10, this.topPos + 121, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_4, this.leftPos + 94, this.topPos + 121, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_5, this.leftPos + 42, this.topPos + 121, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(SPRITE_0, this.leftPos + -3, this.topPos + 78, 0, Mth.clamp((int) OpenCloseReturnProcedure.execute(entity) * 39, 0, 39), 57, 39, 57, 78);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_management"), 47, -9, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_create_edit_restaurant"), 4, 24, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_set_location_for_restaurant"), -5, 56, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_open_close"), 79, 94, -16777216, false);
		guiGraphics.drawString(this.font, MaxTablesReturnProcedure.execute(entity), 6, 126, -12829636, false);
		guiGraphics.drawString(this.font, MaxQueueReturnProcedure.execute(entity), 60, 126, -12829636, false);
		guiGraphics.drawString(this.font, MaxLocationsReturnProcedure.execute(entity), 110, 126, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_button_icon = new ImageButton(this.leftPos + -15, this.topPos + 12, 165, 31,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/button_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/button_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new RestaurantManagementGUIButtonMessage(0, x, y, z));
						RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_button_icon);
		imagebutton_button_icon1 = new ImageButton(this.leftPos + -15, this.topPos + 45, 165, 31,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/button_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/button_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new RestaurantManagementGUIButtonMessage(1, x, y, z));
						RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_button_icon1);
		imagebutton_button_icon2 = new ImageButton(this.leftPos + 69, this.topPos + 83, 81, 31,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/openclosebutton_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/openclosebutton_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new RestaurantManagementGUIButtonMessage(2, x, y, z));
						RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_button_icon2);
	}
}