package net.mcreator.omnichef.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.omnichef.entity.CriticEntity;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class CriticOnInitialEntitySpawnProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		double skinNumber = 0;
		double positionSpace = 0;
		Entity client = null;
		String name = "";
		String helper = "";
		File ClientsNameList = new File("");
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		com.google.gson.JsonArray ClientsList = new com.google.gson.JsonArray();
		client = entity;
		{
			Entity _ent = client;
			if (!_ent.level().isClientSide() && _ent.getServer() != null) {
				_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
						_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "data merge entity @s {Invulnerable:1b}");
			}
		}
		ClientsNameList = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + "ClientsNameList.json");
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(ClientsNameList));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				Object = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				ClientsList = Object.get("Critics names").getAsJsonArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		skinNumber = Mth.nextInt(RandomSource.create(), 0, (int) (ClientsList.size() - 1));
		name = ClientsList.get((int) skinNumber).getAsString();
		if (name.contains("_")) {
			helper = name.replace("_", " ");
			positionSpace = helper.indexOf(" ");
			client.setCustomName(Component.literal(((helper.substring(0, 1)).toUpperCase() + "" + helper.substring(1, (int) positionSpace) + " " + (helper.substring((int) (positionSpace + 1), (int) (positionSpace + 2))).toUpperCase()
					+ helper.substring((int) (positionSpace + 2)))));
		} else {
			client.setCustomName(Component.literal(((name.substring(0, 1)).toUpperCase() + "" + name.substring(1))));
		}
		if (client instanceof CriticEntity customEntity)
			customEntity.setTexture(("texturecritic" + name));
	}
}