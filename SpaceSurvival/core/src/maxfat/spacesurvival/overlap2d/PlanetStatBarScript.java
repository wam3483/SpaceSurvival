package maxfat.spacesurvival.overlap2d;

import java.util.ArrayList;

import maxfat.spacesurvival.rendersystem.TextureActor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.IBaseItem;
import com.uwsoft.editor.renderer.actor.ImageItem;
import com.uwsoft.editor.renderer.actor.LabelItem;
import com.uwsoft.editor.renderer.script.IScript;

public class PlanetStatBarScript implements IScript {

	private final AssetManager assetManager;
	private final MyAssetManager myAssets;
	private final Attributes attributes;

	public PlanetStatBarScript(AssetManager assetManager, Attributes attributes) {
		this.assetManager = assetManager;
		this.myAssets = new MyAssetManager(this.assetManager);
		this.attributes = attributes;
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
	
	float getProgressFor(CompositeItem item){
		String strIcon = item.getCustomVariables().getStringVariable("icon");
		float progress = 0;
		if (strIcon.equalsIgnoreCase("DANGER_LEVEL")) {
			progress = this.attributes.threat;
		} else if (strIcon.equalsIgnoreCase("PLANET_GOLD")) {
			progress = this.attributes.coin;
		} else if (strIcon.equalsIgnoreCase("FOOD")) {
			progress = this.attributes.food;
		} else if (strIcon.equalsIgnoreCase("FARMING")) {
			progress = this.attributes.farming;
		} else if (strIcon.equalsIgnoreCase("REPRODUCTION")) {
			progress = this.attributes.reproduction;
		} else if (strIcon.equalsIgnoreCase("HARDINESS")) {
			progress = this.attributes.hardiness;
		}
		return progress;
	}

	public static class Attributes {
		public float threat;
		public float coin;
		public float food;
		public float farming;
		public float reproduction;
		public float hardiness;
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

	private void insertProgressBar(CompositeItem item) {
		ArrayList<IBaseItem> allItems = item.getItems();
		ImageItem toReplace = (ImageItem) Overlap2DUtil.findItemByIdentifier(
				"progressBar", allItems);

		float maxWidth = toReplace.getWidth() * toReplace.getScaleX();
		TiledDrawable bar = new TiledDrawable(myAssets.getStartBar());
		ProgressBarActor actor = new ProgressBarActor(bar,
				new TextureRegionDrawable(myAssets.getEndCap()), maxWidth);
		Overlap2DUtil.replaceElement(toReplace, actor);
		actor.setColor(Color.GREEN);
		actor.setProgress(0);
		float progress = getProgressFor(item);
		actor.setProgressAnimate(progress, 1, Interpolation.linear);
	}

	@Override
	public void init(CompositeItem item) {
		String title = item.getCustomVariables().getStringVariable("title");
		LabelItem labelItem = item.getLabelById("lblTitle");
		labelItem.setText(title);

		this.insertIcon(item);
		this.insertProgressBar(item);
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