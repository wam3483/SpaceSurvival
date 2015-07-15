package maxfat.spacesurvival.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SpaceSurvivalGame extends Game {

	public SpaceSurvivalGame() {
	}

	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);

		AssetManager assetManager = new AssetManager(
				new LocalFileHandleResolver());
		assetManager.load("scanProgress.atlas", TextureAtlas.class);
		GameLoadingScreen screen = new GameLoadingScreen(assetManager, this);
		this.setScreen(screen);
	}
}