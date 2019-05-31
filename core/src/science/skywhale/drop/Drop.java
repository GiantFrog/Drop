package science.skywhale.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Drop extends Game
{
	public SpriteBatch batch;
	public BitmapFont font;
	public Viewport viewport;
	public int width, height;
	OrthographicCamera camera;
	
	public void create()
	{
		width = 800;
		height = 480;
		batch = new SpriteBatch();
		font = new BitmapFont();	//defaults to Arial
		camera = new OrthographicCamera();
		viewport = new FitViewport(width, height, camera);
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
	
	//TODO texture packer
	//TODO settings screen
}
