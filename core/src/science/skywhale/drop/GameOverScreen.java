package science.skywhale.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameOverScreen implements Screen
{
	private final Drop game;
	private int score;
	private OrthographicCamera camera;
	private String readableBoard;
	private Socket server;

	public GameOverScreen (final Drop game, int score)
	{
		this.game = game;
		this.score = score;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		readableBoard = "";

		try
		{
			server = new Socket("ts.skywhale.science", 9027);
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
		game.font.draw(game.batch, "Your bucket has been submerged. Press Q to try again.", 60, 450);
		game.font.draw(game.batch, "You scored " + score + " points!", 60, 435);
		game.font.draw(game.batch, readableBoard, 60, 400);
		game.batch.end();

		if (Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.Q))
		{
			game.setScreen(new GameScreen(game));
			dispose();
		}
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

	private void addToLeaderboard()
	{
		Gdx.input.getTextInput(new Input.TextInputListener()
		{
			@Override
			public void input(String name)
			{
				try
				{
					PrintWriter out = new PrintWriter(server.getOutputStream(), true);
					out.println(name + ":" + score);
					BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
					while (server.getInputStream().read() >= 0)
						readableBoard += in.readLine() + "\n";
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
			public void canceled()
			{
				try
				{
					PrintWriter out = new PrintWriter(server.getOutputStream(), true);
					out.println("Anonymous:" + score);
					BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
					while (server.getInputStream().read() >= 0)
						readableBoard += in.readLine() + "\n";
					out.print("k");
					out.close();
					in.close();
					server.close();
				}
				catch (IOException eek)
				{
					readableBoard = "Unable to submit your score to the server.\n" + eek;
				}
			}
		}, "You did well!!!", "", "Add your name to the leaderboard!");
	}
}
