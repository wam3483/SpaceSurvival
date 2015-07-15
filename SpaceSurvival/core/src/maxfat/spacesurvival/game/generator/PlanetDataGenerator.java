package maxfat.spacesurvival.game.generator;

import maxfat.graph.PlanetData;
import maxfat.spacesurvival.game.Civilization;
import maxfat.spacesurvival.game.IFactory;

public class PlanetDataGenerator implements IFactory<PlanetData> {
	CivilizationGenerator gen;

	public PlanetDataGenerator(CivilizationGenerator gen) {
		this.gen = gen;
	}

	@Override
	public PlanetData create() {
		Civilization civ = gen.gerateCivilization();
		PlanetData data = new PlanetData(civ.planetComp.getPosition(), 0, 0,
				civ);
		return data;
	}

}
