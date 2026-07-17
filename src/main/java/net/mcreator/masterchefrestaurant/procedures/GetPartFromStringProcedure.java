package net.mcreator.masterchefrestaurant.procedures;

public class GetPartFromStringProcedure {
	public static String execute(double indexNumber, String stringFull) {
		if (stringFull == null)
			return "";
		double index = 0;
		String string = "";
		String returnString = "";
		string = stringFull;
		index = 0;
		if (!(string).equals("")) {
			while (string.contains(":")) {
				if (index == indexNumber) {
					returnString = string.substring(0, string.indexOf(":", 0));
					return returnString;
				}
				string = string.substring(string.indexOf(":", 0) + 1);
				index = index + 1;
			}
			if (index == indexNumber) {
				return string;
			}
		}
		return "Missing string!";
	}
}