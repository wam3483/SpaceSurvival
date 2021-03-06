package maxfat.spacesurvival.screens;

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
import maxfat.spacesurvival.gamesystem.SystemScanningUpdater.ScanCompleteListener;
import maxfat.spacesurvival.gamesystem.SystemSpaceTravel;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class GameStateEngine {
	private final Engine engine;
	private final Graph<PlanetData> graph;
	private GameStateListener gameListener;

	public GameStateEngine(Engine myEngine, Graph<PlanetData> graph) {
		this.graph = graph;
		this.engine = myEngine;

		this.engine.addSystem(new SystemBattle());
		this.engine.addSystem(new SystemSpaceTravel());
		this.engine.addSystem(new SystemRefueling());
		this.engine.addSystem(new SystemPlanetPopulationUpdater());
		this.engine.addSystem(new SystemPlayerGoldUpdater());
		SystemScanningUpdater scanningUpdater = new SystemScanningUpdater();
		scanningUpdater.listener = new ScanCompleteListener() {
			@Override
			public void scanComplete(Entity scanEntity,
					PlanetComponent planetScanned) {
				engine.removeEntity(scanEntity);
				gameListener.onPlanetScanComplete(scanEntity);
			}
		};
		this.engine.addSystem(scanningUpdater);
	}

	public void setListener(GameStateListener gameListener) {
		this.gameListener = gameListener;
	}

	public Graph<PlanetData> getPlanetGraph() {
		return this.graph;
	}

	public Engine getEngine() {
		return this.engine;
	}

	// TODO is this necessary?
	public void addPlayer(PlayerComponent p) {
		Entity e = new Entity();
		e.add(p);
		engine.addEntity(e);
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

	public void addGameComponentsForPlanet(Entity e, Node<PlanetData> node) {
		PlanetData data = node.getData();
		PlanetComponent planetComp = data.getPlanetComponent();
		e.add(planetComp);

		PopulationComponent popComp = data.getPopulationComponent();
		if (popComp != null)
			e.add(popComp);
	}

	public void update(float time) {
		this.engine.update(time);
	}

	public interface GameStateListener {
		void onPlanetScanComplete(Entity planetScanEntity);
	}
}