package maxfat.spacesurvival.rendersystem;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TextureActor extends Actor {
	private final TextureRegion region;

	public TextureActor(TextureRegion region) {
		this.region = region;
		this.setWidth(this.region.getRegionWidth());
		this.setHeight(this.region.getRegionHeight());
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float rotation = this.getRotation();
		float width = this.getWidth();
		float height = this.getHeight();
		batch.setColor(this.getColor());
		float x = getX();
		float y = getY();
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		batch.draw(this.region, //
				x, y, //
				width / 2, height / 2, //
				width, height,//
				scaleX, scaleY, rotation);
	}
}