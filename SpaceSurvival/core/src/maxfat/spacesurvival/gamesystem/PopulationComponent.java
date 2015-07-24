package maxfat.spacesurvival.gamesystem;

import com.badlogic.ashley.core.Component;

public class PopulationComponent extends Component {
	public String name;
	public int reproductionRate;
	public int hardiness;
	public int foodEarnedPerFarmer;
	public int foodEatenPerPersonPerTurn;
	public int goldMiningSpeed;

	public float extrafoodReproductionBonus;

	public String toString() {
		return "[life=" + reproductionRate
				+ ", tough=" + hardiness + ", mine=" + goldMiningSpeed
				+ ", food=" + foodEarnedPerFarmer + "]";
	}
}