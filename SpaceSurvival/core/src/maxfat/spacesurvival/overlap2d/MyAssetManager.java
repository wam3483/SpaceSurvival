package maxfat.spacesurvival.overlap2d;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyAssetManager {
	private final AssetManager assetManager;
	TextureAtlas uiAtlas;

	public MyAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
		uiAtlas = assetManager.get("images/uiAtlas.atlas", TextureAtlas.class);
	}

	public TextureRegion getStartBar() {
		return uiAtlas.findRegion("progressBar");
	}

	public TextureRegion getEndCap() {
		return uiAtlas.findRegion("stripeEndCap");
	}
}
