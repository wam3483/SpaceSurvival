package maxfat.spacesurvival.overlap2d;

import java.util.ArrayList;

import maxfat.spacesurvival.gamesystem.PlanetRestrictions;
import maxfat.spacesurvival.gamesystem.PopulationRestrictions;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.IBaseItem;
import com.uwsoft.editor.renderer.actor.ImageItem;
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

	float getProgressFor(CompositeItem item) {
		String strIcon = Overlap2DUtil.findCustomVariable("progressType", item);
		float progress = 0;
		if (strIcon.equalsIgnoreCase("FARMING")) {
			progress = PopulationRestrictions.FoodProduction
					.normalizeValue(this.attributes.farming);
		} else if (strIcon.equalsIgnoreCase("REPRODUCTION")) {
			progress = PopulationRestrictions.Reproduction
					.normalizeValue(this.attributes.reproduction);
		} else if (strIcon.equalsIgnoreCase("HARDINESS")) {
			progress = PopulationRestrictions.Hardiness
					.normalizeValue(this.attributes.hardiness);
		} else if (strIcon.equalsIgnoreCase("DANGER_LEVEL")) {
			// TODO threat level not being initialized. fix this.
			progress = PlanetRestrictions.DangerLevel.normalizeValue(5);// this.attributes.threat);
		} else {
			throw new RuntimeException(
					"Couldn't get population stat level, unknown attribute type.");
		}
		return progress;
	}

	Color getProgressBarColor(IBaseItem item) {
		String strIcon = Overlap2DUtil.findCustomVariable("progressType", item);
		if (strIcon.equalsIgnoreCase("DANGER_LEVEL")) {
			return Color.RED;
		}
		return Color.GREEN;
	}

	public static class Attributes {
		public int threat;
		public long amountGold;
		public long amountFood;
		public int farming;
		public int reproduction;
		public int hardiness;
	}

	private void insertProgressBar(CompositeItem item) {
		ArrayList<IBaseItem> allItems = item.getItems();
		ImageItem toReplace = (ImageItem) Overlap2DUtil.findItemByIdentifier(
				"progressBar", allItems);
		float maxWidth = toReplace.getWidth() * toReplace.getScaleX();
		TiledDrawable bar = new TiledDrawable(myAssets.getStartBar());
		ProgressBarActor actor = new ProgressBarActor(bar, maxWidth);
		Overlap2DUtil.replaceElement(toReplace, actor);
		actor.setColor(getProgressBarColor(item));
		actor.setProgress(0);
		float progress = getProgressFor(item);
		actor.setProgressAnimate(progress, 1, Interpolation.linear);
	}

	@Override
	public void init(CompositeItem item) {
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