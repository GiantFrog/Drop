package science.skywhale.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameOverScreen implements Screen, InputProcessor
{
	private final Drop game;
	private int score;
	private String name, toDraw;
	private OrthographicCamera camera;
	private String readableBoard;
	private Socket server;

	public GameOverScreen (final Drop game, int score)
	{
		this.game = game;
		this.score = score;
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		readableBoard = "";
		toDraw = "Your bucket has been submerged. Click to try again.";

		try
		{
			server = new Socket("ts.skywhale.science", 9027);
			requestName(); //will call addToLeaderboard()
		}
		catch (IOException dang)
		{
			readableBoard = "Unable to connect to the online leaderboard.\n" + dang;
		}
	}
	public GameOverScreen (final Drop game)
	{
		this.game = game;
		score = -1;
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		readableBoard = "";
		toDraw = "Here is the current leaderboard. Look to it for inspiration.";

		try
		{
			server = new Socket("ts.skywhale.science", 9027);
			name = "Umiko";
			addToLeaderboard();
		}
		catch (IOException dang)
		{
			readableBoard = "Unable to connect to the online leaderboard.\n" + dang;
		}
	}

	@Override
	public void render (float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.font.draw(game.batch, toDraw, 60, 450);
		game.font.draw(game.batch, "You scored " + score + " points!", 60, 435);
		game.font.draw(game.batch, readableBoard, 60, 400);
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

	private void requestName()
	{
		Gdx.input.getTextInput(new Input.TextInputListener()
		{
			@Override
			public void input(String inName)
			{
				name = inName;
				addToLeaderboard();
			}
			@Override
			public void canceled()
			{
				name = "Anonymous";
				addToLeaderboard();
			}
		}, "You did well!!!", "", "Add your name to the leaderboard!");
	}
	private void addToLeaderboard()
	{
		try
		{
			PrintWriter out = new PrintWriter(server.getOutputStream(), true);
			out.println(name + ":" + score);
			BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			String input;
			while (true)
			{
				input = in.readLine();
				if (input.equals("&"))		//& signifies end of leaderboard
					break;
				else
					readableBoard += input + "\n";
			}
			out.print("k");
			out.close();
			in.close();
			server.close();
		}
		catch (IOException eek)
		{
			readableBoard = "Unable to submit " + name + "'s score to the server.\n" + eek;
		}
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
		game.setScreen(new MainMenuScreen(game));
		dispose();
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button)
	{
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
