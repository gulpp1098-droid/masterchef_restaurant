package net.mcreator.omnichef.procedures;

public class SaveJsonAtomicallyProcedure {
	public static boolean execute(com.google.gson.JsonObject jsonObject, String directoryPath, String fileName) {
		if (jsonObject == null || directoryPath == null || fileName == null)
			return false;
		com.google.gson.JsonObject data = new com.google.gson.JsonObject();
		String folder = "";
		String name = "";
		boolean success = false;
		data = jsonObject;
		folder = directoryPath;
		name = fileName;
		java.nio.file.Path temporary = null;
		java.nio.file.Path target = null;
		try {
			if (data == null || folder == null || folder.isBlank() || name == null || name.isBlank()) {
				throw new IllegalArgumentException("JSON data, directory and filename are required");
			}
			var atomicFileName = java.nio.file.Path.of(name);
			if (atomicFileName.isAbsolute() || atomicFileName.getNameCount() != 1 || name.contains("/") || name.contains("\\") || name.equals(".") || name.equals("..")) {
				throw new IllegalArgumentException("Expected a filename without a directory: " + name);
			}
			var directory = java.nio.file.Path.of(folder).toAbsolutePath().normalize();
			target = directory.resolve(atomicFileName);
			var snapshot = data.deepCopy();
			String json = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(snapshot);
			java.nio.file.Files.createDirectories(directory);
			temporary = java.nio.file.Files.createTempFile(directory, ".omnichef-save-", ".tmp");
			try (var channel = java.nio.channels.FileChannel.open(temporary, java.nio.file.StandardOpenOption.WRITE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {
				var bytes = java.nio.ByteBuffer.wrap(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
				while (bytes.hasRemaining()) {
					channel.write(bytes);
				}
				channel.force(true);
			}
			try (var reader = java.nio.file.Files.newBufferedReader(temporary, java.nio.charset.StandardCharsets.UTF_8)) {
				var saved = com.google.gson.JsonParser.parseReader(reader);
				if (!snapshot.equals(saved)) {
					throw new java.io.IOException("Temporary JSON verification failed");
				}
			}
			java.nio.file.Files.move(temporary, target, java.nio.file.StandardCopyOption.ATOMIC_MOVE, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			temporary = null;
			success = true;
		} catch (Exception exception) {
			net.mcreator.omnichef.OmnichefMod.LOGGER.error("Failed to save JSON file: " + (target != null ? target : folder + "/" + name), exception);
		} finally {
			if (temporary != null) {
				try {
					java.nio.file.Files.deleteIfExists(temporary);
				} catch (java.io.IOException exception) {
					net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Could not remove temporary JSON file: " + temporary, exception);
				}
			}
		}
		return success;
	}
}