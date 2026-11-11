package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.init.OmnichefModMenus;
import net.mcreator.omnichef.init.OmnichefModItems;
import net.mcreator.omnichef.init.OmnichefModBlocks;

public class FillGuideItemDisplaysProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
			ItemStack _displayStack0 = new ItemStack(OmnichefModBlocks.CHAIR.get()).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(100), _displayStack0, true);
			ItemStack _displayStack1 = new ItemStack(OmnichefModBlocks.SERVICE_TABLE.get()).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(101), _displayStack1, true);
			ItemStack _displayStack2 = new ItemStack(OmnichefModBlocks.RECEPTION.get()).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(102), _displayStack2, true);
			ItemStack _displayStack3 = new ItemStack(OmnichefModBlocks.RUG_QUEUE.get()).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(103), _displayStack3, true);
			ItemStack _displayStack4 = new ItemStack(OmnichefModBlocks.PREPARATION_STATION.get()).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(104), _displayStack4, true);
			ItemStack _displayStack5 = new ItemStack(OmnichefModBlocks.TRADE_BOARD.get()).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(105), _displayStack5, true);
			ItemStack _displayStack6 = new ItemStack(OmnichefModItems.PREPARED_DISH.get()).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(106), _displayStack6, true);
			ItemStack _displayStack7 = new ItemStack(OmnichefModItems.CHEFS_DIARY.get()).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(107), _displayStack7, true);
		}
	}
}