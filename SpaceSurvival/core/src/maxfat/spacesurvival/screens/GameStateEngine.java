package maxfat.spacesurvival.screens;

import java.util.ArrayList;
import java.util.List;

import maxfat.graph.Graph;
import maxfat.graph.Node;
import maxfat.graph.PlanetData;
import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.gamesystem.PlayerComponent;
import maxfat.spacesurvival.gamesystem.PopulationComponent;
import maxfat.spacesurvival.gamesystem.SystemBattle;
import maxfat.spacesurvival.gamesystem.SystemPlanetPopulationUpdater;
import maxfat.spacesurvival.gamesystem.SystemPlayerGoldUpdater;
import maxfat.spacesurvival.gamesystem.SystemRefueling;
import maxfat.spacesurvival.gamesystem.SystemScanningUpdater;
import maxfat.spacesurvival.gamesystem.SystemSpaceTravel;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class GameStateEngine {
	private final Engine engine;
	private final Graph<PlanetData> graph;
	private List<PlanetGameState> planetGameStateList;

	public GameStateEngine(Graph<PlanetData> graph) {
		this.graph = graph;
		this.planetGameStateList = new ArrayList<PlanetGameState>();

		this.engine = new Engine();
		this.engine.addSystem(new SystemBattle());
		this.engine.addSystem(new SystemSpaceTravel());
		this.engine.addSystem(new SystemRefueling());
		this.engine.addSystem(new SystemPlanetPopulationUpdater());
		this.engine.addSystem(new SystemPlayerGoldUpdater());
		this.engine.addSystem(new SystemScanningUpdater());
		initPlanetEntities();
	}

	public Graph<PlanetData> getPlanetGraph() {
		return this.graph;
	}

	public Engine getEngine() {
		return this.engine;
	}

	public void addPlayer(PlayerComponent p) {
		Entity e = new Entity();
		e.add(p);
		engine.addEntity(e);
	}

	public List<PlanetGameState> getAllPlanetsGameState() {
		return this.planetGameStateList;
	}

	public Node<PlanetData> getNodeFor(PlanetData planetData) {
		for (Node<PlanetData> node : this.graph) {
			if (node.getData().equals(planetData)) {
				return node;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean doesPlayerOwnPlanet(PlayerComponent player,
			PlanetComponent planet) {
		ImmutableArray<Entity> ary = this.engine.getEntitiesFor(Family.all(
				PlanetComponent.class, PlayerComponent.class).get());
		for (Entity e : ary) {
			PlayerComponent p = e.getComponent(PlayerComponent.class);
			if (p.equals(player)) {
				return true;
			}
		}
		return false;
	}

	void initPlanetEntities() {
		for (Node<PlanetData> node : graph) {
			Entity e = new Entity();
			PlanetData data = node.getData();
			PlanetComponent planetComp = data.getPlanetComponent();
			e.add(planetComp);
			
			PopulationComponent popComp = data.getPopulationComponent();
			if (popComp != null)
				e.add(popComp);
			
			engine.addEntity(e);
			this.planetGameStateList.add(new PlanetGameState(e, data));
		}
	}

	public void update(float time) {
		this.engine.update(time);
	}

	public static class PlanetGameState {
		public Entity planetEntity;
		public PlanetData planetData;

		public PlanetGameState(Entity planetEntity, PlanetData planetData) {
			this.planetEntity = planetEntity;
			this.planetData = planetData;
		}
	}

}