package maxfat.spacesurvival.overlap2d;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
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
	private final StageStack displayStack;
	private final Viewport viewport;
	private final Batch batch;
	private final SceneLoader sceneLoader;

	float dialogAnimationSpeed = .5f;

	public GameDialogs(StageStack stack, Viewport viewport, Batch batch) {
		this.displayStack = stack;
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

	private Action getDialogCloseAnimation() {
		return Actions.sequence(//
				Actions.scaleTo(1, 1),//
				Actions.scaleTo(.1f, .1f, dialogAnimationSpeed,
						Interpolation.swingIn));
	}

	// TODO need some planet data to render in correct position
	public void showUnknownPlanetDialog(final Runnable scanPlanetCallback,
			final ICallback<IDialog> capturePlanetCallback) {
		this.sceneLoader.loadScene("UnknownPlanetDialog");
		final CompositeItem root = sceneLoader.getRoot();
		ArrayList<IBaseItem> allItems = root.getItems();

		final Stage stage = new Stage(this.viewport, this.batch);

		CompositeItem planetPlaceholder = (CompositeItem) findItemByIdentifier(
				"planetPlaceholder", allItems);
		float x = planetPlaceholder.getX();
		float y = planetPlaceholder.getY();
		int zIndex = planetPlaceholder.getZIndex();
		float width = planetPlaceholder.getWidth();
		float height = planetPlaceholder.getHeight();
		Group group = planetPlaceholder.getParent();
		group.removeActor(planetPlaceholder);

		Actor planetRenderer = new Actor();
		planetRenderer.setBounds(x, y, width, height);
		planetRenderer.setZIndex(zIndex);
		group.addActor(planetRenderer);

		CompositeItem btnScan = (CompositeItem) findItemByIdentifier("btnScan",
				allItems);
		Action closeAnimation = getDialogCloseAnimation();
		Action scanAction = Actions.sequence(closeAnimation,
				new DialogDisposeAndCallback(stage, scanPlanetCallback));
		btnScan.addListener(new CloseDialogClickListener(root, scanAction));

		CompositeItem btnCapture = (CompositeItem) findItemByIdentifier(
				"btnCapturePlanet", allItems);
		btnCapture.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				Action closeAction = new DialogDisposeAndCallback(stage, null);
				capturePlanetCallback.callback(new CompositeItemDialog(root,
						closeAction));
			}
		});

		pushDialog(stage, root);
	}

	private void pushDialog(Stage stage, CompositeItem root) {
		centerItemInStage(stage, root);
		this.addInAnimation(root);
		stage.addActor(root);

		this.displayStack.push(stage);
	}

	public void showYesNoDialog(String message, String yes, String no,
			final Runnable yesRun, final Runnable noRun) {
		sceneLoader.loadScene("YesNoDialog");
		final CompositeItem root = sceneLoader.getRoot();
		final Stage stage = new Stage(this.viewport, this.batch);
		SimpleButtonScript yesNoScript = new SimpleButtonScript();
		root.addScriptTo("SimpleButton", yesNoScript);

		root.setOrigin(root.getWidth() / 2, root.getHeight() / 2);
		Action closeAnimation = Actions
				.sequence(Actions.scaleTo(1, 1), Actions.scaleTo(.1f, .1f,
						dialogAnimationSpeed, Interpolation.swingIn));

		// animate dialog, dispose stage, then do callback for button press
		Action yesCloseAction = Actions.sequence(closeAnimation,
				new DialogDisposeAndCallback(stage, yesRun));
		Action noCloseAction = Actions.sequence(closeAnimation,
				new DialogDisposeAndCallback(stage, noRun));

		ArrayList<IBaseItem> allItems = root.getItems();
		CompositeItem btnYes = (CompositeItem) findItemByIdentifier("btnYes",
				allItems);
		btnYes.addListener(new CloseDialogClickListener(root, yesCloseAction));

		CompositeItem btnNo = (CompositeItem) findItemByIdentifier("btnNo",
				allItems);
		btnNo.addListener(new CloseDialogClickListener(root, noCloseAction));

		LabelItem lblYes = btnYes.getLabelById("text");
		lblYes.setText(yes);
		LabelItem lblNo = btnNo.getLabelById("text");
		lblNo.setText(no);
		LabelItem lblMessage = (LabelItem) findItemByIdentifier("lblMessage",
				allItems);
		lblMessage.setText(message);

		pushDialog(stage, root);
	}

	public void render(float delta) {
		this.displayStack.render(delta);
	}

	public void update(int width, int height) {
		this.displayStack.resize(width, height);
	}

	public void dispose() {
		this.displayStack.dispose();
	}

	private static class CloseDialogClickListener extends ClickListener {
		private CompositeItem dialog;
		private Action closeAction;

		public CloseDialogClickListener(CompositeItem dialog, Action closeAction) {
			this.dialog = dialog;
			this.closeAction = closeAction;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			this.dialog.clearActions();
			this.dialog.addAction(closeAction);
		}
	};

	public class CompositeItemDialog implements IDialog {
		CompositeItem dialog;
		Action closeAction;

		public CompositeItemDialog(CompositeItem dialog, Action closeAction) {
			this.dialog = dialog;
			this.closeAction = closeAction;
		}

		public void close() {
			this.dialog.addAction(closeAction);
		}
	}

	public static interface IDialog {
		void close();
	}

	private class DialogDisposeAndCallback extends Action {
		Stage stage;
		Runnable callback;

		public DialogDisposeAndCallback(Stage stage, Runnable callback) {
			this.stage = stage;
			this.callback = callback;
		}

		@Override
		public boolean act(float delta) {
			displayStack.pop();
			stage.dispose();
			if (this.callback != null)
				this.callback.run();
			return true;
		}
	}
}