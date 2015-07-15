package maxfat.spacesurvival.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public class GameLoadingScreen implements Screen {
	private final AssetManager assetManager;
	private final Game game;

	public GameLoadingScreen(AssetManager assetManager, Game game) {
		this.assetManager = assetManager;
		this.game = game;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		if (this.assetManager.update()) {
			SinglePlayerRandomGameSetup setup = new SinglePlayerRandomGameSetup();
			setup.generateGame();
			GameScreen gameScreen = new GameScreen(setup.getGameManager(), this.assetManager);
			this.game.setScreen(gameScreen);
		}
		float progress = this.assetManager.getProgress();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
