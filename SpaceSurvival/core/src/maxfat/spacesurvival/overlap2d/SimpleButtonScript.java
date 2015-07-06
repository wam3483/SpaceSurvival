package maxfat.spacesurvival.overlap2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.Image9patchItem;
import com.uwsoft.editor.renderer.actor.LabelItem;
import com.uwsoft.editor.renderer.script.IScript;

public class SimpleButtonScript implements IScript {

	protected CompositeItem buttonHolder;

	protected boolean isDown = false;

	private final DelayedRemovalArray<ClickListener> listeners = new DelayedRemovalArray<ClickListener>(
			0);

	private Color changeColor(Color c, float percent) {
		float r = MathUtils.clamp(c.r + c.r * percent, 0, 1);
		float g = MathUtils.clamp(c.g + c.g * percent, 0, 1);
		float b = MathUtils.clamp(c.b + c.b * percent, 0, 1);
		return new Color(Color.rgba8888(r, g, b, 1));
	}

	@Override
	public void init(CompositeItem item) {
		this.buttonHolder = item;
		buttonHolder.setLayerVisibilty("pressed", false);

		String text = item.getCustomVariables().getStringVariable("text");
		final LabelItem labelItem = item.getLabelById("text");
		if (text != null && labelItem != null) {
			labelItem.setText(text);
			labelItem.setAlignment(Align.center);
		}

		final float smallFontScale = .75f;
		final float bigFontScale = 1.25f;
		float changeAmount = .4f;
		final Color normalColor = buttonHolder.getColor();
		final Color darkColor = changeColor(normalColor, -changeAmount);
		final Color brightColor = changeColor(normalColor, .6f);
		final Image9patchItem buttonBG = (Image9patchItem) item
				.getItemById("btnBG");
		if (buttonBG != null) {
			buttonBG.setColor(normalColor);
		}
		buttonHolder.addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				if (pointer == -1 && !isDown) {
					buttonBG.setColor(brightColor);
					labelItem.setFontScale(bigFontScale);
				}
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer,
					Actor toActor) {
				if (pointer == -1 && !isDown) {
					buttonBG.setColor(normalColor);
					labelItem.setFontScale(1f);
				}
			}
		});
		buttonHolder.addListener(new ClickListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				buttonBG.setColor(darkColor);
				labelItem.setFontScale(smallFontScale);
				isDown = true;

				for (int i = 0; i < listeners.size; i++)
					listeners.get(i).touchDown(event, x, y, pointer, button);

				return super.touchDown(event, x, y, pointer, button);
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (!this.isOver()) {
					buttonBG.setColor(normalColor);
					labelItem.setFontScale(1f);
				} else {
					buttonBG.setColor(brightColor);
					labelItem.setFontScale(bigFontScale);
				}
				isDown = false;

				for (int i = 0; i < listeners.size; i++)
					listeners.get(i).touchUp(event, x, y, pointer, button);

				super.touchUp(event, x, y, pointer, button);
			}
		});
	}

	@Override
	public void act(float delta) {
	}

	/**
	 * Add a listener to receive events like click
	 * 
	 * @see InputListener
	 * @see ClickListener
	 */
	public boolean addListener(ClickListener listener) {
		if (!listeners.contains(listener, true)) {
			listeners.add(listener);
			return true;
		}
		return false;
	}

	public boolean removeListener(ClickListener listener) {
		return listeners.removeValue(listener, true);
	}

	public Array<ClickListener> getListeners() {
		return listeners;
	}

	public void clearListeners() {
		listeners.clear();
	}

	@Override
	public void dispose() {
	}
}