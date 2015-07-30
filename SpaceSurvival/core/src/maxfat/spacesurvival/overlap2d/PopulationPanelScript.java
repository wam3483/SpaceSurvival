package maxfat.spacesurvival.overlap2d;

import java.util.ArrayList;

import maxfat.spacesurvival.overlap2d.PlanetStatBarScript.Attributes;
import maxfat.spacesurvival.rendersystem.TextureActor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.IBaseItem;
import com.uwsoft.editor.renderer.actor.ImageItem;
import com.uwsoft.editor.renderer.actor.LabelItem;
import com.uwsoft.editor.renderer.script.IScript;

public class PopulationPanelScript implements IScript {

	private final AssetManager assetManager;
	private final MyAssetManager myAssets;
	private final Attributes attributes;

	public PopulationPanelScript(AssetManager assetManager,
			Attributes attributes) {
		this.assetManager = assetManager;
		this.myAssets = new MyAssetManager(this.assetManager);
		this.attributes = attributes;
	}

	private void insertIcon(CompositeItem item) {
		String strIcon = item.getCustomVariables().getStringVariable("icon");
		TextureRegion region = getIconFor(strIcon);
		ArrayList<IBaseItem> allItems = item.getItems();
		ImageItem toReplace = (ImageItem) Overlap2DUtil.findItemByIdentifier(
				"icon", allItems);
		TextureActor actor = new TextureActor(region);
		actor.setColor(Color.GREEN);
		Overlap2DUtil.replaceElement(toReplace, actor);
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
		TextureRegion result = atlas.findRegion(key);
		if (result == null)
			result = atlas.findRegion("hardinessIcon");
		return result;
	}

	@Override
	public void init(CompositeItem item) {
		String title = item.getCustomVariables().getStringVariable("title");
		LabelItem labelItem = item.getLabelById("lblTitle");
		labelItem.setText(title);
		this.insertIcon(item);
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
