package maxfat.spacesurvival.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SpaceSurvivalGame extends Game {

	public SpaceSurvivalGame() {
	}

	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);
		this.loadSinglePlayerInfinitePlay();
	}

	private void loadSinglePlayerInfinitePlay() {
		FileHandleResolver resolver = new LocalFileHandleResolver();
		AssetManager assetManager = new AssetManager(resolver);
		assetManager.load("images/scanProgress.atlas", TextureAtlas.class);
		assetManager.load("images/uiAtlas.atlas", TextureAtlas.class);
		final SinglePlayerRandomGameSetup setup = new SinglePlayerRandomGameSetup();
		final GameLoadingScreen screen = new GameLoadingScreen(assetManager,
				setup, new GameLoadingScreen.IGameLoadedListener() {
					@Override
					public void onGameLoaded(AssetManager assetManager) {
						GameScreen gameScreen = new GameScreen(
								setup.getGameManager(), assetManager);
						Screen oldScreen = getScreen();
						setScreen(gameScreen);
						oldScreen.dispose();
					}
				});
		this.setScreen(screen);
	}
}