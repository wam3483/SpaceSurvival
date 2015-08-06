package maxfat.spacesurvival.game.generator;

import maxfat.graph.IntRange;
import maxfat.util.random.IRandom;
import maxfat.util.random.RandomUtil;

public class RandomIntProvider implements IIntProvider {

	private final IntRange range;
	private final IRandom random;

	public RandomIntProvider(IRandom random, IntRange range) {
		this.random = random;
		this.range = range;
	}

	public int getValue() {
		return RandomUtil.randomBetweenRanges(this.random, this.range.getMin(),
				range.getMax());
	}
}