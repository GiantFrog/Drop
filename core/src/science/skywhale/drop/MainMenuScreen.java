package science.skywhale.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen, InputProcessor
{
	private final Drop game;
	private OrthographicCamera camera;
	
	public MainMenuScreen (final Drop game)
	{
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void render (float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		game.font.draw(game.batch, "Welcome to Drop!!", 100, 150);
		game.font.draw(game.batch, "Are you ready to begin your journey?", 100, 100);
		game.batch.end();
	}
	
	@Override
	public void resize (int width, int height)
	{
	
	}
	@Override
	public void show()
	{
	
	}
	@Override
	public void hide()
	{
	
	}
	@Override
	public void pause()
	{
	
	}
	@Override
	public void resume()
	{
	
	}
	
	@Override
	public void dispose()
	{
	
	}

	@Override
	public boolean keyDown (int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp (int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped (char character)
	{
		return false;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button)
	{
		game.setScreen(new GameScreen(game));
		dispose();
		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled (int amount)
	{
		return false;
	}
}
