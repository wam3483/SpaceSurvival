package maxfat.spacesurvival.screens;

import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.rendersystem.RadiusComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Fires events for input related to non-main screen game events like touching a
 * planet, or ship.
 * 
 * @author wmorrison
 *
 */
public class GameScreenInputManager extends InputMultiplexer {
	IGameUIListener listener = NullListener;
	private final Stage stage;
	private final Viewport gameViewport;

	public GameScreenInputManager(Viewport gameViewport) {
		this.stage = new Stage(gameViewport);
		this.addProcessor(this.stage);
		this.gameViewport = gameViewport;
		this.addProcessor(this.inputProcessor);
	}

	public void addPlanetActor(final Actor a) {
		this.stage.addActor(a);
		a.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Entity planetEntity = (Entity) a.getUserObject();
				listener.onPlanetClicked(planetEntity);
			}
		});
	}

	PinchToZoomController pinchToZoom = new PinchToZoomController();
	InputProcessor inputProcessor = new InputProcessor() {
		double mouseScrollSpeed = .2;
		private int startDragPointer;
		int startX;
		int startY;

		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
				listener.onExitRequested();
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer,
				int button) {
			pinchToZoom.fingerInput(screenX, screenY, pointer);
			this.startDragPointer = pointer;
			startX = screenX;
			startY = screenY;
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			pinchToZoom.removeFinger(pointer);
			this.startDragPointer = -1;
			return false;
		}

		final Vector3 zero = new Vector3();
		final Vector3 temp = new Vector3();
		final Vector2 dragVector = new Vector2();

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			pinchToZoom.fingerInput(screenX, screenY, pointer);
			if (pinchToZoom.isActive()) {
				float deltaZoom = pinchToZoom.getLastZoomChange();
				listener.zoom(deltaZoom);
			} else if (pointer == startDragPointer) {
				int diffX = screenX - startX;
				int diffY = screenY - startY;
				startX = screenX;
				startY = screenY;
				zero.setZero();
				temp.set(diffX, diffY, 0);

				OrthographicCamera camera = (OrthographicCamera) gameViewport
						.getCamera();
				Vector3 v1 = camera.unproject(zero);
				Vector3 v2 = camera.unproject(temp);
				v1.sub(v2);
				dragVector.set(v1.x, v1.y);
				listener.viewportDrag(dragVector);
				return true;
			}
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			float scale = (float) mouseScrollSpeed * amount;
			listener.zoom(scale);
			return true;
		}
	};

	public void pause() {
		this.pinchToZoom.clear();
	}

	public void update(float time) {
		this.stage.act(time);
	}

	public void dispose() {
		this.stage.dispose();
	}

	public interface IGameUIListener {
		void onPlanetClicked(Entity planetEntity);

		void viewportDrag(Vector2 v);

		void zoom(float increment);

		void onExitRequested();
	}

	private static final IGameUIListener NullListener = new IGameUIListener() {
		@Override
		public void onPlanetClicked(Entity p) {
		}

		@Override
		public void viewportDrag(Vector2 v) {
		}

		@Override
		public void zoom(float increment) {
		}

		@Override
		public void onExitRequested() {
		}
	};
}
