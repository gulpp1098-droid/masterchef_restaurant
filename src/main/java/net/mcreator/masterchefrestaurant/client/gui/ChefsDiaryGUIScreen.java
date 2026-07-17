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

import net.mcreator.masterchefrestaurant.world.inventory.ChefsDiaryGUIMenu;
import net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryGUIScreen extends AbstractContainerScreen<ChefsDiaryGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private ImageButton imagebutton_setarea_button;
	private ImageButton imagebutton_requirements_button;
	private ImageButton imagebutton_open_button;
	private ImageButton imagebutton_serve_button;
	private ImageButton imagebutton_expcoins_button;
	private ImageButton imagebutton_spatula_button;
	private ImageButton imagebutton_setup_button;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefs_diary_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/bookmarks.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("masterchef_restaurant:textures/screens/restaurant_icon.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("masterchef_restaurant:textures/screens/location_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("masterchef_restaurant:textures/screens/requirements_icon.png");
	private static final ResourceLocation IMAGE_6 = ResourceLocation.parse("masterchef_restaurant:textures/screens/open_icon.png");
	private static final ResourceLocation IMAGE_7 = ResourceLocation.parse("masterchef_restaurant:textures/screens/serve_icon.png");
	private static final ResourceLocation IMAGE_8 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_9 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_10 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_11 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatula_icon.png");
	private static final ResourceLocation IMAGE_12 = ResourceLocation.parse("masterchef_restaurant:textures/screens/hammer_icon.png");

	public ChefsDiaryGUIScreen(ChefsDiaryGUIMenu container, Inventory inventory, Component text) {
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
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
		guiGraphics.blit(IMAGE_1, this.leftPos + 141, this.topPos + -101, 0, 0, 35, 140, 35, 140);
		guiGraphics.blit(IMAGE_2, this.leftPos + -146, this.topPos + -90, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_3, this.leftPos + -145, this.topPos + -81, 0, 0, 39, 32, 39, 32);
		guiGraphics.blit(IMAGE_4, this.leftPos + 10, this.topPos + -36, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_5, this.leftPos + 10, this.topPos + -55, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_6, this.leftPos + 10, this.topPos + 13, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_7, this.leftPos + 10, this.topPos + 34, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_8, this.leftPos + 10, this.topPos + 54, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_9, this.leftPos + -146, this.topPos + 58, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_10, this.leftPos + 0, this.topPos + -90, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_11, this.leftPos + 10, this.topPos + -76, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_12, this.leftPos + 10, this.topPos + -10, 0, 0, 16, 16, 16, 16);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_overview_wip"), -145, -103, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_welcome_chef"), -102, -79, -3407872, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_this_diary_will_guide"), -102, -69, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_will_guild_you_through"), -102, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_the_basics_of_running"), -148, -49, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_your_own_restaurant"), -148, -40, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_your_goal_is_to_build"), -148, -30, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_manage_and_improve"), -148, -21, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_a_restaurant_of_your_dream"), -148, -12, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_a_successful_restaurant"), -148, -2, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_needs_more_than_good"), -148, 8, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_you_will_need_a_proper_equipment"), -148, 18, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_equipment_place_and_enough"), -148, 27, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_enough_patience_to_handle"), -148, 37, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_hungry_customers"), -148, 46, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_basic_tutorial"), 4, -101, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(0, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_base_icon);
		imagebutton_food_icon = new ImageButton(this.leftPos + 148, this.topPos + -68, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/food_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/food_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(1, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_food_icon);
		imagebutton_clients_icon = new ImageButton(this.leftPos + 149, this.topPos + -39, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/clients_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/clients_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(2, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_clients_icon);
		imagebutton_appliences_icon = new ImageButton(this.leftPos + 148, this.topPos + -11, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/appliences_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/appliences_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(3, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_appliences_icon);
		imagebutton_stats_icon = new ImageButton(this.leftPos + 147, this.topPos + 17, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/stats_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/stats_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(4, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
		imagebutton_setarea_button = new ImageButton(this.leftPos + 29, this.topPos + -31, 102, 12,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/setarea_button.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/setarea_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(5, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_setarea_button);
		imagebutton_requirements_button = new ImageButton(this.leftPos + 31, this.topPos + -51, 80, 12,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/requirements_button.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/requirements_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(6, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 6, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_requirements_button);
		imagebutton_open_button = new ImageButton(this.leftPos + 29, this.topPos + 17, 87, 11,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/open_button.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/open_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(7, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 7, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_open_button);
		imagebutton_serve_button = new ImageButton(this.leftPos + 29, this.topPos + 38, 87, 11,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/serve_button.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/serve_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(8, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 8, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_serve_button);
		imagebutton_expcoins_button = new ImageButton(this.leftPos + 29, this.topPos + 59, 68, 11,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/expcoins_button.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/expcoins_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(9, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 9, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_expcoins_button);
		imagebutton_spatula_button = new ImageButton(this.leftPos + 31, this.topPos + -71, 74, 12,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/spatula_button.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/spatula_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(10, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 10, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_spatula_button);
		imagebutton_setup_button = new ImageButton(this.leftPos + 30, this.topPos + -6, 90, 12,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/setup_button.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/setup_button.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(11, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 11, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_setup_button);
	}
}