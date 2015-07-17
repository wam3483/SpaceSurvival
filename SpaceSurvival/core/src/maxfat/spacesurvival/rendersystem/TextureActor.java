package maxfat.spacesurvival.rendersystem;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TextureActor extends Actor {
	private final TextureRegion region;

	public TextureActor(TextureRegion region) {
		this.region = region;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float rotation = this.getRotation();
		float width = this.region.getRegionWidth();
		float height = this.region.getRegionHeight();
		batch.setColor(this.getColor());
		batch.draw(this.region, //
				getX(), getY(), //
				width / 2, height / 2, //
				width, height,//
				getScaleX(), getScaleY(), rotation);
	}
}