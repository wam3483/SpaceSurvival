package maxfat.spacesurvival.overlap2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ProgressBarActor extends Actor {
	Drawable bar;
	Drawable endCap;
	float maxWidth;

	public ProgressBarActor(Drawable bar, Drawable endCap, float maxWidth) {
		this.bar = bar;
		this.endCap = endCap;
		this.maxWidth = maxWidth;
	}

	public void setProgressAnimate(float value,float delay, Interpolation interpolation) {
		value = MathUtils.clamp(value, 0, 1);
		float newWidth = maxWidth * value;
		float height = getHeight();
		this.addAction(Actions
				.sizeTo(newWidth, height, delay, interpolation));
	}

	public void setProgress(float value) {
		value = MathUtils.clamp(value, 0, 1);
		this.setWidth(maxWidth * value);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float w = getWidth();
		float h = getHeight();
		float x = getX();
		float y = getY();
		float capWidth = endCap.getMinWidth();
		float barWidth = w - capWidth;
		batch.setColor(this.getColor());
		this.bar.draw(batch, x, y, barWidth, h);
		this.endCap.draw(batch, x + barWidth, y, capWidth, h);
	}
}
