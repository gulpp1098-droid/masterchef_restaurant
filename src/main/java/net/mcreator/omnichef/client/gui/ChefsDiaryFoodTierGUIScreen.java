package net.mcreator.omnichef.client.gui;

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

import net.mcreator.omnichef.world.inventory.ChefsDiaryFoodTierGUIMenu;
import net.mcreator.omnichef.network.ChefsDiaryFoodTierGUIButtonMessage;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryFoodTierGUIScreen extends AbstractContainerScreen<ChefsDiaryFoodTierGUIMenu> implements OmnichefModScreens.ScreenAccessor {
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
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/chefs_diary_food_tier_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("omnichef:textures/screens/bookmarks.png");

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
				if (mouseX < this.leftPos + -44 || mouseX >= this.leftPos + -28 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
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
			guiTools$itemDisplayTooltip18 : {
				if (!(true))
					break guiTools$itemDisplayTooltip18;
				if (mouseX < this.leftPos + -128 || mouseX >= this.leftPos + -112 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip18;
				boolean guiTools$displayMasked18 = false;
				if (guiTools$displayMasked18)
					break guiTools$itemDisplayTooltip18;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack18 = menu.getMenuState(3, "19", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack18 == null || guiTools$tooltipStack18.isEmpty())
					break guiTools$itemDisplayTooltip18;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack18, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip19 : {
				if (!(true))
					break guiTools$itemDisplayTooltip19;
				if (mouseX < this.leftPos + -86 || mouseX >= this.leftPos + -70 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip19;
				boolean guiTools$displayMasked19 = false;
				if (guiTools$displayMasked19)
					break guiTools$itemDisplayTooltip19;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack19 = menu.getMenuState(3, "20", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack19 == null || guiTools$tooltipStack19.isEmpty())
					break guiTools$itemDisplayTooltip19;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack19, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip20 : {
				if (!(true))
					break guiTools$itemDisplayTooltip20;
				if (mouseX < this.leftPos + -44 || mouseX >= this.leftPos + -28 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip20;
				boolean guiTools$displayMasked20 = false;
				if (guiTools$displayMasked20)
					break guiTools$itemDisplayTooltip20;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack20 = menu.getMenuState(3, "21", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack20 == null || guiTools$tooltipStack20.isEmpty())
					break guiTools$itemDisplayTooltip20;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack20, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip21 : {
				if (!(true))
					break guiTools$itemDisplayTooltip21;
				if (mouseX < this.leftPos + -128 || mouseX >= this.leftPos + -112 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip21;
				boolean guiTools$displayMasked21 = false;
				if (guiTools$displayMasked21)
					break guiTools$itemDisplayTooltip21;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack21 = menu.getMenuState(3, "22", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack21 == null || guiTools$tooltipStack21.isEmpty())
					break guiTools$itemDisplayTooltip21;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack21, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip22 : {
				if (!(true))
					break guiTools$itemDisplayTooltip22;
				if (mouseX < this.leftPos + -86 || mouseX >= this.leftPos + -70 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip22;
				boolean guiTools$displayMasked22 = false;
				if (guiTools$displayMasked22)
					break guiTools$itemDisplayTooltip22;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack22 = menu.getMenuState(3, "23", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack22 == null || guiTools$tooltipStack22.isEmpty())
					break guiTools$itemDisplayTooltip22;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack22, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip23 : {
				if (!(true))
					break guiTools$itemDisplayTooltip23;
				if (mouseX < this.leftPos + -44 || mouseX >= this.leftPos + -28 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip23;
				boolean guiTools$displayMasked23 = false;
				if (guiTools$displayMasked23)
					break guiTools$itemDisplayTooltip23;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack23 = menu.getMenuState(3, "24", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack23 == null || guiTools$tooltipStack23.isEmpty())
					break guiTools$itemDisplayTooltip23;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack23, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip24 : {
				if (!(true))
					break guiTools$itemDisplayTooltip24;
				if (mouseX < this.leftPos + -128 || mouseX >= this.leftPos + -112 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip24;
				boolean guiTools$displayMasked24 = false;
				if (guiTools$displayMasked24)
					break guiTools$itemDisplayTooltip24;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack24 = menu.getMenuState(3, "25", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack24 == null || guiTools$tooltipStack24.isEmpty())
					break guiTools$itemDisplayTooltip24;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack24, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip25 : {
				if (!(true))
					break guiTools$itemDisplayTooltip25;
				if (mouseX < this.leftPos + -86 || mouseX >= this.leftPos + -70 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip25;
				boolean guiTools$displayMasked25 = false;
				if (guiTools$displayMasked25)
					break guiTools$itemDisplayTooltip25;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack25 = menu.getMenuState(3, "26", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack25 == null || guiTools$tooltipStack25.isEmpty())
					break guiTools$itemDisplayTooltip25;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack25, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip26 : {
				if (!(true))
					break guiTools$itemDisplayTooltip26;
				if (mouseX < this.leftPos + -44 || mouseX >= this.leftPos + -28 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip26;
				boolean guiTools$displayMasked26 = false;
				if (guiTools$displayMasked26)
					break guiTools$itemDisplayTooltip26;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack26 = menu.getMenuState(3, "27", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack26 == null || guiTools$tooltipStack26.isEmpty())
					break guiTools$itemDisplayTooltip26;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack26, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip27 : {
				if (!(true))
					break guiTools$itemDisplayTooltip27;
				if (mouseX < this.leftPos + 17 || mouseX >= this.leftPos + 33 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip27;
				boolean guiTools$displayMasked27 = false;
				if (guiTools$displayMasked27)
					break guiTools$itemDisplayTooltip27;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack27 = menu.getMenuState(3, "28", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack27 == null || guiTools$tooltipStack27.isEmpty())
					break guiTools$itemDisplayTooltip27;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack27, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip28 : {
				if (!(true))
					break guiTools$itemDisplayTooltip28;
				if (mouseX < this.leftPos + 58 || mouseX >= this.leftPos + 74 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip28;
				boolean guiTools$displayMasked28 = false;
				if (guiTools$displayMasked28)
					break guiTools$itemDisplayTooltip28;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack28 = menu.getMenuState(3, "29", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack28 == null || guiTools$tooltipStack28.isEmpty())
					break guiTools$itemDisplayTooltip28;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack28, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip29 : {
				if (!(true))
					break guiTools$itemDisplayTooltip29;
				if (mouseX < this.leftPos + 99 || mouseX >= this.leftPos + 115 || mouseY < this.topPos + -80 || mouseY >= this.topPos + -64)
					break guiTools$itemDisplayTooltip29;
				boolean guiTools$displayMasked29 = false;
				if (guiTools$displayMasked29)
					break guiTools$itemDisplayTooltip29;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack29 = menu.getMenuState(3, "30", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack29 == null || guiTools$tooltipStack29.isEmpty())
					break guiTools$itemDisplayTooltip29;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack29, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip30 : {
				if (!(true))
					break guiTools$itemDisplayTooltip30;
				if (mouseX < this.leftPos + 17 || mouseX >= this.leftPos + 33 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip30;
				boolean guiTools$displayMasked30 = false;
				if (guiTools$displayMasked30)
					break guiTools$itemDisplayTooltip30;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack30 = menu.getMenuState(3, "31", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack30 == null || guiTools$tooltipStack30.isEmpty())
					break guiTools$itemDisplayTooltip30;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack30, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip31 : {
				if (!(true))
					break guiTools$itemDisplayTooltip31;
				if (mouseX < this.leftPos + 58 || mouseX >= this.leftPos + 74 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip31;
				boolean guiTools$displayMasked31 = false;
				if (guiTools$displayMasked31)
					break guiTools$itemDisplayTooltip31;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack31 = menu.getMenuState(3, "32", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack31 == null || guiTools$tooltipStack31.isEmpty())
					break guiTools$itemDisplayTooltip31;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack31, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip32 : {
				if (!(true))
					break guiTools$itemDisplayTooltip32;
				if (mouseX < this.leftPos + 99 || mouseX >= this.leftPos + 115 || mouseY < this.topPos + -37 || mouseY >= this.topPos + -21)
					break guiTools$itemDisplayTooltip32;
				boolean guiTools$displayMasked32 = false;
				if (guiTools$displayMasked32)
					break guiTools$itemDisplayTooltip32;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack32 = menu.getMenuState(3, "33", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack32 == null || guiTools$tooltipStack32.isEmpty())
					break guiTools$itemDisplayTooltip32;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack32, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip33 : {
				if (!(true))
					break guiTools$itemDisplayTooltip33;
				if (mouseX < this.leftPos + 17 || mouseX >= this.leftPos + 33 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip33;
				boolean guiTools$displayMasked33 = false;
				if (guiTools$displayMasked33)
					break guiTools$itemDisplayTooltip33;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack33 = menu.getMenuState(3, "34", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack33 == null || guiTools$tooltipStack33.isEmpty())
					break guiTools$itemDisplayTooltip33;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack33, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip34 : {
				if (!(true))
					break guiTools$itemDisplayTooltip34;
				if (mouseX < this.leftPos + 58 || mouseX >= this.leftPos + 74 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip34;
				boolean guiTools$displayMasked34 = false;
				if (guiTools$displayMasked34)
					break guiTools$itemDisplayTooltip34;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack34 = menu.getMenuState(3, "35", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack34 == null || guiTools$tooltipStack34.isEmpty())
					break guiTools$itemDisplayTooltip34;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack34, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip35 : {
				if (!(true))
					break guiTools$itemDisplayTooltip35;
				if (mouseX < this.leftPos + 99 || mouseX >= this.leftPos + 115 || mouseY < this.topPos + 10 || mouseY >= this.topPos + 26)
					break guiTools$itemDisplayTooltip35;
				boolean guiTools$displayMasked35 = false;
				if (guiTools$displayMasked35)
					break guiTools$itemDisplayTooltip35;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack35 = menu.getMenuState(3, "36", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack35 == null || guiTools$tooltipStack35.isEmpty())
					break guiTools$itemDisplayTooltip35;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack35, mouseX, mouseY);
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
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -128), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay1 : {
				if (!(true))
					break guiTools$itemDisplay1;
				net.minecraft.world.item.ItemStack guiTools$displayStack1 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack1 == null || guiTools$displayStack1.isEmpty())
					break guiTools$itemDisplay1;
				boolean guiTools$displayMasked1 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -86), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay2 : {
				if (!(true))
					break guiTools$itemDisplay2;
				net.minecraft.world.item.ItemStack guiTools$displayStack2 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack2 == null || guiTools$displayStack2.isEmpty())
					break guiTools$itemDisplay2;
				boolean guiTools$displayMasked2 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -44), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay3 : {
				if (!(true))
					break guiTools$itemDisplay3;
				net.minecraft.world.item.ItemStack guiTools$displayStack3 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack3 == null || guiTools$displayStack3.isEmpty())
					break guiTools$itemDisplay3;
				boolean guiTools$displayMasked3 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -128), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay4 : {
				if (!(true))
					break guiTools$itemDisplay4;
				net.minecraft.world.item.ItemStack guiTools$displayStack4 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack4 == null || guiTools$displayStack4.isEmpty())
					break guiTools$itemDisplay4;
				boolean guiTools$displayMasked4 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -86), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay5 : {
				if (!(true))
					break guiTools$itemDisplay5;
				net.minecraft.world.item.ItemStack guiTools$displayStack5 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack5 == null || guiTools$displayStack5.isEmpty())
					break guiTools$itemDisplay5;
				boolean guiTools$displayMasked5 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -44), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked5) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + -44, this.topPos + -37);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + -44, this.topPos + -37);
					}
					if (!guiTools$displayMasked5)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack5, this.leftPos + -44, this.topPos + -37);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay6 : {
				if (!(true))
					break guiTools$itemDisplay6;
				net.minecraft.world.item.ItemStack guiTools$displayStack6 = menu.getMenuState(3, "7", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack6 == null || guiTools$displayStack6.isEmpty())
					break guiTools$itemDisplay6;
				boolean guiTools$displayMasked6 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -128), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay7 : {
				if (!(true))
					break guiTools$itemDisplay7;
				net.minecraft.world.item.ItemStack guiTools$displayStack7 = menu.getMenuState(3, "8", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack7 == null || guiTools$displayStack7.isEmpty())
					break guiTools$itemDisplay7;
				boolean guiTools$displayMasked7 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -86), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay8 : {
				if (!(true))
					break guiTools$itemDisplay8;
				net.minecraft.world.item.ItemStack guiTools$displayStack8 = menu.getMenuState(3, "9", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack8 == null || guiTools$displayStack8.isEmpty())
					break guiTools$itemDisplay8;
				boolean guiTools$displayMasked8 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -44), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay9 : {
				if (!(true))
					break guiTools$itemDisplay9;
				net.minecraft.world.item.ItemStack guiTools$displayStack9 = menu.getMenuState(3, "10", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack9 == null || guiTools$displayStack9.isEmpty())
					break guiTools$itemDisplay9;
				boolean guiTools$displayMasked9 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 17), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay10 : {
				if (!(true))
					break guiTools$itemDisplay10;
				net.minecraft.world.item.ItemStack guiTools$displayStack10 = menu.getMenuState(3, "11", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack10 == null || guiTools$displayStack10.isEmpty())
					break guiTools$itemDisplay10;
				boolean guiTools$displayMasked10 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 58), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay11 : {
				if (!(true))
					break guiTools$itemDisplay11;
				net.minecraft.world.item.ItemStack guiTools$displayStack11 = menu.getMenuState(3, "12", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack11 == null || guiTools$displayStack11.isEmpty())
					break guiTools$itemDisplay11;
				boolean guiTools$displayMasked11 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 99), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay12 : {
				if (!(true))
					break guiTools$itemDisplay12;
				net.minecraft.world.item.ItemStack guiTools$displayStack12 = menu.getMenuState(3, "13", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack12 == null || guiTools$displayStack12.isEmpty())
					break guiTools$itemDisplay12;
				boolean guiTools$displayMasked12 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 17), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay13 : {
				if (!(true))
					break guiTools$itemDisplay13;
				net.minecraft.world.item.ItemStack guiTools$displayStack13 = menu.getMenuState(3, "14", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack13 == null || guiTools$displayStack13.isEmpty())
					break guiTools$itemDisplay13;
				boolean guiTools$displayMasked13 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 58), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay14 : {
				if (!(true))
					break guiTools$itemDisplay14;
				net.minecraft.world.item.ItemStack guiTools$displayStack14 = menu.getMenuState(3, "15", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack14 == null || guiTools$displayStack14.isEmpty())
					break guiTools$itemDisplay14;
				boolean guiTools$displayMasked14 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 99), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay15 : {
				if (!(true))
					break guiTools$itemDisplay15;
				net.minecraft.world.item.ItemStack guiTools$displayStack15 = menu.getMenuState(3, "16", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack15 == null || guiTools$displayStack15.isEmpty())
					break guiTools$itemDisplay15;
				boolean guiTools$displayMasked15 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 17), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay16 : {
				if (!(true))
					break guiTools$itemDisplay16;
				net.minecraft.world.item.ItemStack guiTools$displayStack16 = menu.getMenuState(3, "17", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack16 == null || guiTools$displayStack16.isEmpty())
					break guiTools$itemDisplay16;
				boolean guiTools$displayMasked16 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 58), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay17 : {
				if (!(true))
					break guiTools$itemDisplay17;
				net.minecraft.world.item.ItemStack guiTools$displayStack17 = menu.getMenuState(3, "18", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack17 == null || guiTools$displayStack17.isEmpty())
					break guiTools$itemDisplay17;
				boolean guiTools$displayMasked17 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 99), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
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
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay18 : {
				if (!(true))
					break guiTools$itemDisplay18;
				net.minecraft.world.item.ItemStack guiTools$displayStack18 = menu.getMenuState(3, "19", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack18 == null || guiTools$displayStack18.isEmpty())
					break guiTools$itemDisplay18;
				boolean guiTools$displayMasked18 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -128), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked18) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack18, this.leftPos + -128, this.topPos + -80);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack18, this.leftPos + -128, this.topPos + -80);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay19 : {
				if (!(true))
					break guiTools$itemDisplay19;
				net.minecraft.world.item.ItemStack guiTools$displayStack19 = menu.getMenuState(3, "20", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack19 == null || guiTools$displayStack19.isEmpty())
					break guiTools$itemDisplay19;
				boolean guiTools$displayMasked19 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -86), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked19) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack19, this.leftPos + -86, this.topPos + -80);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack19, this.leftPos + -86, this.topPos + -80);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay20 : {
				if (!(true))
					break guiTools$itemDisplay20;
				net.minecraft.world.item.ItemStack guiTools$displayStack20 = menu.getMenuState(3, "21", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack20 == null || guiTools$displayStack20.isEmpty())
					break guiTools$itemDisplay20;
				boolean guiTools$displayMasked20 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -44), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked20) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack20, this.leftPos + -44, this.topPos + -80);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack20, this.leftPos + -44, this.topPos + -80);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay21 : {
				if (!(true))
					break guiTools$itemDisplay21;
				net.minecraft.world.item.ItemStack guiTools$displayStack21 = menu.getMenuState(3, "22", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack21 == null || guiTools$displayStack21.isEmpty())
					break guiTools$itemDisplay21;
				boolean guiTools$displayMasked21 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -128), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked21) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack21, this.leftPos + -128, this.topPos + -37);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack21, this.leftPos + -128, this.topPos + -37);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay22 : {
				if (!(true))
					break guiTools$itemDisplay22;
				net.minecraft.world.item.ItemStack guiTools$displayStack22 = menu.getMenuState(3, "23", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack22 == null || guiTools$displayStack22.isEmpty())
					break guiTools$itemDisplay22;
				boolean guiTools$displayMasked22 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -86), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked22) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack22, this.leftPos + -86, this.topPos + -37);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack22, this.leftPos + -86, this.topPos + -37);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay23 : {
				if (!(true))
					break guiTools$itemDisplay23;
				net.minecraft.world.item.ItemStack guiTools$displayStack23 = menu.getMenuState(3, "24", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack23 == null || guiTools$displayStack23.isEmpty())
					break guiTools$itemDisplay23;
				boolean guiTools$displayMasked23 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -44), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked23) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack23, this.leftPos + -44, this.topPos + -37);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack23, this.leftPos + -44, this.topPos + -37);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay24 : {
				if (!(true))
					break guiTools$itemDisplay24;
				net.minecraft.world.item.ItemStack guiTools$displayStack24 = menu.getMenuState(3, "25", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack24 == null || guiTools$displayStack24.isEmpty())
					break guiTools$itemDisplay24;
				boolean guiTools$displayMasked24 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -128), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked24) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack24, this.leftPos + -128, this.topPos + 10);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack24, this.leftPos + -128, this.topPos + 10);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay25 : {
				if (!(true))
					break guiTools$itemDisplay25;
				net.minecraft.world.item.ItemStack guiTools$displayStack25 = menu.getMenuState(3, "26", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack25 == null || guiTools$displayStack25.isEmpty())
					break guiTools$itemDisplay25;
				boolean guiTools$displayMasked25 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -86), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked25) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack25, this.leftPos + -86, this.topPos + 10);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack25, this.leftPos + -86, this.topPos + 10);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay26 : {
				if (!(true))
					break guiTools$itemDisplay26;
				net.minecraft.world.item.ItemStack guiTools$displayStack26 = menu.getMenuState(3, "27", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack26 == null || guiTools$displayStack26.isEmpty())
					break guiTools$itemDisplay26;
				boolean guiTools$displayMasked26 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -44), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked26) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack26, this.leftPos + -44, this.topPos + 10);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack26, this.leftPos + -44, this.topPos + 10);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay27 : {
				if (!(true))
					break guiTools$itemDisplay27;
				net.minecraft.world.item.ItemStack guiTools$displayStack27 = menu.getMenuState(3, "28", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack27 == null || guiTools$displayStack27.isEmpty())
					break guiTools$itemDisplay27;
				boolean guiTools$displayMasked27 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 17), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked27) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack27, this.leftPos + 17, this.topPos + -80);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack27, this.leftPos + 17, this.topPos + -80);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay28 : {
				if (!(true))
					break guiTools$itemDisplay28;
				net.minecraft.world.item.ItemStack guiTools$displayStack28 = menu.getMenuState(3, "29", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack28 == null || guiTools$displayStack28.isEmpty())
					break guiTools$itemDisplay28;
				boolean guiTools$displayMasked28 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 58), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked28) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack28, this.leftPos + 58, this.topPos + -80);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack28, this.leftPos + 58, this.topPos + -80);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay29 : {
				if (!(true))
					break guiTools$itemDisplay29;
				net.minecraft.world.item.ItemStack guiTools$displayStack29 = menu.getMenuState(3, "30", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack29 == null || guiTools$displayStack29.isEmpty())
					break guiTools$itemDisplay29;
				boolean guiTools$displayMasked29 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 99), (1.0F - 1.0f) * (this.topPos + -80), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked29) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack29, this.leftPos + 99, this.topPos + -80);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack29, this.leftPos + 99, this.topPos + -80);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay30 : {
				if (!(true))
					break guiTools$itemDisplay30;
				net.minecraft.world.item.ItemStack guiTools$displayStack30 = menu.getMenuState(3, "31", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack30 == null || guiTools$displayStack30.isEmpty())
					break guiTools$itemDisplay30;
				boolean guiTools$displayMasked30 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 17), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked30) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack30, this.leftPos + 17, this.topPos + -37);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack30, this.leftPos + 17, this.topPos + -37);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay31 : {
				if (!(true))
					break guiTools$itemDisplay31;
				net.minecraft.world.item.ItemStack guiTools$displayStack31 = menu.getMenuState(3, "32", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack31 == null || guiTools$displayStack31.isEmpty())
					break guiTools$itemDisplay31;
				boolean guiTools$displayMasked31 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 58), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked31) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack31, this.leftPos + 58, this.topPos + -37);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack31, this.leftPos + 58, this.topPos + -37);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay32 : {
				if (!(true))
					break guiTools$itemDisplay32;
				net.minecraft.world.item.ItemStack guiTools$displayStack32 = menu.getMenuState(3, "33", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack32 == null || guiTools$displayStack32.isEmpty())
					break guiTools$itemDisplay32;
				boolean guiTools$displayMasked32 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 99), (1.0F - 1.0f) * (this.topPos + -37), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked32) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack32, this.leftPos + 99, this.topPos + -37);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack32, this.leftPos + 99, this.topPos + -37);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay33 : {
				if (!(true))
					break guiTools$itemDisplay33;
				net.minecraft.world.item.ItemStack guiTools$displayStack33 = menu.getMenuState(3, "34", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack33 == null || guiTools$displayStack33.isEmpty())
					break guiTools$itemDisplay33;
				boolean guiTools$displayMasked33 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 17), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked33) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack33, this.leftPos + 17, this.topPos + 10);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack33, this.leftPos + 17, this.topPos + 10);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay34 : {
				if (!(true))
					break guiTools$itemDisplay34;
				net.minecraft.world.item.ItemStack guiTools$displayStack34 = menu.getMenuState(3, "35", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack34 == null || guiTools$displayStack34.isEmpty())
					break guiTools$itemDisplay34;
				boolean guiTools$displayMasked34 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 58), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked34) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack34, this.leftPos + 58, this.topPos + 10);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack34, this.leftPos + 58, this.topPos + 10);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay35 : {
				if (!(true))
					break guiTools$itemDisplay35;
				net.minecraft.world.item.ItemStack guiTools$displayStack35 = menu.getMenuState(3, "36", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack35 == null || guiTools$displayStack35.isEmpty())
					break guiTools$itemDisplay35;
				boolean guiTools$displayMasked35 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 99), (1.0F - 1.0f) * (this.topPos + 10), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked35) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack35, this.leftPos + 99, this.topPos + 10);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack35, this.leftPos + 99, this.topPos + 10);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food_wip"), -135, -98, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1"), -133, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy"), -91, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2"), -49, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy"), -133, -12, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_2"), -91, -12, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_3"), -49, -12, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_4"), -133, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_5"), -91, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_6"), -49, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7"), 10, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy"), 51, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_2"), 92, -59, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_3"), 10, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_4"), 51, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_5"), 92, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_6"), 10, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_7"), 51, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_food_tier_gui.label_food1_copy_2_copy_7_copy_8"), 92, 35, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18, new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/base_icon.png"), ResourceLocation.parse("omnichef:textures/screens/base_icon.png")),
				e -> {
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
		imagebutton_food_icon = new ImageButton(this.leftPos + 148, this.topPos + -68, 18, 18, new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/food_icon.png"), ResourceLocation.parse("omnichef:textures/screens/food_icon.png")),
				e -> {
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
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/clients_icon.png"), ResourceLocation.parse("omnichef:textures/screens/clients_icon.png")), e -> {
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
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/appliences_icon.png"), ResourceLocation.parse("omnichef:textures/screens/appliences_icon.png")), e -> {
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
		imagebutton_stats_icon = new ImageButton(this.leftPos + 147, this.topPos + 17, 18, 18, new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/stats_icon.png"), ResourceLocation.parse("omnichef:textures/screens/stats_icon.png")),
				e -> {
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
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png"), ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_last_page_icon);
		imagebutton_next_page_icon = new ImageButton(this.leftPos + 108, this.topPos + 57, 16, 16,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/next_page_icon.png"), ResourceLocation.parse("omnichef:textures/screens/next_page_icon.png")), e -> {
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