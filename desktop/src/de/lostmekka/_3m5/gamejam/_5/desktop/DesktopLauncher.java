package de.lostmekka._3m5.gamejam._5.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.lostmekka._3m5.gamejam._5.JamGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 700;
		new LwjglApplication(new JamGame(), config);
	}
}
