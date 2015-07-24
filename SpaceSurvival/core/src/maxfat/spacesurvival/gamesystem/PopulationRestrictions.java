package maxfat.spacesurvival.gamesystem;

import maxfat.graph.FloatRange;
import maxfat.graph.IntRange;

public class PopulationRestrictions {
	public static final IntRange Hardiness = new IntRange(0, 10);
	public static final IntRange Reproduction = new IntRange(1, 10);
	public static final IntRange FoodProduction = new IntRange(1, 10);
	public static final IntRange GoldProduction = new IntRange(1, 10);

	public static final FloatRange StarveChance = new FloatRange(.1f, 1f);

}
