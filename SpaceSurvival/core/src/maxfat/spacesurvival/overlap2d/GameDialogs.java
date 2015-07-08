package maxfat.spacesurvival.overlap2d;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.IBaseItem;
import com.uwsoft.editor.renderer.actor.LabelItem;

public class GameDialogs {
	private final StageStack stack;
	private final Viewport viewport;
	private final Batch batch;
	private final SceneLoader sceneLoader;

	float dialogAnimationSpeed = .5f;

	public GameDialogs(StageStack stack, Viewport viewport, Batch batch) {
		this.stack = stack;
		this.viewport = viewport;
		this.batch = batch;
		this.sceneLoader = new SceneLoader();
	}

	private IBaseItem findItemByIdentifier(String id, ArrayList<IBaseItem> items) {
		for (IBaseItem item : items) {
			if (item.getDataVO().itemIdentifier.equals(id)) {
				return item;
			}
			if (item.isComposite()) {
				CompositeItem cmp = (CompositeItem) item;
				IBaseItem recursedItem = findItemByIdentifier(id,
						cmp.getItems());
				if (recursedItem != null)
					return recursedItem;
			}
		}
		return null;
	}

	private void centerItemInStage(Stage stage, CompositeItem item) {
		Vector3 pos = stage.getCamera().position;
		float x = (pos.x - item.getWidth() / 2);
		float y = (pos.y - item.getHeight() / 2);
		item.setPosition(x, y);
	}

	public void showYesNoDialog(String message, Runnable yesRun, Runnable noRun) {
		this.showYesNoDialog(message, "Yes", "No", yesRun, noRun);
	}

	private void addInAnimation(CompositeItem root) {
		root.setOrigin(root.getWidth() / 2, root.getHeight() / 2);
		root.setScale(.1f, .1f);
		root.addAction(Actions.scaleTo(1, 1, dialogAnimationSpeed,
				Interpolation.swingOut));
	}

	private void addOutAnimation(CompositeItem root, Action after1,
			Action after2) {
		root.setOrigin(root.getWidth() / 2, root.getHeight() / 2);
		root.setScale(1, 1);
		root.addAction(Actions.sequence(Actions.scaleTo(.1f, .1f,
				dialogAnimationSpeed, Interpolation.swingIn), after1, after2));
	}

	public void showYesNoDialog(String message, String yes, String no,
			final Runnable yesRun, final Runnable noRun) {
		sceneLoader.loadScene("YesNoDialog");
		final CompositeItem root = sceneLoader.getRoot();
		final Stage stage = new Stage(this.viewport, this.batch);
		SimpleButtonScript yesNoScript = new SimpleButtonScript();
		root.addScriptTo("SimpleButton", yesNoScript);

		ArrayList<IBaseItem> allItems = root.getItems();
		CompositeItem btnYes = (CompositeItem) findItemByIdentifier("btnYes",
				allItems);

		// Must be last action executed because it removes stage from stack,
		// Stage then can no longer be updated and can't execute further
		// actions.
		final Action lastAction = new Action() {
			@Override
			public boolean act(float delta) {
				stack.pop();
				stage.dispose();
				return true;
			}
		};
		btnYes.addListener(new ClickListener() {
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				root.clearActions();
				addOutAnimation(root, new RunAction(yesRun), lastAction);
			}
		});
		LabelItem lblYes = btnYes.getLabelById("text");
		lblYes.setText(yes);

		CompositeItem btnNo = (CompositeItem) findItemByIdentifier("btnNo",
				allItems);
		btnNo.addListener(new ClickListener() {
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				root.clearActions();
				addOutAnimation(root, new RunAction(noRun), lastAction);
			}
		});
		LabelItem lblNo = btnNo.getLabelById("text");
		lblNo.setText(no);

		LabelItem lblMessage = (LabelItem) findItemByIdentifier("lblMessage",
				allItems);
		lblMessage.setText(message);

		centerItemInStage(stage, root);

		this.addInAnimation(root);
		stage.addActor(root);
		this.stack.push(stage);
	}

	public void render(float delta) {
		this.stack.render(delta);
	}

	public void update(int width, int height) {
		this.stack.resize(width, height);
	}

	public void dispose() {
		this.stack.dispose();
	}

	private class RunAction extends Action {
		Runnable run;

		public RunAction(Runnable run) {
			this.run = run;
		}

		@Override
		public boolean act(float delta) {
			if (this.run != null)
				this.run.run();
			return true;
		}
	}
}