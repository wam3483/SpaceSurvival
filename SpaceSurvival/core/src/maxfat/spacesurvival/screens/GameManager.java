package maxfat.spacesurvival.screens;

import maxfat.graph.Graph;
import maxfat.graph.Node;
import maxfat.graph.PlanetData;
import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.gamesystem.PlayerComponent;
import maxfat.spacesurvival.gamesystem.PlayerQuery;
import maxfat.spacesurvival.rendersystem.RadiusComponent;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameManager {
	private final Engine engine;
	private final GameStateEngine gameStateEngine;
	private final RenderEngine renderEngine;
	private final Viewport gameViewport;
	private final PlayerQuery playerQuery;

	public GameManager(Graph<PlanetData> graph, Viewport gameViewport,
			PlayerComponent playerComp) {
		this.engine = new Engine();
		this.gameViewport = gameViewport;
		this.renderEngine = new RenderEngine(engine, this.gameViewport, graph);
		this.gameStateEngine = new GameStateEngine(engine, graph);
		this.playerQuery = new PlayerQuery(playerComp, this.gameStateEngine);

		for (Node<PlanetData> node : graph) {
			Entity e = new Entity();
			this.renderEngine.addRenderComponentsForPlanet(e, node);
			this.gameStateEngine.addGameComponentsForPlanet(e, node);
			engine.addEntity(e);
		}
	}

	public void addEntity(Entity e) {
		this.engine.addEntity(e);
	}

	public PlayerQuery getPlayer() {
		return this.playerQuery;
	}

	public Viewport getGameViewport() {
		return this.gameViewport;
	}

	@SuppressWarnings("unchecked")
	public ImmutableArray<Entity> getPlanets() {
		return engine.getEntitiesFor(Family.all(PlanetComponent.class,
				RadiusComponent.class).get());
	}

	public GameStateEngine getGameStateEngine() {
		return this.gameStateEngine;
	}

	public void update(float delta) {
		this.gameStateEngine.update(delta);
		this.renderEngine.render(delta);
	}
}
