package maxfat.spacesurvival.overlap2d;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class StageStack {
	private final Stack<StageWrapper> stack;
	private StageWrapper currentWrapper;

	public StageStack() {
		this.stack = new Stack<StageWrapper>();
	}

	public void render(float delta) {
		if (this.currentWrapper.stage != null) {
			Stage stage = this.currentWrapper.stage;
			stage.act(delta);
			stage.draw();
		}
	}

	public void push(Stage stage) {
		this.push(new StageWrapper(stage, stage));
	}

	public void push(StageWrapper inputProcessor) {
		this.stack.add(this.currentWrapper);
		this.currentWrapper = inputProcessor;
		Gdx.input.setInputProcessor(this.currentWrapper.inputProcessor);
	}

	public void resize(int width, int height) {
		for (StageWrapper s : this.stack) {
			s.stage.getViewport().update(width, height);
		}
	}

	public void pop() {
		StageWrapper p = this.stack.pop();
		Gdx.input.setInputProcessor(p.inputProcessor);
		this.currentWrapper = p;
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
}