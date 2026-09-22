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
import net.mcreator.masterchefrestaurant.procedures.*;
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
	private ImageButton imagebutton_next_page_icon;
	private ImageButton imagebutton_last_page_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/restaurant_management_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatulagui.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/ribbon_icon.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatula_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("masterchef_restaurant:textures/screens/servicetable_icon.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("masterchef_restaurant:textures/screens/localization_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("masterchef_restaurant:textures/screens/queue_rug_item.png");
	private static final ResourceLocation IMAGE_6 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatulasidegui.png");
	private static final ResourceLocation IMAGE_7 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatulasidegui.png");
	private static final ResourceLocation IMAGE_8 = ResourceLocation.parse("masterchef_restaurant:textures/screens/ribbonsmall_icon.png");
	private static final ResourceLocation IMAGE_9 = ResourceLocation.parse("masterchef_restaurant:textures/screens/ribbonsmall_icon.png");
	private static final ResourceLocation IMAGE_10 = ResourceLocation.parse("masterchef_restaurant:textures/screens/facehappy_icon.png");
	private static final ResourceLocation IMAGE_11 = ResourceLocation.parse("masterchef_restaurant:textures/screens/facemedium_icon.png");
	private static final ResourceLocation IMAGE_12 = ResourceLocation.parse("masterchef_restaurant:textures/screens/faceangry_icon.png");
	private static final ResourceLocation IMAGE_13 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_14 = ResourceLocation.parse("masterchef_restaurant:textures/screens/reputation_icon.png");
	private static final ResourceLocation IMAGE_15 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_16 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_17 = ResourceLocation.parse("masterchef_restaurant:textures/screens/itemslot_icon.png");
	private static final ResourceLocation IMAGE_18 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_19 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_20 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
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
		guiTools$itemDisplayTooltips : {
			guiTools$itemDisplayTooltip0 : {
				if (!(true))
					break guiTools$itemDisplayTooltip0;
				if (mouseX < this.leftPos + 189 || mouseX >= this.leftPos + 205 || mouseY < this.topPos + 28 || mouseY >= this.topPos + 44)
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
				if (mouseX < this.leftPos + 189 || mouseX >= this.leftPos + 205 || mouseY < this.topPos + 65 || mouseY >= this.topPos + 81)
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
				if (mouseX < this.leftPos + 189 || mouseX >= this.leftPos + 205 || mouseY < this.topPos + 104 || mouseY >= this.topPos + 120)
					break guiTools$itemDisplayTooltip2;
				boolean guiTools$displayMasked2 = false;
				if (guiTools$displayMasked2)
					break guiTools$itemDisplayTooltip2;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack2 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack2 == null || guiTools$tooltipStack2.isEmpty())
					break guiTools$itemDisplayTooltip2;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack2, mouseX, mouseY);
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
			guiTools$alphaBlit(guiGraphics, SPRITE_0, this.leftPos + -8, this.topPos + 81, 0, Mth.clamp((int) OpenCloseReturnProcedure.execute(entity) * 39, 0, 39), 57, 39, 57, 78);
			guiTools$alphaBlit(guiGraphics, IMAGE_1, this.leftPos + 22, this.topPos + -12, 0, 0, 93, 21, 93, 21);
			guiTools$alphaBlit(guiGraphics, IMAGE_2, this.leftPos + 31, this.topPos + -13, 0, 0, 16, 16, 16, 16);
			guiTools$alphaBlit(guiGraphics, IMAGE_3, this.leftPos + -10, this.topPos + 121, 0, 0, 16, 16, 16, 16);
			guiTools$alphaBlit(guiGraphics, IMAGE_4, this.leftPos + 94, this.topPos + 121, 0, 0, 16, 16, 16, 16);
			guiTools$alphaBlit(guiGraphics, IMAGE_5, this.leftPos + 42, this.topPos + 121, 0, 0, 16, 16, 16, 16);
			guiTools$alphaBlit(guiGraphics, IMAGE_6, this.leftPos + -131, this.topPos + -23, 0, 0, 91, 176, 91, 176);
			guiTools$alphaBlit(guiGraphics, IMAGE_7, this.leftPos + 175, this.topPos + -23, 0, 0, 91, 176, 91, 176);
			guiTools$alphaBlit(guiGraphics, IMAGE_8, this.leftPos + -118, this.topPos + -12, 0, 0, 65, 20, 65, 20);
			guiTools$alphaBlit(guiGraphics, IMAGE_9, this.leftPos + 188, this.topPos + -12, 0, 0, 65, 20, 65, 20);
			guiTools$alphaBlit(guiGraphics, IMAGE_10, this.leftPos + -106, this.topPos + 24, 0, 0, 11, 11, 11, 11);
			guiTools$alphaBlit(guiGraphics, IMAGE_11, this.leftPos + -106, this.topPos + 49, 0, 0, 11, 11, 11, 11);
			guiTools$alphaBlit(guiGraphics, IMAGE_12, this.leftPos + -106, this.topPos + 75, 0, 0, 11, 11, 11, 11);
			guiTools$alphaBlit(guiGraphics, IMAGE_13, this.leftPos + -108, this.topPos + 98, 0, 0, 15, 17, 15, 17);
			guiTools$alphaBlit(guiGraphics, IMAGE_14, this.leftPos + -106, this.topPos + 128, 0, 0, 11, 11, 11, 11);
			guiTools$alphaBlit(guiGraphics, IMAGE_15, this.leftPos + 188, this.topPos + 27, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_16, this.leftPos + 188, this.topPos + 64, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_17, this.leftPos + 188, this.topPos + 103, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_18, this.leftPos + 215, this.topPos + 27, 0, 0, 15, 17, 15, 17);
			guiTools$alphaBlit(guiGraphics, IMAGE_19, this.leftPos + 215, this.topPos + 64, 0, 0, 15, 17, 15, 17);
			guiTools$alphaBlit(guiGraphics, IMAGE_20, this.leftPos + 215, this.topPos + 103, 0, 0, 15, 17, 15, 17);
			if (this.enhanced_image_button_button_icon != null && this.enhanced_image_button_button_icon.visible) {
				this.enhanced_image_button_button_icon.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
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
						guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + 189, this.topPos + 28);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + 189, this.topPos + 28);
				}
				if (!guiTools$displayMasked0)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack0, this.leftPos + 189, this.topPos + 28);
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
						guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + 189, this.topPos + 65);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + 189, this.topPos + 65);
				}
				if (!guiTools$displayMasked1)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack1, this.leftPos + 189, this.topPos + 65);
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
						guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 189, this.topPos + 104);
					} finally {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
					}
				} else {
					guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 189, this.topPos + 104);
				}
				if (!guiTools$displayMasked2)
					guiGraphics.renderItemDecorations(font, guiTools$displayStack2, this.leftPos + 189, this.topPos + 104);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_management"), 47, -9, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_create_edit_restaurant"), 26, 24, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_set_location_for_restaurant"), -5, 56, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_open_close"), 79, 94, -16777216, false);
		guiGraphics.drawString(this.font, MaxTablesReturnProcedure.execute(entity), 6, 126, -12829636, false);
		guiGraphics.drawString(this.font, MaxQueueReturnProcedure.execute(entity), 60, 126, -12829636, false);
		guiGraphics.drawString(this.font, MaxLocationsReturnProcedure.execute(entity), 110, 126, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_stats"), -109, -8, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_fully"), -118, 13, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_part_served"), -115, 38, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_not_served"), -111, 64, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_coins_earned"), -117, 90, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_reputation"), -112, 116, -12829636, false);
		guiGraphics.drawString(this.font, FullyServedReturnProcedure.execute(entity), -83, 25, -12829636, false);
		guiGraphics.drawString(this.font, PartServedReturnProcedure.execute(entity), -83, 51, -12829636, false);
		guiGraphics.drawString(this.font, NotServedReturnProcedure.execute(entity), -83, 77, -12829636, false);
		guiGraphics.drawString(this.font, CoinsEarnedReturnProcedure.execute(entity), -83, 104, -12829636, false);
		guiGraphics.drawString(this.font, ReputationReturnProcedure.execute(entity), -83, 130, -12829636, false);
		guiGraphics.drawString(this.font, Food1NameReturnProcedure.execute(entity), 188, 15, -12829636, false);
		guiGraphics.drawString(this.font, Food2NameReturnProcedure.execute(entity), 188, 51, -12829636, false);
		guiGraphics.drawString(this.font, Food3NameReturnProcedure.execute(entity), 188, 89, -12829636, false);
		guiGraphics.drawString(this.font, Food1RewardReturnProcedure.execute(world, entity), 231, 32, -12829636, false);
		guiGraphics.drawString(this.font, Food2RewardReturnProcedure.execute(world, entity), 231, 69, -12829636, false);
		guiGraphics.drawString(this.font, Food3RewardReturnProcedure.execute(world, entity), 231, 108, -12829636, false);
		guiGraphics.drawString(this.font, PageReturnProcedure.execute(entity), 217, 129, -12829636, false);
		this.guiTools$renderMultilineLabel(guiGraphics, java.util.Objects.toString(net.mcreator.masterchefrestaurant.procedures.MenuNameReturnProcedure.execute(entity), ""), 204, -12, 64, 43, -1, false, 0.75F, 0);
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
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
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
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
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
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_button_icon2);
		imagebutton_next_page_icon = new ImageButton(this.leftPos + 239, this.topPos + 125, 16, 16,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/next_page_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/next_page_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new RestaurantManagementGUIButtonMessage(3, x, y, z));
						RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_next_page_icon);
		imagebutton_last_page_icon = new ImageButton(this.leftPos + 186, this.topPos + 125, 16, 16,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/last_page_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/last_page_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new RestaurantManagementGUIButtonMessage(4, x, y, z));
						RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_last_page_icon);
		enhanced_image_button_button_icon = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 182, this.topPos + 157, 78, 21, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/button_icon.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/button_icon.png")), e -> {
					int x = RestaurantManagementGUIScreen.this.x;
					int y = RestaurantManagementGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.masterchefrestaurant.network.RestaurantManagementGUIButtonMessage(5, x, y, z));
						net.mcreator.masterchefrestaurant.network.RestaurantManagementGUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/button_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Next day menu";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_button_icon);
	}

	private final java.util.Map<String, java.util.List<String>> guiTools$multilineCache = new java.util.HashMap<>();

	private void guiTools$renderMultilineLabel(GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int boxHeight, int color, boolean shadow, float scale, int overflowMode) {
		if (text == null || scale <= 0.0F || boxWidth <= 0 || boxHeight <= 0)
			return;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int contentHeight = Math.max(0, (int) Math.floor(boxHeight / scale));
		int lineStep = this.font.lineHeight + 1;
		int maxLines = contentHeight < this.font.lineHeight ? 0 : 1 + (contentHeight - this.font.lineHeight) / lineStep;
		String cacheKey = text + "\u0000" + wrapWidth + "\u0000" + maxLines + "\u0000" + overflowMode;
		java.util.List<String> lines = this.guiTools$multilineCache.computeIfAbsent(cacheKey, key -> this.guiTools$displayMultilineText(text, wrapWidth, maxLines, overflowMode));
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
			for (String line : lines) {
				guiGraphics.drawString(this.font, line, 0, currentY, color, shadow);
				currentY += lineStep;
			}
		} finally {
			guiGraphics.pose().popPose();
			if (clip)
				guiGraphics.disableScissor();
		}
	}

	private boolean guiTools$isMultilineTruncated(String text, int boxWidth, int boxHeight, float scale, int overflowMode) {
		if (text == null || overflowMode == 0 || scale <= 0.0F)
			return false;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int contentHeight = Math.max(0, (int) Math.floor(boxHeight / scale));
		java.util.List<String> lines = this.guiTools$wrapMultilineText(text, wrapWidth);
		for (String line : lines)
			if (this.font.width(line) > wrapWidth)
				return true;
		return !lines.isEmpty() && this.font.lineHeight + (lines.size() - 1) * (this.font.lineHeight + 1) > contentHeight;
	}

	private java.util.List<String> guiTools$displayMultilineText(String text, int wrapWidth, int maxLines, int overflowMode) {
		java.util.List<String> wrapped = this.guiTools$wrapMultilineText(text, wrapWidth);
		if (overflowMode != 2)
			return wrapped;
		if (maxLines <= 0)
			return java.util.List.of();
		boolean verticalOverflow = wrapped.size() > maxLines;
		java.util.List<String> result = new java.util.ArrayList<>();
		for (int index = 0; index < Math.min(maxLines, wrapped.size()); index++) {
			result.add(this.guiTools$ellipsize(wrapped.get(index), wrapWidth, verticalOverflow && index == maxLines - 1));
		}
		return java.util.List.copyOf(result);
	}

	private String guiTools$ellipsize(String value, int maxWidth, boolean forceEllipsis) {
		if (!forceEllipsis && this.font.width(value) <= maxWidth)
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
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_button_icon;

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
			return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("masterchef_restaurant", "textures/screens/" + texture);
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