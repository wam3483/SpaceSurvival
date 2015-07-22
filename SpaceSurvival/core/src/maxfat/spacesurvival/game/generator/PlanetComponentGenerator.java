package maxfat.spacesurvival.game.generator;

import maxfat.spacesurvival.gamesystem.PlanetComponent;

import com.badlogic.gdx.math.Vector2;

public class PlanetComponentGenerator {
	private final PlanetParams params;
	private final PlanetNameService nameProvider;

	public PlanetComponentGenerator(PlanetNameService nameProvider,
			PlanetParams params) {
		this.params = params;
		this.nameProvider = nameProvider;
	}

	public PlanetComponent generatePlanetComponent() {
		PlanetComponent comp = new PlanetComponent(new Vector2());
		comp.temperatureCelsius = params.temperatureProvider.getValue();
		comp.percentWater = params.waterProvider.getValue();
		comp.amountGold = params.goldProvider.getValue();
		comp.amountFood = params.foodProvider.getValue();
		comp.population = params.currentPopulationProvider.getValue();
		comp.name = this.nameProvider.getPlanetName(comp);
		return comp;
	}

	public static class PlanetParams {
		public RandomFloatProvider temperatureProvider;
		public RandomFloatProvider waterProvider;
		public RandomIntProvider foodProvider;
		public RandomIntProvider goldProvider;
		public RandomFloatProvider birthBonusProvider;
		public RandomFloatProvider currentPopulationProvider;
	}
}
