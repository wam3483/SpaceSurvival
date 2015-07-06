package maxfat.spacesurvival.overlap2d;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.LabelItem;

public class DialogBuilder {
	SceneLoader sceneLoader;
	Stage stage;

	public DialogBuilder(Stage stage) {
		this.sceneLoader = new SceneLoader();
		this.stage = stage;
	}

	public CompositeItem getExitDialog(final IYesNoListener listener) {
		sceneLoader.loadScene("YesNoDialog");
		final CompositeItem sceneRoot = sceneLoader.getRoot();
		SimpleButtonScript yesNoScript = new SimpleButtonScript();
		sceneRoot.addScriptTo("SimpleButton", yesNoScript);
		yesNoScript.addListener(new ClickListener() {
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				CompositeItem compButton = (CompositeItem) event
						.getListenerActor();
				LabelItem item = compButton.getLabelById("text");
				String text = item.getText().toString();
				if (text.equalsIgnoreCase("yes")) {
					listener.yesPressed(sceneRoot);
				} else if (text.equalsIgnoreCase("no")) {
					listener.noPressed(sceneRoot);
				}
			}
		});
		Vector3 pos = this.stage.getCamera().position;
		float x = (pos.x - sceneRoot.getWidth() / 2);
		float y = (pos.y - sceneRoot.getHeight() / 2);
		sceneRoot
				.setOrigin(sceneRoot.getWidth() / 2, sceneRoot.getHeight() / 2);
		sceneRoot.setPosition(x, y);
		sceneRoot.setScale(.1f, .1f);
		sceneRoot.addAction(Actions.scaleTo(1, 1, 1, Interpolation.swingOut));
		return sceneRoot;
	}

	public interface IYesNoListener {
		void yesPressed(CompositeItem dialog);

		void noPressed(CompositeItem dialog);
	}
}
