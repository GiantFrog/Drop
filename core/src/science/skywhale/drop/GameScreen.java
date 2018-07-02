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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen
{
	private final Drop game;
	private Texture dropImg, stoneImg, spongeImg, bucketImg;
	private Sound[] dropSound, splashSound, tinkSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private Rectangle bucket, water;
	private Array<FallingThing> raindrops;	//stores sponges and stuff, too
	private long lastDropTime, lastCollisionTime;
	private int dropsGathered, speed, speedMod;
	private ShapeRenderer shapeRenderer;
	private boolean left, right;

	//set and configure all the objects
	public GameScreen (final Drop game)
	{
		this.game = game;
		Gdx.input.setInputProcessor(null);
		dropSound = new Sound[3];
		splashSound = new Sound[3];
		tinkSound = new Sound[3];
		
		dropImg = new Texture("droplet.png");
		stoneImg = new Texture("stone.png");
		spongeImg = new Texture("sponge.png");
		bucketImg = new Texture(Gdx.files.internal("bucket.png"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("ChocolateRain.mp3"));
		dropSound[0] = Gdx.audio.newSound(Gdx.files.internal("drop1.wav"));
		dropSound[1] = Gdx.audio.newSound(Gdx.files.internal("drop2.wav"));
		dropSound[2] = Gdx.audio.newSound(Gdx.files.internal("drop3.mp3"));
		splashSound[0] = Gdx.audio.newSound(Gdx.files.internal("splash1.mp3"));
		splashSound[1] = Gdx.audio.newSound(Gdx.files.internal("splash2.mp3"));
		splashSound[2] = Gdx.audio.newSound(Gdx.files.internal("splash3.mp3"));
		tinkSound[0] = Gdx.audio.newSound(Gdx.files.internal("tink1.mp3"));
		tinkSound[1] = Gdx.audio.newSound(Gdx.files.internal("tink2.mp3"));
		tinkSound[2] = Gdx.audio.newSound(Gdx.files.internal("tink3.mp3"));
		speed = speedMod = 0;
		
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
		
		raindrops = new Array<FallingThing>();
		spawnRaindrop();
	}
	
	private void spawnRaindrop()
	{
		double randy = Math.random();
		if (randy >= .95)		//5% chance for a sponge
			raindrops.add(new FallingThing(3, spongeImg));
		else if (randy >= .75)	//20% chance for a stone
			raindrops.add(new FallingThing(2, stoneImg));
		else					//75% chance for a raindrop
			raindrops.add(new FallingThing(1, dropImg));
		//mark the current time, so we can later check to see if it has been long enough to drop another one
		lastDropTime = TimeUtils.nanoTime();
	}
	
	@Override
	public void render (float delta) {
		//background
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		//SpriteBatch renders based on camera's coordinate system
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(bucketImg, bucket.x, bucket.y);
		for (FallingThing raindrop : raindrops)
		{
			game.batch.draw(raindrop.getImg(), raindrop.getX(), raindrop.getY());
		}
		game.font.draw(game.batch, "Drops Gathered: " + dropsGathered, 10, 470);
		game.batch.end();

		left = right = false;

		//touch movement
		if (Gdx.input.isTouched(1))
		{
			game.touchPos.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
			camera.unproject(game.touchPos);
			//16 pixel buffer zone with no movement
			if (game.touchPos.x < bucket.x + 24)
				speed = -300;
			else if (game.touchPos.x > bucket.x + 40)
				speed = 300;
			else
				speed = 0;
		}
		else if (Gdx.input.isTouched(0))
		{
			game.touchPos.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
			camera.unproject(game.touchPos);
			//16 pixel buffer zone with no movement
			if (game.touchPos.x < bucket.x + 24)
				speed = -300;
			else if (game.touchPos.x > bucket.x + 40)
				speed = 300;
			else
				speed = 0;
		}
		else
			speed = 0;

		//spawn items and reset bucket speed mod, if it is time.
		if (TimeUtils.nanoTime() - lastDropTime > 900000000)
			spawnRaindrop();
		if (speedMod != 0 && TimeUtils.nanoTime() - lastCollisionTime > 200000000)
			speedMod = 0;
		
		//move drops and remove them if they move off the screen or into the bucket
		Iterator<FallingThing> it = raindrops.iterator();
		while (it.hasNext())
		{
			FallingThing raindrop = it.next();
			raindrop.fall(Gdx.graphics.getDeltaTime());

			switch (raindrop.getType())
			{
				case 1:		//drop
					if (raindrop.getY() + 64 < 0)
					{
						water.height += 10;
						it.remove();
					}
					else if (raindrop.getRect().overlaps(bucket))
					{
						dropsGathered++;
						dropSound[(int)(Math.random()*3)].play();
						it.remove();
					}
					break;

				case 2:		//stone
					if (raindrop.getY() + 64 < 0)
					{
						splashSound[(int)(Math.random()*3)].play();
						it.remove();
					}
					else if (raindrop.getRect().overlaps(bucket))
					{
						if (TimeUtils.nanoTime() > lastCollisionTime + 100000000)
							tinkSound[(int)(Math.random()*3)].play();	//only play a sound if 1/10th sec has passed
						if (raindrop.getX() > bucket.x)
							speedMod = -600;
						else
							speedMod = 600;
						lastCollisionTime = TimeUtils.nanoTime();
					}
					break;

				case 3:		//sponge
					if (raindrop.getY() + 64 < 0)
					{
						if (water.height > 0)
							water.height -= 10;
						it.remove();
					}
					else if (raindrop.getRect().overlaps(bucket))
					{
						if (dropsGathered > 0)
							dropsGathered--;
						//play a spongy sound?
						it.remove();
					}
					break;

				default:
					it.remove();
			}
		}
		
		//bucket movement
		//speed is reset to 0 in the touch movement above
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			speed = -300;
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			speed = 300;
		bucket.x += (speed + speedMod)*Gdx.graphics.getDeltaTime();
		
		//not allowed to go out of bounds
		if (bucket.x < 0)
			bucket.x = 0;
		else if (bucket.x > 800 - 64)
			bucket.x = 800-64;
		
		//draw the water
		shapeRenderer.setProjectionMatrix(camera.combined);

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
		dropSound[0].dispose();
		dropSound[1].dispose();
		dropSound[2].dispose();
		splashSound[0].dispose();
		splashSound[1].dispose();
		splashSound[2].dispose();
		tinkSound[0].dispose();
		tinkSound[1].dispose();
		tinkSound[2].dispose();
		rainMusic.dispose();
		shapeRenderer.dispose();
	}
}
