package maxfat.spacesurvival.gamesystem;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PlanetComponent extends Component {

	public String name;
	private final Vector2 position;

	public float scanTime = 1;
	public float population;
	public float farmingPopulation;

	public float naturalDeathPercent;
	public long amountGold;
	public long amountFood;
	
	public float percentWater;
	public float temperatureCelsius;

	public PlanetComponent(Vector2 position) {
		this.position = position;
	}

	public Vector2 getPosition() {
		return this.position;
	}

	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}

	public void setPosition(Vector2 position) {
		this.setPosition(position.x, position.y);
	}

	public float getIdlePopulation() {
		return this.population - this.farmingPopulation;
	}

	public boolean canSupportLife() {
		return true;
	}

	public float getNaturalDeathPercent() {
		return this.naturalDeathPercent;
	}

	public void setFarmingPopulation(float farmingPop) {
		this.farmingPopulation = farmingPop;
	}

	public void addPeople(float people) {
		this.population += people;
	}
}