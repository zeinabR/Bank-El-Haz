package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.BankElHaz;
import com.mygdx.game.Network.MainFlow;
import com.mygdx.game.Network.NetworkManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		MainFlow flow = new MainFlow();
		NetworkManager manager = flow.init();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "BankElHaz";
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new BankElHaz(manager), config);
	}
}
