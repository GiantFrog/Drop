package science.skywhale.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Drop extends Game
{
	public SpriteBatch batch;
	public BitmapFont font;
	public Vector3 touchPos;
	
	public void create()
	{
		batch = new SpriteBatch();
		font = new BitmapFont();	//defaults to Arial
		touchPos = new Vector3();
		this.setScreen(new MainMenuScreen(this));
	}
	
	public void render()
	{
		super.render();
	}
	
	public void dispose()
	{
		batch.dispose();
		font.dispose();
		this.getScreen().dispose();
	}

	//TODO animations
	//TODO sprite packer
	//TODO settings screen
}
