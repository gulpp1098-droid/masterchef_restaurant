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

import net.mcreator.masterchefrestaurant.world.inventory.BlocksGuideGUIMenu;
import net.mcreator.masterchefrestaurant.network.BlocksGuideGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class BlocksGuideGUIScreen extends AbstractContainerScreen<BlocksGuideGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private ImageButton imagebutton_next_page_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/blocks_guide_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/bookmarks.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/requirements_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("masterchef_restaurant:textures/screens/reception_item.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("masterchef_restaurant:textures/screens/servicetable_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("masterchef_restaurant:textures/screens/chair_icon.png");
	private static final ResourceLocation IMAGE_6 = ResourceLocation.parse("masterchef_restaurant:textures/screens/queue_rug_item.png");
	private static final ResourceLocation IMAGE_7 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_8 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");

	public BlocksGuideGUIScreen(BlocksGuideGUIMenu container, Inventory inventory, Component text) {
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
		guiGraphics.blit(IMAGE_2, this.leftPos + -31, this.topPos + -106, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_3, this.leftPos + -136, this.topPos + 42, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_4, this.leftPos + -107, this.topPos + 43, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_5, this.leftPos + -78, this.topPos + 43, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_6, this.leftPos + -49, this.topPos + 43, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_7, this.leftPos + 1, this.topPos + -49, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_8, this.leftPos + 1, this.topPos + 39, 0, 0, 133, 11, 133, 11);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_overview_wip"), -145, -103, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_before_customers_can_visit"), -145, -88, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_visit_your_restaurant"), -145, -76, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_needs_a_few_basic_blocks"), -145, -62, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_each_block_has_a_job"), -145, -47, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_at_start_prepare"), -145, -33, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_reception"), -145, -17, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_service_table"), -145, -5, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_chair"), -144, 8, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_queue_rug_optional"), -144, 21, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_reception1"), 5, -102, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_restaurant_begins_from_this_plac"), 5, -88, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_this_place"), 5, -75, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_gui.label_will_wait_in_queue_here"), 6, -62, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png")), e -> {
					int x = BlocksGuideGUIScreen.this.x;
					int y = BlocksGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideGUIButtonMessage(0, x, y, z));
						BlocksGuideGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
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
					int x = BlocksGuideGUIScreen.this.x;
					int y = BlocksGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideGUIButtonMessage(1, x, y, z));
						BlocksGuideGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
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
					int x = BlocksGuideGUIScreen.this.x;
					int y = BlocksGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideGUIButtonMessage(2, x, y, z));
						BlocksGuideGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
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
					int x = BlocksGuideGUIScreen.this.x;
					int y = BlocksGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideGUIButtonMessage(3, x, y, z));
						BlocksGuideGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
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
					int x = BlocksGuideGUIScreen.this.x;
					int y = BlocksGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideGUIButtonMessage(4, x, y, z));
						BlocksGuideGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
		imagebutton_next_page_icon = new ImageButton(this.leftPos + 108, this.topPos + 57, 16, 16,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/next_page_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/next_page_icon.png")), e -> {
					int x = BlocksGuideGUIScreen.this.x;
					int y = BlocksGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideGUIButtonMessage(5, x, y, z));
						BlocksGuideGUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_next_page_icon);
	}
}