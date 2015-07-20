package maxfat.spacesurvival.overlap2d;

import java.util.ArrayList;

import maxfat.spacesurvival.rendersystem.TextureActor;
import maxfat.util.random.RandomUtil;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.IBaseItem;
import com.uwsoft.editor.renderer.actor.ImageItem;
import com.uwsoft.editor.renderer.actor.LabelItem;

public class GameUIManager {
	private final StageStack displayStack;
	private final Viewport viewport;
	private final Batch batch;
	private final SceneLoader sceneLoader;
	private final ShapeRenderer shapeRenderer;
	private final AssetManager assetManager;
	float dialogAnimationSpeed = .5f;

	public GameUIManager(StageStack stack, Viewport viewport, Batch batch,
			AssetManager assetManager) {
		this.displayStack = stack;
		this.viewport = viewport;
		this.batch = batch;
		this.assetManager = assetManager;
		CustomResourceManager resourceManager = new CustomResourceManager();
		resourceManager.initAllResources();
		this.sceneLoader = new SceneLoader(resourceManager);
		this.shapeRenderer = new ShapeRenderer();
	}

	public Array<Actor> getPlanetScanActors(Vector2 center) {
		TextureAtlas atlas = assetManager.get("scanProgress.atlas",
				TextureAtlas.class);
		Array<AtlasRegion> ary = atlas.getRegions();
		Array<Actor> actors = new Array<Actor>();
		float scaleMultiplier = .5f;
		float initialAnimationDuration = scaleMultiplier * ary.size;
		float baseAlpha = .7f;
		for (int i = 0; i < ary.size; i++) {
			TextureRegion region = ary.get(i);
			Actor a = new TextureActor(region);
			a.setColor(new Color(Color.rgba8888(.25f, .5f, .66f, baseAlpha)));
			a.setPosition(center.x - region.getRegionWidth() / 2, center.y
					- region.getRegionHeight() / 2);
			float rotateSpeed = 10 * (i + 1);
			float initialRotation = (float) RandomUtil.randomBetweenRanges(0,
					360);
			a.setRotation(initialRotation);
			a.setScale(0);
			Action extraAction = null;
			if (i == 0) {
				Action scaleUp = Actions.parallel(
						Actions.alpha(baseAlpha / 2, .5f),
						Actions.scaleBy(1, 1, .5f, Interpolation.swingOut));
				Action scaleDown = Actions.parallel(
						Actions.alpha(baseAlpha, 1f),
						Actions.scaleTo(1, 1, 1f, Interpolation.linear));
				extraAction = Actions.sequence(Actions
						.delay(initialAnimationDuration), Actions
						.forever(Actions.sequence(Actions.delay(5), scaleUp,
								scaleDown)));
			} else {
				extraAction = Actions.delay(0);
			}
			float scaleDelay = i * scaleMultiplier;
			a.addAction(Actions.sequence(
					Actions.delay(scaleDelay),
					Actions.scaleTo(1, 1, .5f, new Interpolation.SwingOut(10)),
					Actions.parallel(extraAction,
							Actions.forever(Actions.rotateBy(rotateSpeed, 1)))));
			actors.add(a);
		}
		return actors;
	}

	public Actor getPlanetExclaimationIcon() {
		this.sceneLoader.loadScene("PlanetNewInfo");
		CompositeItem root = sceneLoader.getRoot();
		// root.setTransform(false);
		root.setScale(0, 0);
		root.addAction(Actions.scaleTo(1, 1, .5f, Interpolation.swingOut));
		root.setOrigin(root.getWidth() / 2, root.getHeight() / 2);
		return root;
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

	private Vector2 getCenterOfActor(Actor a) {
		return new Vector2(a.getX() + a.getWidth() / 2 * a.getScaleX(),
				a.getY() + a.getHeight() / 2 * a.getScaleY());
	}

	private void createPlanetFrameAction(Actor frameActor, Actor centerActor,
			float delay, float moveDistance) {
		float planetFrameAlpha = .9f;

		Vector2 originalPos = new Vector2(frameActor.getX(), frameActor.getY());
		Vector2 centerOfFrame = getCenterOfActor(frameActor);
		Vector2 moveBy = getCenterOfActor(centerActor);
		moveBy.sub(centerOfFrame).nor().scl(moveDistance);

		Color c = frameActor.getColor();
		frameActor.setColor(c.r, c.g, c.b, 0f);
		Action moveAndFadeIn = Actions.parallel(Actions.alpha(planetFrameAlpha,
				2, Interpolation.elastic), Actions.moveTo(originalPos.x,
				originalPos.y, 2, Interpolation.exp10));
		Action action = Actions.sequence(Actions.delay(delay),
				Actions.moveBy(moveBy.x, moveBy.y), moveAndFadeIn);
		frameActor.addAction(action);
	}

	public void showUnknownPlanetDialog(Entity planetEntity,
			final Runnable scanPlanetCallback,
			final ICallback<IDialog> capturePlanetCallback) {
		this.sceneLoader.loadScene("PlanetManagementDialog");
		final CompositeItem root = sceneLoader.getRoot();
		SimpleButtonScript yesNoScript = new SimpleButtonScript();
		root.addScriptTo("SimpleButton", yesNoScript);
		ArrayList<IBaseItem> allItems = root.getItems();

		ImageItem planetPlaceholder = (ImageItem) findItemByIdentifier(
				"planetPlaceholder", allItems);
		float x = planetPlaceholder.getX();
		float y = planetPlaceholder.getY();
		int zIndex = planetPlaceholder.getZIndex();
		float width = planetPlaceholder.getWidth()
				* planetPlaceholder.getScaleX();
		float height = planetPlaceholder.getHeight()
				* planetPlaceholder.getScaleY();
		Group group = planetPlaceholder.getParent();
		group.removeActor(planetPlaceholder);

		PlanetTileActor planetRenderer = new PlanetTileActor(planetEntity,
				this.shapeRenderer);
		group.addActor(planetRenderer);
		planetRenderer.setBounds(x, y, width, height);
		planetRenderer.setZIndex(zIndex);

		// fade in green corners around planet.
		float planetFrameMoveDist = 50;
		float planetFrameDelay = dialogAnimationSpeed / 2;

		final Stage stage = new Stage(this.viewport, this.batch);
		ImageItem topRightDisplay = (ImageItem) findItemByIdentifier(
				"topRightDisplay", allItems);
		createPlanetFrameAction(topRightDisplay, planetRenderer,
				planetFrameDelay, planetFrameMoveDist);

		ImageItem bottomLeftDisplay = (ImageItem) findItemByIdentifier(
				"bottomLeftDisplay", allItems);
		createPlanetFrameAction(bottomLeftDisplay, planetRenderer,
				planetFrameDelay, planetFrameMoveDist);

		// set up button animations and listeners
		CompositeItem btnClose = (CompositeItem) findItemByIdentifier(
				"btnClose", allItems);
		Action btnCloseAction = Actions.sequence(getDialogCloseAnimation(),
				new DialogDisposeAndCallback(stage, null));
		btnClose.addListener(new CloseDialogClickListener(root, btnCloseAction));

		CompositeItem btnScan = (CompositeItem) findItemByIdentifier(
				"btnScanPlanet", allItems);
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
		this.shapeRenderer.dispose();
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