package maxfat.graph;

import com.badlogic.gdx.math.MathUtils;

public class IntRange {
	private final int min;
	private final int max;

	public IntRange(int min, int max) {
		if (max < min)
			throw new IllegalArgumentException("max must be greater than min");
		this.min = min;
		this.max = max;
	}

	public int range() {
		return this.max - this.min;
	}

	public float normalizeValue(int value) {
		value = clamp(value);
		return (value - min) / (float) max;
	}

	public int clamp(int value) {
		return MathUtils.clamp(value, min, max);
	}

	public int getMin() {
		return this.min;
	}

	public int getMax() {
		return this.max;
	}
}
