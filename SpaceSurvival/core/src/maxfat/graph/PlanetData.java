package maxfat.graph;

import maxfat.spacesurvival.game.Civilization;
import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.gamesystem.PopulationComponent;

import com.badlogic.gdx.math.Vector2;

public class PlanetData implements I2DData {
	Civilization civilization;
	final Vector2 point;
	float minDistance;
	float size;

	float population;
	float maxPopulation;

	public PlanetData(Vector2 point, float minDistance, float size,
			Civilization civilization) {
		this.point = point;
		this.minDistance = minDistance;
		this.size = size;
		this.civilization = civilization;
	}

	public PlanetComponent getPlanetComponent() {
		return this.civilization.planetComp;
	}

	public PopulationComponent getPopulationComponent() {
		return this.civilization.popComponent;
	}

	public float getSize() {
		return this.size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public Vector2 getPoint() {
		return this.point;
	}

	public void setPoint(Vector2 point) {
		this.point.set(point);
	}

	public float getMinDistance() {
		return this.minDistance;
	}

	public void setMinDistance(float minDistance) {
		this.minDistance = minDistance;
	}
}