package net.mcreator.omnichef.procedures;

public class GetTargetClientsForLevelProcedure {
	public static double execute(double level) {
		if (level <= 5) {
			return 2;
		} else if (level <= 9) {
			return 3;
		}
		return Math.round((0.0002007114 * Math.pow(level, 3) - 0.01210222 * Math.pow(level, 2) + 0.7033476 * level) - 0.0239655);
	}
}