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

import net.mcreator.masterchefrestaurant.world.inventory.OpenGuideGUIMenu;
import net.mcreator.masterchefrestaurant.network.OpenGuideGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class OpenGuideGUIScreen extends AbstractContainerScreen<OpenGuideGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/open_guide_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/bookmarks.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/open_icon.png");

	public OpenGuideGUIScreen(OpenGuideGUIMenu container, Inventory inventory, Component text) {
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
		guiGraphics.blit(IMAGE_2, this.leftPos + -30, this.topPos + -107, 0, 0, 15, 17, 15, 17);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_overview_wip"), -145, -103, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_every_chef_needs_a_proper_tool"), -145, -89, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_tool"), -145, -77, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_the_golden_spatula_will"), -144, -64, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_be_your_best_friend"), -144, -52, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_it_lets_you_create"), -144, -38, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_manage_your_restaurant"), -143, -25, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_as_well_as_claim_area"), -143, -12, -65485, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_check_basic_info_and"), -143, 0, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_prepare_place_for_customers"), -143, 13, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_keep_it_close_without_it"), -143, 25, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_your_restaurant_is_useless_as"), -143, 38, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_as_menu_with_no_food"), -143, 52, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_after_that_you_can_add"), 1, -89, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_3_more_but_it_is"), 1, -77, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_up_to_you"), 1, -63, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_inside_this_area_you_can"), 0, -38, -65485, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_place_all_your_fuctional"), 0, -25, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_blocks"), 0, -12, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_with_level_you_will_gain"), -1, 1, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_more_space_to_claim_for"), -1, 14, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_is_up_to_you"), 1, -50, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_faster_so_you_do_not"), -2, 26, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.open_guide_gui.label_have_to_sit_there_all_the"), -1, 38, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png")), e -> {
					int x = OpenGuideGUIScreen.this.x;
					int y = OpenGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new OpenGuideGUIButtonMessage(0, x, y, z));
						OpenGuideGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
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
					int x = OpenGuideGUIScreen.this.x;
					int y = OpenGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new OpenGuideGUIButtonMessage(1, x, y, z));
						OpenGuideGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
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
					int x = OpenGuideGUIScreen.this.x;
					int y = OpenGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new OpenGuideGUIButtonMessage(2, x, y, z));
						OpenGuideGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
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
					int x = OpenGuideGUIScreen.this.x;
					int y = OpenGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new OpenGuideGUIButtonMessage(3, x, y, z));
						OpenGuideGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
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
					int x = OpenGuideGUIScreen.this.x;
					int y = OpenGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new OpenGuideGUIButtonMessage(4, x, y, z));
						OpenGuideGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
	}
}