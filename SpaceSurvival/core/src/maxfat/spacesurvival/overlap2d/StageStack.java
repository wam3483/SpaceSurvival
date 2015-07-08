package maxfat.spacesurvival.overlap2d;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class StageStack {
	private final Stack<StageWrapper> stack;

	public StageStack() {
		this.stack = new Stack<StageWrapper>();
	}

	public void render(float delta) {
		StageWrapper wrapper = this.stack.peek();
		if (wrapper != null && wrapper.stage != null) {
			Stage stage = wrapper.stage;
			stage.act(delta);
			stage.draw();
		}
	}

	public void push(Stage stage) {
		if (stage == null)
			throw new NullPointerException("stage");
		this.push(new StageWrapper(stage, stage));
	}

	public void push(StageWrapper stageWrapper) {
		if (stageWrapper == null)
			throw new NullPointerException("stageWrapper");
		this.stack.push(stageWrapper);
		Gdx.input.setInputProcessor(stageWrapper.inputProcessor);
	}

	public void resize(int width, int height) {
		for (StageWrapper s : this.stack) {
			if (s.stage != null) {
				s.stage.getViewport().update(width, height);
			}
		}
	}

	public void pop() {
		this.stack.pop();
		StageWrapper next = this.stack.peek();
		if (next != null) {
			Gdx.input.setInputProcessor(next.inputProcessor);
		} else {
			Gdx.input.setInputProcessor(null);
		}
	}

	public static class StageWrapper {
		public Stage stage;
		// might want an InputMultiplexer a stage AND other processors can
		// handle input.
		public InputProcessor inputProcessor;

		public StageWrapper(Stage stage, InputProcessor processor) {
			this.stage = stage;
			this.inputProcessor = processor;
		}
	}

	public void dispose() {
		for (StageWrapper wrapper : this.stack) {
			if (wrapper.stage != null) {
				wrapper.stage.dispose();
			}
		}
		this.stack.clear();
	}
}