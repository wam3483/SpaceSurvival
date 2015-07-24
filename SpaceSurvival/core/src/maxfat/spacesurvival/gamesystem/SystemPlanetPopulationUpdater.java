package maxfat.spacesurvival.gamesystem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class SystemPlanetPopulationUpdater extends IteratingSystem {

	private ComponentMapper<PlanetComponent> planetMapper = ComponentMapper
			.getFor(PlanetComponent.class);
	private ComponentMapper<PopulationComponent> populationMapper = ComponentMapper
			.getFor(PopulationComponent.class);

	@SuppressWarnings("unchecked")
	public SystemPlanetPopulationUpdater() {
		super(Family.all(PlanetComponent.class, PopulationComponent.class,
				PlayerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PlanetComponent planetComp = planetMapper.get(entity);
		PopulationComponent popComp = populationMapper.get(entity);
		CivHelper civ = new CivHelper(planetComp, popComp);

		this.updateStarvation(civ);
		this.updateNaturalDeaths(civ);
		this.updateBirths(civ);
	}

	private void updateBirths(CivHelper civ) {
		float peopleBorn = civ.getPopulation() * civ.getBirthRate();
		civ.addPopulation(peopleBorn);
	}

	private void updateNaturalDeaths(CivHelper civ) {
		float naturalDeaths = civ.getPercentDeath();
		float deaths = civ.getPopulation() * naturalDeaths;
		civ.addPopulation(-deaths);
	}

	private void updateStarvation(CivHelper civ) {
		float food = civ.getFoodProduced();
		float requiredFood = civ.getRequiredFood();
		if (food < requiredFood) {
			float fedPopulation = food / civ.getFoodNeededPerPerson();
			float unfedPopulation = civ.getPopulation() - fedPopulation;
			float starvationDeaths = unfedPopulation
					* civ.getStarvationPercent();
			civ.addPopulation(-starvationDeaths);
		}
	}

	private class CivHelper {
		final float MIN_DEATH = .01f;
		PlanetComponent planetComp;
		PopulationComponent popComp;

		public float getFoodBirthBonus() {
			if (this.getFoodProduced() == this.getRequiredFood() * 2) {
				return this.popComp.extrafoodReproductionBonus;
			} else {
				return 0;
			}
		}

		public CivHelper(PlanetComponent planetComp, PopulationComponent popComp) {
			this.planetComp = planetComp;
			this.popComp = popComp;
		}

		/**
		 * Starve chance per person is reduced by .1 for each point of
		 * hardiness.
		 * 
		 * @return
		 */
		public float getStarvationPercent() {
			float normalized = 1 - popComp.hardiness
					/ (float) PopulationRestrictions.Hardiness.range();
			float min = PopulationRestrictions.StarveChance.getMin();
			float chance = min + normalized
					* PopulationRestrictions.StarveChance.range();
			return chance;
		}

		public float getPercentDeath() {
			float death = this.planetComp.getNaturalDeathPercent();
			float popDeath = this.popComp.hardiness;
			death -= popDeath;
			if (death < 0)
				death = MIN_DEATH;
			return death;
		}

		public float getBirthRate() {
			return popComp.reproductionRate + this.getFoodBirthBonus();
		}

		public void addPopulation(float pop) {
			this.planetComp.addPeople(pop);
		}

		public float getPopulation() {
			return this.planetComp.population;
		}

		public float getFoodNeededPerPerson() {
			return this.popComp.foodEatenPerPersonPerTurn;
		}

		public float getRequiredFood() {
			return this.popComp.foodEatenPerPersonPerTurn
					* this.planetComp.population;
		}

		public float getFoodProduced() {
			float foodGeneratedPerFarmer = popComp.foodEarnedPerFarmer;
			float foodProduced = foodGeneratedPerFarmer
					* planetComp.farmingPopulation;
			return foodProduced;
		}
	}
}