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
import java.security.Signature;

import static com.sun.deploy.util.Base64Wrapper.encodeToString;

public class GameOverScreen implements Screen, InputProcessor
{
	private final Drop game;
	private int score, lowestScore;
	private String name, toDraw;
	private OrthographicCamera camera;
	private String leftColString, rightColString;
	private Socket server;
	private BufferedReader in;
	private Signature signature;

	public GameOverScreen (final Drop game, int score)		//Game has just finished
	{
		this.game = game;
		this.score = score;
		lowestScore = -1;
		Gdx.input.setInputProcessor(this);
		camera = game.camera;
		leftColString = "";
		rightColString = "";
		toDraw = "Your bucket has been submerged. Click to try again.";

		try
		{
			signature = Signature.getInstance("RSA");
			signature.initSign(game.key);
			
			String toSign = name + ":#:" + score;
			//out.println(name + ":" + score);
			signature.update(toSign.getBytes());
			String signed = encodeToString(signature.sign());
			System.out.println(signed);
			
			server = new Socket("drop.skywhale.science", 9027);
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			requestName(); //will call contactServer()
		}
		catch (Exception dang)
		{
			leftColString = "Unable to connect to the online leaderboard.\n" + dang;
			rightColString = ":(";
		}
	}
	public GameOverScreen (final Drop game)		//from the main menu, not the game
	{
		this.game = game;
		score = -1;
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		leftColString = "";
		rightColString = "";
		toDraw = "Here is the current leaderboard. Look to it for inspiration.";

		try
		{
			server = new Socket("drop.skywhale.science", 9027);
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			name = "Umiko";
			contactServer();
		}
		catch (IOException dang)
		{
			leftColString = "Unable to connect to the online leaderboard.\n" + dang;
			rightColString = ":(";
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
		game.font.draw(game.batch, leftColString, 60, 400);
		game.font.draw(game.batch, rightColString, 430, 400);
		game.batch.end();
	}

	@Override
	public void resize (int width, int height)
	{
		game.viewport.update(width, height);
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

	//pulls the leaderboard from the server, then prompts the user for their name if we scored high enough.
	private void requestName()
	{
		String input;
		int added = 0;
		try
		{
			while (true)
			{
				input = in.readLine();
				if (input.equals("&"))        //& signifies end of leaderboard
					break;
				else
				{
					if (added < 22)
					{
						leftColString += input + "\n";
						added++;
					} else
					{
						rightColString += input + "\n";
						added++;
						//if this is the last entry, make note of th lowest score
						if (added == 44)
							lowestScore = Integer.parseInt(input.split(": ")[1]);
					}
				}
			}
		}
		catch (IOException eek)
		{
			leftColString = "Unable to pull the leaderboard from the server.\n" + eek;
		}
		
		if (score > lowestScore)
		{
			Gdx.input.getTextInput(new Input.TextInputListener()
			{
				@Override
				public void input (String inName)
				{
					name = inName;
					contactServer();
				}
				
				@Override
				public void canceled ()
				{
					name = "Anonymous";
					contactServer();
				}
			}, "You did well!!!", "", "Add your name to the leaderboard!");
		}
	}
	
	//follow up with the server to either add our score or say we didn't make it on.
	private void contactServer()
	{
		try
		{
			PrintWriter out = new PrintWriter(server.getOutputStream(), true);
			
			//we made it to the leaderboard! add our score and get the updated version
			if (score > lowestScore)
			{
				String toSign = name + ":#:" + score;
				//out.println(name + ":" + score);
				signature.update(toSign.getBytes());
				String signed = encodeToString(signature.sign());
				System.out.println(signed);
				
				leftColString = "";
				rightColString = "";
				String input;
				int added = 0;
				while (true)
				{
					input = in.readLine();
					if (input.equals("&"))		//& signifies end of leaderboard
						break;
					else
					{
						if (added < 22)
						{
							leftColString += input + "\n";
							added++;
						}
						else
						{
							rightColString += input + "\n";
							added++;
						}
					}
				}
			}
			else
				out.println("nope");
			
			out.close();
			in.close();
			server.close();
		}
		catch (Exception eek)
		{
			leftColString = "Unable to submit " + name + "'s score to the server.\n" + eek;
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
