package science.skywhale.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen
{
	final Drop game;
	
	private Texture dropImg, bucketImg;
	private Sound dropSound1, dropSound2, dropSound3;
	private Music rainMusic;
	private OrthographicCamera camera;
	private Rectangle bucket, water;
	private Vector3 touchPos;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private int dropsGathered;
	private ShapeRenderer shapeRenderer;

	//set and configure all the objects
	public GameScreen (final Drop game)
	{
		this.game = game;
		
		dropImg = new Texture("droplet.png");
		bucketImg = new Texture(Gdx.files.internal("bucket.png"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("ChocolateRain.mp3"));
		dropSound1 = Gdx.audio.newSound(Gdx.files.internal("drop1.wav"));
		dropSound2 = Gdx.audio.newSound(Gdx.files.internal("drop2.wav"));
		dropSound3 = Gdx.audio.newSound(Gdx.files.internal("drop3.wav"));
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		bucket = new Rectangle();
		bucket.x = 800/2 - 64/2;
		bucket.y = 20;
		bucket.width = bucket.height = 64;

		water = new Rectangle();
		water.x = water.y = 0;
		water.width = 800;
		water.height = 0;
		
		rainMusic.setLooping(true);
		rainMusic.setVolume(.3f);

		shapeRenderer = new ShapeRenderer();
		
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
	}
	
	private void spawnRaindrop()
	{
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	@Override
	public void render (float delta)
	{
		//background
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		//SpriteBatch renders based on camera's coordinate system
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		game.batch.draw(bucketImg, bucket.x, bucket.y);
		for (Rectangle raindrop: raindrops)
		{
			game.batch.draw(dropImg, raindrop.x, raindrop.y);
		}
		game.font.draw(game.batch, "Drops Gathered: " + dropsGathered, 10, 470);
		game.batch.end();

		//bucket movement
		if (Gdx.input.isTouched())
		{
			touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64/2;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			bucket.x -= 300*Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			bucket.x += 300*Gdx.graphics.getDeltaTime();
		
		if (bucket.x < 0)
			bucket.x = 0;
		else if (bucket.x > 800 - 64)
			bucket.x = 800-64;
		
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
			spawnRaindrop();
		
		//move drops and remove them if they move off the screen or into the bucket
		Iterator<Rectangle> it = raindrops.iterator();
		while (it.hasNext())
		{
			Rectangle raindrop = it.next();
			raindrop.y -= 200*Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0)
			{
				water.height += 10;
				it.remove();
			}
			else if (raindrop.overlaps(bucket))
			{
				dropsGathered++;
				switch ((int)(Math.random()*3))
				{
					case 0:
						dropSound1.play();
						break;
					case 1:
						dropSound2.play();
						break;
					case 2:
						dropSound3.play();
						break;
				}
				it.remove();
			}
		}
		//draw the water
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 255, 1);
		shapeRenderer.rect(water.getX(), water.getY(), water.getWidth(), water.getHeight());
		shapeRenderer.end();

		//lose if the water is too high
		if (water.getHeight() >= 80)
		{
			game.setScreen(new GameOverScreen(game, dropsGathered));
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
		rainMusic.play();
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
	public void dispose ()
	{
		dropImg.dispose();
		bucketImg.dispose();
		dropSound1.dispose();
		dropSound2.dispose();
		dropSound3.dispose();
		rainMusic.dispose();
		shapeRenderer.dispose();
	}
}
