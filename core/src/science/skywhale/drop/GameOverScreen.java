package science.skywhale.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class GameOverScreen implements Screen
{
	final Drop game;
	private int score, position;
	OrthographicCamera camera;
	private FileHandle leaderFile;
	private Json leaderSon;
	private Array<LeaderboardEntry> leaderboard;
	private String readableBoard;

	public GameOverScreen (final Drop game, int score)
	{
		this.game = game;
		this.score = score;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		leaderFile = Gdx.files.local("leaderboard.txt");
		leaderSon = new Json();
		readableBoard = "";

		leaderboard = leaderSon.fromJson(Array.class, leaderFile.readString());	//load the old leaderboard

		if (leaderboard != null) {
			boolean added = false;
			//add the new score in the place of the first entry with a smaller score.
			for (int a = 0; a < leaderboard.size; a++) {
				if (leaderboard.get(a).getScore() < score) {
					position = a;
					addToLeaderboard();
					added = true;
					break;
				}
			}
			//if it hasn't been added, add it to the end, board will be trimmed later.
			if (!added)
			{
				position = leaderboard.size;
				addToLeaderboard();
			}
		}
		else
		{
			leaderboard = new Array<LeaderboardEntry>();
			position = 0;
			addToLeaderboard();
		}

		//remove any entries after the max size
		if (leaderboard.size > 20)
			leaderboard.truncate(20);

		updateLeaderboard();
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

		if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.Q))
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
				leaderboard.insert(position, new LeaderboardEntry(name, score));
				updateLeaderboard();
			}
			@Override
			public void canceled()
			{
				leaderboard.insert(position, new LeaderboardEntry("Anonymous", score));
				updateLeaderboard();
			}
		}, "You did well!!!", "", "Add your name to the leaderboard!");
	}
	//save the leaderboard and turn it into a readable string to print
	private void updateLeaderboard()
	{
		leaderFile.writeString(leaderSon.toJson(leaderboard), false);
		readableBoard = "";
		for (LeaderboardEntry entry: leaderboard)
		{
			readableBoard += entry.getName() + ": " + entry.getScore() + "\n";
		}
	}
}
