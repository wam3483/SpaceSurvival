package maxfat.spacesurvival.overlap2d;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.LabelItem;
import com.uwsoft.editor.renderer.script.IScript;

public class PlanetStatBarScript implements IScript {

	private final AssetManager assetManager;

	public PlanetStatBarScript(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public TextureRegion getIconFor(String strIcon) {
		TextureAtlas atlas = this.assetManager.get("images/uiAtlas.atlas",
				TextureAtlas.class);
		String key = "";
		if (strIcon.equalsIgnoreCase("DANGER_LEVEL")) {
			key = "threatIcon";
		} else if (strIcon.equalsIgnoreCase("PLANET_GOLD")) {
			key = "coinIcon";
		} else if (strIcon.equalsIgnoreCase("FOOD")) {
			key = "foodIcon";
		} else if (strIcon.equalsIgnoreCase("FARMING")) {
			key = "farmingIcon";
		} else if (strIcon.equalsIgnoreCase("REPRODUCTION")) {
			key = "reproductionIcon";
		} else if (strIcon.equalsIgnoreCase("HARDINESS")) {
			key = "hardinessIcon";
		}
		return atlas.findRegion(key);
	}

	@Override
	public void init(CompositeItem item) {
		String title = item.getCustomVariables().getStringVariable("title");
		final LabelItem labelItem = item.getLabelById("lblTitle");
		labelItem.setText(title);

		String strIcon = item.getCustomVariables().getStringVariable("icon");
		TextureRegion region = getIconFor(strIcon);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void act(float delta) {
		// TODO Auto-generated method stub
	}
}