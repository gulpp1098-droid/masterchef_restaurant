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
		guiGraphics.blit(IMAGE_6, this.leftPos + -131, this.topPos + -23, 0, 0, 91, 176, 91, 176);
		guiGraphics.blit(IMAGE_7, this.leftPos + 175, this.topPos + -23, 0, 0, 91, 176, 91, 176);
		guiGraphics.blit(IMAGE_8, this.leftPos + -118, this.topPos + -12, 0, 0, 65, 20, 65, 20);
		guiGraphics.blit(IMAGE_9, this.leftPos + 188, this.topPos + -12, 0, 0, 65, 20, 65, 20);
		guiGraphics.blit(IMAGE_10, this.leftPos + -106, this.topPos + 24, 0, 0, 11, 11, 11, 11);
		guiGraphics.blit(IMAGE_11, this.leftPos + -106, this.topPos + 49, 0, 0, 11, 11, 11, 11);
		guiGraphics.blit(IMAGE_12, this.leftPos + -106, this.topPos + 75, 0, 0, 11, 11, 11, 11);
		guiGraphics.blit(IMAGE_13, this.leftPos + -108, this.topPos + 98, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_14, this.leftPos + -106, this.topPos + 128, 0, 0, 11, 11, 11, 11);
		guiGraphics.blit(IMAGE_15, this.leftPos + 188, this.topPos + 27, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_16, this.leftPos + 188, this.topPos + 64, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_17, this.leftPos + 188, this.topPos + 103, 0, 0, 18, 18, 18, 18);
		guiGraphics.blit(IMAGE_18, this.leftPos + 215, this.topPos + 27, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_19, this.leftPos + 215, this.topPos + 64, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_20, this.leftPos + 215, this.topPos + 103, 0, 0, 15, 17, 15, 17);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.restaurant_management_gui.label_stats"), -109, -8, -1, false);
		guiGraphics.drawString(this.font, MenuNameReturnProcedure.execute(entity), 198, -8, -1, false);
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
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
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
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
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
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
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
		this.addRenderableWidget(enhanced_image_button_button_icon);
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
}