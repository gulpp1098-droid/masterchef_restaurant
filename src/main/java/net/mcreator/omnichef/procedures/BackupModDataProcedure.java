package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class BackupModDataProcedure {
	public static boolean execute(LevelAccessor world) {
		String folder = "";
		boolean success = false;
		folder = GetMasterchefWorldPathProcedure.execute(world);
		try {
			if (folder.isBlank()) {
				throw new java.io.IOException("World data path is empty");
			}
			var sourceDirectory = java.nio.file.Path.of(folder);
			var variables = net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world);
			String[] fileNames = {variables.Restaurant_File_Name, variables.RestaurantFood_File_Name, variables.ClientsDatabase_File_Name};
			boolean anyFileExists = false;
			for (String fileName : fileNames) {
				if (java.nio.file.Files.exists(sourceDirectory.resolve(fileName))) {
					anyFileExists = true;
				}
			}
			if (anyFileExists) {
				var backupDirectory = sourceDirectory.resolve("backups");
				java.nio.file.Files.createDirectories(backupDirectory);
				var pendingDirectory = java.nio.file.Files.createTempDirectory(backupDirectory, ".pending-");
				var manifest = new com.google.gson.JsonObject();
				manifest.addProperty("created_at", java.time.Instant.now().toString());
				var files = new com.google.gson.JsonObject();
				for (String fileName : fileNames) {
					var source = sourceDirectory.resolve(fileName);
					if (java.nio.file.Files.exists(source)) {
						var destination = pendingDirectory.resolve(fileName);
						java.nio.file.Files.copy(source, destination);
						if (java.nio.file.Files.mismatch(source, destination) != -1) {
							throw new java.io.IOException("Backup verification failed: " + fileName);
						}
						files.addProperty(fileName, "copied");
					} else {
						files.addProperty(fileName, "missing");
					}
				}
				manifest.add("files", files);
				java.nio.file.Files.writeString(pendingDirectory.resolve("manifest.json"), new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(manifest), java.nio.charset.StandardCharsets.UTF_8);
				String snapshotName = "startup-" + java.time.Instant.now().toEpochMilli() + "-" + java.util.UUID.randomUUID();
				var completedDirectory = backupDirectory.resolve(snapshotName);
				try {
					java.nio.file.Files.move(pendingDirectory, completedDirectory, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
				} catch (java.nio.file.AtomicMoveNotSupportedException exception) {
					java.nio.file.Files.move(pendingDirectory, completedDirectory);
				}
			}
			success = true;
		} catch (Exception exception) {
			net.mcreator.omnichef.OmnichefMod.LOGGER.error("Omnichef: startup backup failed; data initialization was not performed.", exception);
		}
		return success;
	}
}