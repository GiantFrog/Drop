package science.skywhale.drop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class FallingThing
{
	private int type;	//1 for drop, 2 for stone, 3 for sponge
	private Rectangle rect;
	private Texture img;
	private int speed;

	public FallingThing (int type, Texture img)
	{
		this.type = type;
		this.img = img;
		rect = new Rectangle();
		rect.x = MathUtils.random(0, 800-64);
		rect.y = 480;
		rect.width = rect.height = 64;
		speed = 200;
	}

	public void fall (float deltaTime)
	{
		rect.y -= speed*deltaTime;
	}

	public int getType()
	{
		return type;
	}
	public Texture getImg()
	{
		return img;
	}
	public float getX()
	{
		return rect.x;
	}
	public float getY()
	{
		return rect.y;
	}
	public Rectangle getRect()
	{
		return rect;
	}
}
