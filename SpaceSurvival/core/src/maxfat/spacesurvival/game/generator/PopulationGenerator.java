package maxfat.spacesurvival.game.generator;

import maxfat.graph.IntRange;
import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.gamesystem.PopulationComponent;
import maxfat.spacesurvival.gamesystem.PopulationRestrictions;
import maxfat.util.random.IRandom;
import maxfat.util.random.RandomUtil;

import com.badlogic.gdx.math.MathUtils;

public class PopulationGenerator {

	private final PopulationParams params;
	private final PopulationNameService populationNameGen;

	public PopulationGenerator(PopulationParams params,
			PopulationNameService nameGenerator) {
		this.populationNameGen = nameGenerator;
		this.params = params;
	}

	public PopulationComponent generatePopulationComponent(
			PlanetComponent planetComp) {
		PopulationComponent pop = null;

		pop = new PopulationComponent();
		pop.extrafoodReproductionBonus = 1f;
		pop.name = this.populationNameGen.getPopulationName(planetComp, pop);

		int count = 0;
		int totalPoints = getTotalPoints();

		// loop until all points have been used.
		// 50% chance each iteration that an attribute will get a point.
		while (totalPoints > 0) {
			boolean shouldAddPoint = RandomUtil.randomBool(params.random);
			boolean addedPoint = false;
			if (shouldAddPoint) {
				switch (count) {
				case 0:
					if (pop.reproductionRate < params.reproductionRange
							.getMax()) {
						addedPoint = true;
						pop.reproductionRate++;
					}
					break;
				case 1:
					if (pop.foodEarnedPerFarmer < params.foodPerFarmerRange
							.getMax()) {
						addedPoint = true;
						pop.foodEarnedPerFarmer++;
					}
					break;
				case 2:
					if (pop.hardiness < params.hardinessRange.getMax()) {
						addedPoint = true;
						pop.hardiness++;
					}
					break;
				case 3:
					if (pop.goldMiningSpeed < params.miningSpeedRange.getMax()) {
						addedPoint = true;
						pop.goldMiningSpeed++;
					}
					break;
				}
			}
			if (addedPoint) {
				totalPoints--;
			}
			count = (count + 1) % 4;
		}

		pop.foodEarnedPerFarmer = MathUtils.clamp(pop.foodEarnedPerFarmer,
				params.foodPerFarmerRange.getMin(),
				params.foodPerFarmerRange.getMax());
		pop.hardiness = MathUtils.clamp(pop.hardiness,
				params.hardinessRange.getMin(), params.hardinessRange.getMax());
		pop.goldMiningSpeed = MathUtils.clamp(pop.goldMiningSpeed,
				params.miningSpeedRange.getMin(),
				params.miningSpeedRange.getMax());

		return pop;
	}

	int getTotalPoints() {
		double gaussian = params.random.nextGaussian();
		if (gaussian < 0)
			gaussian *= -1;

		int maxPoints = params.reproductionRange.getMax()
				+ params.hardinessRange.getMax()
				+ params.miningSpeedRange.getMax()
				+ params.foodPerFarmerRange.getMax();
		
		// around 10% of samples fall outside [1.7, -1.7]
		// this means ~10% of populations will be maxed
		float maxPointsMark = 1.7f;
		float normal = (float) MathUtils.clamp(gaussian / maxPointsMark, 0, 1);
		int totalPointsAvailable = (int) (normal * maxPoints);
		return totalPointsAvailable;
	}

	public static class PopulationParams {
		protected IRandom random;
		IntRange reproductionRange;
		IntRange hardinessRange;
		IntRange foodPerFarmerRange;
		IntRange miningSpeedRange;

		public PopulationParams(IRandom random) {
			this.random = random;
			this.reproductionRange = PopulationRestrictions.Reproduction;
			this.hardinessRange = PopulationRestrictions.Hardiness;
			this.foodPerFarmerRange = PopulationRestrictions.FoodProduction;
			this.miningSpeedRange = PopulationRestrictions.GoldProduction;
		}
	}
}
