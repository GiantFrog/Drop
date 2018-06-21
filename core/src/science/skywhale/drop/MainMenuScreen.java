package science.skywhale.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen implements Screen
{
	private final Drop game;
	private OrthographicCamera camera;
	private Table table;
	private TextButton startButton, leaderboardButton;
	private Skin skin;
	private Label areYouReady;
	private Stage stage;
	
	public MainMenuScreen (final Drop game)
	{
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		//table.setDebug(true);
		areYouReady = new Label("Welcome to Drop!!\n\nAre you ready to begin your journey?", skin);
		startButton = new TextButton("I am!!", skin);
		leaderboardButton = new TextButton("No...", skin);

		startButton.addListener(new ClickListener()
		{
			@Override
			public void clicked (InputEvent event, float x, float y)
			{
				game.setScreen(new GameScreen(game));
				dispose();
			}
		});
		leaderboardButton.addListener(new ClickListener()
		{
			@Override
			public void clicked (InputEvent event, float x, float y)
			{
				game.setScreen((new GameOverScreen(game)));
				dispose();
			}
		});

		table.add(areYouReady).colspan(2);
		table.row();
		table.add(startButton).width(50).height(35).space(70);
		table.add(leaderboardButton).width(50).height(35).space(70);

		stage.addActor(table);
	}
	
	@Override
	public void render (float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		stage.draw();
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
}
