package science.skywhale.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game
{
	public SpriteBatch batch;
	public BitmapFont font;
	
	public void create()
	{
		batch = new SpriteBatch();
		font = new BitmapFont();	//defaults to Arial
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

	//TODO buttons
	//TODO settings screen
}
