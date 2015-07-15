package maxfat.spacesurvival.screens;

import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.gamesystem.PlayerQuery;
import maxfat.spacesurvival.gamesystem.ScanComponent;
import maxfat.spacesurvival.overlap2d.GameDialogs;
import maxfat.spacesurvival.overlap2d.GameDialogs.IDialog;
import maxfat.spacesurvival.overlap2d.ICallback;
import maxfat.spacesurvival.overlap2d.StageStack;
import maxfat.spacesurvival.rendersystem.PositionComponent;
import maxfat.spacesurvival.rendersystem.RadiusComponent;
import maxfat.spacesurvival.rendersystem.ScanComponentRenderable;
import maxfat.spacesurvival.rendersystem.TextureActor;
import maxfat.util.random.RandomUtil;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
	private FitViewport uiViewport;
	GameScreenInputManager inputManager;
	ViewController gameView;

	PlayerQuery player;
	StageStack stageStack;
	GameDialogs dialogs;
	GameManager gameManager;

	private final ComponentMapper<RadiusComponent> radiusMapper = ComponentMapper
			.getFor(RadiusComponent.class);
	private final ComponentMapper<PlanetComponent> planetMapper = ComponentMapper
			.getFor(PlanetComponent.class);
	private final AssetManager assetManager;

	public GameScreen(GameManager gameManager, AssetManager assetManager) {
		this.assetManager = assetManager;
		this.gameManager = gameManager;
		this.player = this.gameManager.getPlayer();
		this.uiViewport = new FitViewport(GameConstants.ScreenWidth,
				GameConstants.ScreenHeight);
		Viewport gameViewport = gameManager.getGameViewport();
		this.gameView = new ViewController(gameViewport);

		this.inputManager = new GameScreenInputManager(gameViewport);
		this.inputManager.listener = inputListener;
		this.stageStack = new StageStack();
		this.stageStack.push(new StageStack.StageWrapper(null,
				this.inputManager));

		dialogs = new GameDialogs(this.stageStack, this.uiViewport,
				new PolygonSpriteBatch());
		initPlanetTouchableUI();
	}

	private void initPlanetTouchableUI() {
		ImmutableArray<Entity> ary = gameManager.getPlanets();
		for (final Entity e : ary) {
			final Actor a = new Actor();
			PlanetComponent p = planetMapper.get(e);
			RadiusComponent radiusComp = radiusMapper.get(e);
			Vector2 point = p.getPosition();
			float size = radiusComp.radius * 2;
			a.setBounds(point.x - size / 2, point.y - size / 2, size, size);
			a.setUserObject(e);
			this.inputManager.addPlanetActor(a);
		}
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
		public void onPlanetClicked(final Entity entity) {
			if (player.ownsPlanet(entity)) {
				// display management view.
			} else if (player.hasLimitedKnowledgeOfPlanet(entity)) {
				// display limited planet info.
				// population name, planet stats, planet name.
			} else if (player.canPlayerScanPlanet(entity)) {
				// option to remotely scan planet.
				Runnable scanPlanetRunnable = new Runnable() {
					@Override
					public void run() {
						PlanetComponent planetComp = entity
								.getComponent(PlanetComponent.class);
						PositionComponent pos = entity
								.getComponent(PositionComponent.class);
						RadiusComponent radius = entity
								.getComponent(RadiusComponent.class);
						Entity scanEntity = new Entity();
						scanEntity.add(planetComp);
						scanEntity.add(pos);
						scanEntity.add(radius);
						scanEntity.add(player.getPlayerComponent());
						scanEntity.add(new ScanComponent());
						TextureAtlas atlas = assetManager.get(
								"scanProgress.atlas", TextureAtlas.class);
						Array<AtlasRegion> ary = atlas.getRegions();
						Array<Actor> actors = new Array<Actor>();
						Vector2 v = planetComp.getPosition();
						for (int i = 0; i < 5; i++) {
							TextureRegion region = ary.get(i);
							Actor a = new TextureActor(region);
							a.setPosition(v.x - region.getRegionWidth() / 2,
									v.y - region.getRegionHeight() / 2);
							float rotateSpeed = 10 * (i + 1);
							float initialRotation = (float) RandomUtil
									.randomBetweenRanges(0, 360);
							a.setRotation(initialRotation);
							a.addAction(Actions.forever(Actions.rotateBy(
									rotateSpeed, 1)));
							actors.add(a);
						}
						scanEntity.add(new ScanComponentRenderable(actors));
						gameManager.addEntity(scanEntity);
					}
				};
				ICallback<IDialog> claimPlanetCallback = new ICallback<IDialog>() {
					@Override
					public void callback(IDialog scanPlanetDialog) {
						scanPlanetDialog.close();
					}
				};
				dialogs.showUnknownPlanetDialog(entity, scanPlanetRunnable,
						claimPlanetCallback);
			}
		}

		@Override
		public void onExitRequested() {
			dialogs.showYesNoDialog("Do you want to quit?", "Yes", "No",
					new Runnable() {
						public void run() {
							Gdx.app.exit();
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
		this.gameManager.update(delta);
		this.dialogs.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		this.gameManager.getGameViewport().update(width, height);
		this.dialogs.update(width, height);
	}

	@Override
	public void pause() {
		this.inputManager.pause();
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