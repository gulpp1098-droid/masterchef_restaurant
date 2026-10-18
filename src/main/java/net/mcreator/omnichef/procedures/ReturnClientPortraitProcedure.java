package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ReturnClientPortraitProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		String clientImage = "";
		if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Akari")) {
			return "client_akari_icon.png";
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Alberto")) {
			return "client_alberto_icon.png";
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Carlo Romano")) {
			return "client_carlo_romano_icon.png";
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Chuck")) {
			return "client_chuck_icon.png";
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Maggie")) {
			return "client_maggie_icon.png";
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Ninjoy")) {
			return "client_ninjoy_icon.png";
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Tohru")) {
			return "client_tohru_icon.png";
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Wally")) {
			return "client_wally_icon.png";
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientTexture).equals("Papa Louie")) {
			return "critic_papa_louie_icon.png";
		}
		return "client_chuck_icon.png";
	}
}