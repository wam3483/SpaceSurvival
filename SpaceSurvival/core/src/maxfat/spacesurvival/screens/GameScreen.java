package maxfat.spacesurvival.screens;

import maxfat.graph.Graph;
import maxfat.graph.PlanetData;
import maxfat.spacesurvival.gamesystem.PlayerComponent;
import maxfat.spacesurvival.gamesystem.PlayerQuery;
import maxfat.spacesurvival.overlap2d.DialogBuilder;
import maxfat.spacesurvival.overlap2d.DialogBuilder.IYesNoListener;
import maxfat.spacesurvival.screens.GameStateEngine.PlanetGameState;
import maxfat.util.random.RandomUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.uwsoft.editor.renderer.actor.CompositeItem;

public class GameScreen implements Screen {
	private FitViewport viewport;
	GameScreenInputManager inputManager;
	private GameStateEngine gameState;
	private RenderEngine renderEngine;
	ViewController gameView;

	PlayerQuery player;
	DialogBuilder dialogBuilder;
	Stage uiStage;

	public GameScreen(Graph<PlanetData> graph) {
		this.uiStage = new Stage(new FitViewport(GameConstants.ScreenWidth,
				GameConstants.ScreenHeight));
		this.viewport = new FitViewport(GameConstants.ScreenWidth,
				GameConstants.ScreenHeight);
		this.gameView = new ViewController(viewport);
		this.renderEngine = new RenderEngine(this.viewport, graph);
		PlayerComponent playerComponent = new PlayerComponent(
				RandomUtil.getUniqueId());
		this.gameState = new GameStateEngine(this.renderEngine.getGraph());
		this.gameState.addPlayer(playerComponent);
		this.player = new PlayerQuery(playerComponent, this.gameState);
		this.inputManager = new GameScreenInputManager(this.gameState,
				this.viewport);
		this.inputManager.listener = inputListener;
		this.dialogBuilder = new DialogBuilder(this.uiStage);
		Gdx.input.setInputProcessor(this.inputManager);
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
			CompositeItem exitDialog = dialogBuilder
					.getExitDialog(new IYesNoListener() {
						@Override
						public void yesPressed(CompositeItem dialog) {
							dialog.dispose();
							Gdx.app.exit();
						}

						@Override
						public void noPressed(CompositeItem dialog) {
							dialog.remove();
							dialog.dispose();
							Gdx.input.setInputProcessor(inputManager);
						}
					});
			uiStage.addActor(exitDialog);
			Gdx.input.setInputProcessor(uiStage);
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
		this.uiStage.act(delta);
		this.uiStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		this.viewport.update(width, height);
		this.uiStage.getViewport().update(width, height);
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