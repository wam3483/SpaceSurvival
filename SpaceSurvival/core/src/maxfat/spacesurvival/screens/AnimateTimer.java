package maxfat.spacesurvival.screens;

public class AnimateTimer<T> {
	T[] items;
	float frequency;

	public AnimateTimer(float frameFrequency, T[] items) {
		this.items = items;
		this.frequency = frameFrequency;
	}

	public int getItemIndex(float stateTime) {
		int frameIndex = (int) (stateTime / this.frequency);
		return frameIndex % this.items.length;
	}

	public T getAnimation(float stateTime) {
		return items[getItemIndex(stateTime)];
	}
}
