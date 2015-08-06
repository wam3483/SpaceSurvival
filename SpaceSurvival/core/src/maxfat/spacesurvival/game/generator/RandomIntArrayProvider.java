package maxfat.spacesurvival.game.generator;

import maxfat.util.random.IRandom;

public class RandomIntArrayProvider implements IIntProvider {
	private final int[] values;
	private final IRandom random;

	public RandomIntArrayProvider(int[] values, IRandom random) {
		this.values = values;
		this.random = random;
	}

	@Override
	public int getValue() {
		return values[random.nextInt(values.length)];
	}
}