package dev.stormy.client.clickgui;

import java.awt.Color;

import dev.stormy.client.module.modules.client.ClickGuiModule;

public class Theme {
	public static Color getMainColor() {
		String themeColor = ClickGuiModule.clientTheme.getMode().toString();

		switch (themeColor) {
		case "Pink":
			return new Color(232, 100, 195);
		case "Tryfle":
			return new Color(216, 65, 100);
		case "Coral":
			return new Color(255, 105, 105);
		case "Steel":
			return new Color(52, 152, 219);
		case "Emerald":
			return new Color(46, 204, 113);
		case "Amethyst":
			return new Color(155, 89, 182);
		case "Lily":
			return new Color(76, 56, 108);
		case "Venom":
			return new Color(81, 4, 228);
		default: // UNUSED
			return new Color(255, 255, 255);
		}
	}

	public static Color getBackColor() {
		return new Color(0, 0, 0, 100);
	}

}
