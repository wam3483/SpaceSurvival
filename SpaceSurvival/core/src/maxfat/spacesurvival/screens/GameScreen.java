package maxfat.spacesurvival.screens;

import maxfat.graph.Graph;
import maxfat.graph.PlanetData;
import maxfat.spacesurvival.gamesystem.PlayerComponent;
import maxfat.spacesurvival.gamesystem.PlayerQuery;
import maxfat.spacesurvival.overlap2d.GameDialogs;
import maxfat.spacesurvival.overlap2d.StageStack;
import maxfat.spacesurvival.screens.GameStateEngine.PlanetGameState;
import maxfat.util.random.RandomUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
	private FitViewport gameViewport;
	private FitViewport uiViewport;
	GameScreenInputManager inputManager;
	private GameStateEngine gameState;
	private RenderEngine renderEngine;
	ViewController gameView;

	PlayerQuery player;
	StageStack stageStack;
	GameDialogs dialogs;

	public GameScreen(Graph<PlanetData> graph) {
		this.gameViewport = new FitViewport(GameConstants.ScreenWidth,
				GameConstants.ScreenHeight);
		this.uiViewport = new FitViewport(GameConstants.ScreenWidth,
				GameConstants.ScreenHeight);
		this.gameView = new ViewController(gameViewport);
		this.renderEngine = new RenderEngine(this.gameViewport, graph);
		PlayerComponent playerComponent = new PlayerComponent(
				RandomUtil.getUniqueId());
		this.gameState = new GameStateEngine(this.renderEngine.getGraph());
		this.gameState.addPlayer(playerComponent);
		this.player = new PlayerQuery(playerComponent, this.gameState);
		this.inputManager = new GameScreenInputManager(this.gameState,
				this.gameViewport);
		this.inputManager.listener = inputListener;
		this.stageStack = new StageStack();
		this.stageStack.push(new StageStack.StageWrapper(null,
				this.inputManager));

		dialogs = new GameDialogs(this.stageStack, this.uiViewport,
				new SpriteBatch());
	}

	GameScreenInputManager.IGameUIListener inputListener = new GameScreenInputManager.IGameUIListener() {

		@Override
		public void zoom(float increment) {
			gameView.addZoom(increment);
		}

		@Override
		public void viewportDrag(Vector2 v) {
			gameView.translate(v.x, v.y);
		}

		@Override
		public void onPlanetClicked(PlanetGameState p) {
			if (player.ownsPlanet(p)) {
				// display management view.
			} else if (player.hasLimitedKnowledgeOfPlanet(p)) {
				// display limited planet info.
				// population name, planet stats, planet name.
			} else if (player.canPlayerScanPlanet(p)) {
				// option to remotely scan planet.
			}
		}

		@Override
		public void onExitRequested() {
			dialogs.showYesNoDialog("Do you want to quit?", "Yes", "No",
					new Runnable() {
						public void run() {
							System.out.println("quit!");
							// Gdx.app.exit();
						}
					}, null);
		}
	};

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(
				GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		this.gameView.update(delta);
		this.gameState.update(delta);
		this.renderEngine.render(delta);
		this.dialogs.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		this.gameViewport.update(width, height);
		this.gameViewport.update(width, height);
		this.dialogs.update(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		this.inputManager.dispose();
	}
}