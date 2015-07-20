package maxfat.spacesurvival.screens;

import maxfat.graph.Graph;
import maxfat.graph.Node;
import maxfat.graph.PlanetData;
import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.gamesystem.PlayerComponent;
import maxfat.spacesurvival.gamesystem.PlayerQuery;
import maxfat.spacesurvival.rendersystem.PlanetIconComponent;
import maxfat.spacesurvival.rendersystem.RadiusComponent;
import maxfat.spacesurvival.screens.GameStateEngine.GameStateListener;

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
	private final Viewport uiViewport;
	private final PlayerQuery playerQuery;

	private GameManagerListener listener;

	public GameManager(Graph<PlanetData> graph, Viewport gameViewport,
			Viewport uiViewport, PlayerComponent playerComp) {
		this.listener = NullListener;
		this.engine = new Engine();
		this.gameViewport = gameViewport;
		this.uiViewport = uiViewport;
		this.renderEngine = new RenderEngine(engine, this.gameViewport, graph);
		this.gameStateEngine = new GameStateEngine(engine, graph);
		this.playerQuery = new PlayerQuery(playerComp, this.gameStateEngine);
		this.initPlanetEntities(graph);
		this.addEngineListeners();
	}

	@SuppressWarnings("unchecked")
	public void removeAllNotificationsFor(Entity entity) {
		ImmutableArray<Entity> entities = this.engine.getEntitiesFor(Family
				.all(PlanetIconComponent.class).get());
		for (Entity e : entities) {
			PlanetIconComponent scan = e
					.getComponent(PlanetIconComponent.class);
			if (scan.planetEntity == entity) {
				this.engine.removeEntity(e);
			}
		}
	}

	public void setListener(GameManagerListener listener) {
		this.listener = listener;
	}

	private void initPlanetEntities(Graph<PlanetData> graph) {
		for (Node<PlanetData> node : graph) {
			Entity e = new Entity();
			this.renderEngine.addRenderComponentsForPlanet(e, node);
			this.gameStateEngine.addGameComponentsForPlanet(e, node);
			engine.addEntity(e);
		}
	}

	private void addEngineListeners() {
		this.gameStateEngine.setListener(new GameStateListener() {
			@Override
			public void onPlanetScanComplete(Entity planetScanEntity) {
				listener.onScanPlanetComplete(GameManager.this,
						planetScanEntity);
			}
		});
	}

	public void addEntity(Entity e) {
		this.engine.addEntity(e);
	}

	public PlayerQuery getPlayer() {
		return this.playerQuery;
	}

	public Viewport getUIViewport() {
		return this.uiViewport;
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
		this.engine.update(delta);
	}

	public void resize(int width, int height) {
		this.gameViewport.update(width, height);
		this.renderEngine.resize(width, height);
	}

	public interface GameManagerListener {
		void onScanPlanetComplete(GameManager manager, Entity planetScanEntity);
	}

	public static final GameManagerListener NullListener = new GameManagerListener() {
		@Override
		public void onScanPlanetComplete(GameManager manager,
				Entity planetScanEntity) {
		}
	};
}
