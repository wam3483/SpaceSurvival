package maxfat.spacesurvival.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PinchToZoomController {
	private final FingerPointer finger1;
	private final FingerPointer finger2;
	private int numFingers = 0;
	private float initialDistance = 0;
	float deltaZoom = 0;

	public PinchToZoomController() {
		this.finger1 = new FingerPointer();
		this.finger2 = new FingerPointer();
	}

	private void initFingers(int pointer) {
		numFingers = MathUtils.clamp(numFingers + 1, 0, 2);
		// initialize fingers.
		if (!finger1.isDefined() && numFingers == 1) {
			finger1.pointer = pointer;
		} else if (!finger2.isDefined() && numFingers == 2) {
			finger2.pointer = pointer;
		}
		if (finger1.isDefined() && finger2.isDefined()) {
			initialDistance = finger1.position.dst(finger2.position);
		}
	}

	public void fingerInput(int screenX, int screenY, int pointer) {
		if (pointer != finger1.pointer && pointer != finger2.pointer) {
			initFingers(pointer);
		}

		// updating fingers.
		boolean wasChange = false;
		if (pointer == finger1.pointer) {
			wasChange = finger1.update(screenX, screenY, pointer);
		} else if (pointer == finger2.pointer) {
			wasChange = finger2.update(screenX, screenY, pointer);
		}

		// zoom if both fingers down.
		if (wasChange && finger1.isDefined() && finger2.isDefined()) {
			zoom();
		}
	}

	public boolean isActive() {
		return finger1.isDefined() && finger2.isDefined();
	}

	private void zoom() {
		float currentDistance = finger1.position.dst(finger2.position);
		deltaZoom = currentDistance / initialDistance - 1;
		deltaZoom *= -1;
		initialDistance = currentDistance;
	}

	public float getLastZoomChange() {
		return this.deltaZoom;
	}

	public void removeFinger(int pointer) {
		numFingers = MathUtils.clamp(numFingers - 1, 0, 2);
		if (finger1.pointer == pointer) {
			this.finger1.clear();
		} else if (finger2.pointer == pointer) {
			this.finger2.clear();
		}
	}

	public void clear() {
		finger1.clear();
		finger2.clear();
		numFingers = 0;
		initialDistance = 0;
	}

	private static class FingerPointer {
		public final Vector2 position = new Vector2();
		public int pointer = -1;

		public boolean isDefined() {
			return this.pointer != -1;
		}

		public boolean update(int x, int y, int pointer) {
			boolean change = false;
			change = pointer != this.pointer || this.position.x != x
					|| this.position.y != y;
			this.pointer = pointer;
			this.position.set(x, y);
			return change;
		}

		public void clear() {
			this.position.set(0, 0);
			this.pointer = -1;
		}

		public String toString() {
			return "[" + position + " " + pointer + "]";
		}
	}
}