package maxfat.spacesurvival.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public class GameLoadingScreen implements Screen {
	private final AssetManager assetManager;
	private final IGameLoadedListener listener;
	private final SinglePlayerRandomGameSetup setup;

	public GameLoadingScreen(AssetManager assetManager,
			SinglePlayerRandomGameSetup setup, IGameLoadedListener listener) {
		this.assetManager = assetManager;
		this.setup = setup;
		this.listener = listener;
	}

	@Override
	public void render(float delta) {
		if (this.assetManager.update()) {
			this.setup.generateGame();
			this.listener.onGameLoaded(this.assetManager);
		}
		float progress = this.assetManager.getProgress();
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}

	public interface IGameLoadedListener {
		void onGameLoaded(AssetManager assetManager);
	}
}
