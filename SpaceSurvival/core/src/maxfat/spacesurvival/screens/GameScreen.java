package maxfat.spacesurvival.screens;

import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.gamesystem.PlayerQuery;
import maxfat.spacesurvival.gamesystem.ScanProgressComponent;
import maxfat.spacesurvival.overlap2d.GameUIManager;
import maxfat.spacesurvival.overlap2d.GameUIManager.IDialog;
import maxfat.spacesurvival.overlap2d.ICallback;
import maxfat.spacesurvival.overlap2d.StageStack;
import maxfat.spacesurvival.rendersystem.PlanetIconComponent;
import maxfat.spacesurvival.rendersystem.PositionComponent;
import maxfat.spacesurvival.rendersystem.RadiusComponent;
import maxfat.spacesurvival.rendersystem.ScanComponentRenderable;
import maxfat.spacesurvival.screens.GameManager.GameManagerListener;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
	private Viewport uiViewport;
	GameScreenInputManager inputManager;
	ViewController cameraManager;

	PlayerQuery player;
	StageStack stageStack;
	GameUIManager gameUI;
	GameManager gameController;

	private final ComponentMapper<RadiusComponent> radiusMapper = ComponentMapper
			.getFor(RadiusComponent.class);
	private final ComponentMapper<PlanetComponent> planetMapper = ComponentMapper
			.getFor(PlanetComponent.class);
	private final AssetManager assetManager;


	public GameScreen(GameManager gameManager, AssetManager assetManager) {
		this.assetManager = assetManager;

		this.gameController = gameManager;
		this.player = this.gameController.getPlayer();
		this.uiViewport = gameManager.getUIViewport();
		Viewport gameViewport = gameManager.getGameViewport();
		this.cameraManager = new ViewController(gameViewport);

		this.inputManager = new GameScreenInputManager(gameViewport);
		this.inputManager.listener = inputListener;
		this.stageStack = new StageStack();
		this.stageStack.push(new StageStack.StageWrapper(null,
				this.inputManager));

		gameUI = new GameUIManager(this.stageStack, this.uiViewport,
				new PolygonSpriteBatch(), this.assetManager);
		initPlanetTouchableUI();

		this.gameController.setListener(this.gameManagerListener);
	}

	GameManagerListener gameManagerListener = new GameManagerListener() {
		@Override
		public void onScanPlanetComplete(GameManager manager,
				Entity planetScanEntity) {
			Entity iconEntity = new Entity();
			PositionComponent p = planetScanEntity
					.getComponent(PositionComponent.class);
			RadiusComponent radius = planetScanEntity
					.getComponent(RadiusComponent.class);
			ScanProgressComponent comp = planetScanEntity
					.getComponent(ScanProgressComponent.class);
			Actor planetHasInfo = gameUI.getPlanetExclaimationIcon();
			float x = p.x;
			float y = p.y + radius.radius;
			iconEntity.add(new PlanetIconComponent(comp.planetEntity,
					planetHasInfo, x, y));
			manager.addEntity(iconEntity);
		}
	};

	private void initPlanetTouchableUI() {
		ImmutableArray<Entity> ary = gameController.getPlanets();
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
			cameraManager.addZoom(increment);
		}

		@Override
		public void viewportDrag(Vector2 v) {
			cameraManager.translate(v.x, v.y);
		}

		@Override
		public void onPlanetClicked(final Entity entity) {
			gameController.removeAllNotificationsFor(entity);
			if (player.ownsPlanet(entity)) {
				// display management view.
			} else if (player.hasLimitedKnowledgeOfPlanet(entity)) {
				// display limited planet info.
				// population name, planet stats, planet name.
				ICallback<IDialog> claimPlanetCallback = new ICallback<IDialog>() {
					@Override
					public void callback(IDialog scanPlanetDialog) {
						scanPlanetDialog.close();
					}
				};
				gameUI.showLimitedInfoPlanetDialog(entity, claimPlanetCallback);
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

						// scan entity is separate entity.
						// this is so many players can scan the same planet,
						// since only 1 component of each type is present an
						// entity
						Entity scanEntity = new Entity();

						// add game state components
						scanEntity.add(planetComp);
						scanEntity.add(player.getPlayerComponent());
						// scan entity links to planet entity through this
						// component
						scanEntity.add(new ScanProgressComponent(entity));

						// add render state components
						scanEntity.add(pos);
						scanEntity.add(radius);

						Vector2 scanAnimationCenter = planetComp.getPosition();
						scanEntity.add(new ScanComponentRenderable(gameUI
								.getPlanetScanActors(scanAnimationCenter)));
						gameController.addEntity(scanEntity);
					}
				};
				ICallback<IDialog> claimPlanetCallback = new ICallback<IDialog>() {
					@Override
					public void callback(IDialog scanPlanetDialog) {
						scanPlanetDialog.close();
					}
				};
				gameUI.showUnknownPlanetDialog(entity, scanPlanetRunnable,
						claimPlanetCallback);
			}
		}

		@Override
		public void onExitRequested() {
			gameUI.showYesNoDialog("Do you want to quit?", "Yes", "No",
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
		// update camera with player input.
		this.cameraManager.update(delta);

		// updates view model and renders view.
		this.gameController.update(delta);

		// render ui.
		this.gameUI.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		this.gameController.resize(width, height);
		this.gameUI.update(width, height);
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