package maxfat.spacesurvival.screens;

import maxfat.graph.FloatRange;
import maxfat.graph.Graph;
import maxfat.graph.GraphGenerationParams;
import maxfat.graph.GraphGenerator;
import maxfat.graph.IntRange;
import maxfat.graph.Node;
import maxfat.graph.PlanetData;
import maxfat.spacesurvival.game.generator.CivilizationGenerator;
import maxfat.spacesurvival.game.generator.PlanetComponentGenerator;
import maxfat.spacesurvival.game.generator.PlanetDataGenerator;
import maxfat.spacesurvival.game.generator.PlanetNameService;
import maxfat.spacesurvival.game.generator.PopulationGenerator;
import maxfat.spacesurvival.game.generator.PopulationNameService;
import maxfat.spacesurvival.game.generator.RandomFloatProvider;
import maxfat.util.random.DefaultRandom;
import maxfat.util.random.IRandom;

import com.badlogic.gdx.Game;

public class SpaceSurvivalGame extends Game {

	private GraphGenerator<PlanetData> generator;

	public SpaceSurvivalGame() {
		this.initGenerator();
	}

	@Override
	public void create() {
		Node<PlanetData> node = this.generator.generate();
		Graph<PlanetData> graph = new Graph<PlanetData>(node);
		this.setScreen(new GameScreen(graph));
	}

	private void initGenerator() {
		int numberNodes = 1000;
		IntRange edgeRange = new IntRange(2, 2);
		FloatRange distanceRange = new FloatRange(50, 2000);
		IntRange nodeExpansionRange = new IntRange(1, 1);
		FloatRange sizeRange = new FloatRange(50, 100);
		int edgesToAllNodesDistance = 2000;
		GraphGenerationParams graphParams = new GraphGenerationParams(
				numberNodes, edgeRange, distanceRange, nodeExpansionRange,
				sizeRange, edgesToAllNodesDistance);
		IRandom random = new DefaultRandom(0L);

		PlanetNameService planetNameService = new PlanetNameService(random,
				new String[] { "bob", "joe", "george", "earth" });
		PlanetComponentGenerator.PlanetParams planetParams = new PlanetComponentGenerator.PlanetParams();
		planetParams.birthBonusProvider = new RandomFloatProvider(random,
				new FloatRange(1, 10));
		planetParams.currentPopulationProvider = new RandomFloatProvider(
				random, new FloatRange(1, 10));
		planetParams.foodBonusProvider = new RandomFloatProvider(random,
				new FloatRange(1, 10));
		planetParams.maxPopProvider = new RandomFloatProvider(
				random,
				new FloatRange((float) Math.pow(10, 3), (float) Math.pow(10, 7)));
		planetParams.temperatureProvider = new RandomFloatProvider(random, //
				new FloatRange(GameConstants.PlanetConstants.LivableMinTemp,
						GameConstants.PlanetConstants.LivableMaxTemp));
		planetParams.waterProvider = new RandomFloatProvider(random,
				new FloatRange(0, 1));

		PlanetComponentGenerator planetGen = new PlanetComponentGenerator(
				planetNameService, planetParams);

		PopulationNameService popName = new PopulationNameService();
		PopulationGenerator.PopulationParams popParams = new PopulationGenerator.PopulationParams(
				random);
		PopulationGenerator popGen = new PopulationGenerator(popParams, popName);
		CivilizationGenerator civGen = new CivilizationGenerator(planetGen,
				popGen, random, .75f);

		PlanetDataGenerator factory = new PlanetDataGenerator(civGen);
		this.generator = new GraphGenerator<PlanetData>(graphParams, random,
				factory);
	}
}