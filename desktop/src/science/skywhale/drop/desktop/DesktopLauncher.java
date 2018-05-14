package science.skywhale.drop.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import science.skywhale.drop.Drop;

public class DesktopLauncher
{
	public static void main (String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Drop";
		config.width = 800;
		config.height = 480;
		config.foregroundFPS = config.backgroundFPS = 120;
		config.addIcon("bucket.png", Files.FileType.Internal);
		config.addIcon("bucketSmall.png", Files.FileType.Internal);
		config.addIcon("bucketTiny.png", Files.FileType.Internal);
		new LwjglApplication(new Drop(), config);
	}
}
