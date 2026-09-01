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

import net.mcreator.masterchefrestaurant.world.inventory.BlocksGuideP3GUIMenu;
import net.mcreator.masterchefrestaurant.network.BlocksGuideP3GUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class BlocksGuideP3GUIScreen extends AbstractContainerScreen<BlocksGuideP3GUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
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
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/blocks_guide_p_3_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/bookmarks.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");

	public BlocksGuideP3GUIScreen(BlocksGuideP3GUIMenu container, Inventory inventory, Component text) {
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
		guiGraphics.blit(IMAGE_2, this.leftPos + -146, this.topPos + -49, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_3, this.leftPos + -145, this.topPos + 39, 0, 0, 133, 11, 133, 11);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_p_3_gui.label_service_table"), -144, -101, -12829636, false);
<<<<<<< Updated upstream
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_p_3_gui.label_customers_will_gladly"), -144, -88, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_p_3_gui.label_come_and_eat_food"), -144, -75, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.blocks_guide_p_3_gui.label_just_add_some_chair_as_well"), -144, -61, -12829636, false);
=======
>>>>>>> Stashed changes
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png")), e -> {
					int x = BlocksGuideP3GUIScreen.this.x;
					int y = BlocksGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideP3GUIButtonMessage(0, x, y, z));
						BlocksGuideP3GUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
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
					int x = BlocksGuideP3GUIScreen.this.x;
					int y = BlocksGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideP3GUIButtonMessage(1, x, y, z));
						BlocksGuideP3GUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
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
					int x = BlocksGuideP3GUIScreen.this.x;
					int y = BlocksGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideP3GUIButtonMessage(2, x, y, z));
						BlocksGuideP3GUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
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
					int x = BlocksGuideP3GUIScreen.this.x;
					int y = BlocksGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideP3GUIButtonMessage(3, x, y, z));
						BlocksGuideP3GUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
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
					int x = BlocksGuideP3GUIScreen.this.x;
					int y = BlocksGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideP3GUIButtonMessage(4, x, y, z));
						BlocksGuideP3GUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
		imagebutton_last_page_icon = new ImageButton(this.leftPos + -134, this.topPos + 56, 16, 16,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/last_page_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/last_page_icon.png")), e -> {
					int x = BlocksGuideP3GUIScreen.this.x;
					int y = BlocksGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new BlocksGuideP3GUIButtonMessage(5, x, y, z));
						BlocksGuideP3GUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
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