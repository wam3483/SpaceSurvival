package maxfat.spacesurvival.rendersystem;

import maxfat.spacesurvival.gamesystem.PlanetComponent;
import maxfat.spacesurvival.gamesystem.PlayerComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SystemPlanetRenderer extends EntitySystem {
	private ImmutableArray<Entity> entities;
	PolygonSpriteBatch spriteBatch;
	private ComponentMapper<PositionComponent> positionMapper = ComponentMapper
			.getFor(PositionComponent.class);
	private ComponentMapper<PlayerComponent> playerMapper = ComponentMapper
			.getFor(PlayerComponent.class);
	private ComponentMapper<AnimatingPolygonRegionComponent> regionMapper = ComponentMapper
			.getFor(AnimatingPolygonRegionComponent.class);

	Viewport viewport;

	public SystemPlanetRenderer(Viewport viewport) {
		this.spriteBatch = new PolygonSpriteBatch();
		this.viewport = viewport;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.entities = engine.getEntitiesFor(Family.all(
				PositionComponent.class, RadiusComponent.class,
				AnimatingPolygonRegionComponent.class, PlanetComponent.class)
				.get());
	}

	@Override
	public void update(float deltaTime) {
		Camera camera = viewport.getCamera();
		this.spriteBatch.setProjectionMatrix(camera.projection);
		this.spriteBatch.setTransformMatrix(camera.view);
		this.spriteBatch.begin();

		for (int i = 0; i < this.entities.size(); ++i) {
			Entity entity = entities.get(i);
			PositionComponent position = positionMapper.get(entity);
			AnimatingPolygonRegionComponent poly = this.regionMapper
					.get(entity);
			PlayerComponent playerComponent = this.playerMapper.get(entity);
			boolean onlyRenderFirst = false;
			if (playerComponent != null) {
				onlyRenderFirst = true;
			}
			for (AnimatingPolygonRegionComponent.PolygonLayer layer : poly.layers) {
				this.spriteBatch.draw(layer.polygonRegion, position.x,
						position.y);
				if (onlyRenderFirst)
					break;
			}
			renderExclaimation(entity, position.x, position.y, deltaTime);
		}
		this.spriteBatch.end();
	}

	private void renderExclaimation(Entity e, float x, float y, float deltaTime) {
		PlanetIconComponent comp = e
				.getComponent(PlanetIconComponent.class);
		if (comp != null) {
			comp.exclaimationActor.act(deltaTime);
			comp.exclaimationActor.draw(this.spriteBatch, 1);
		}
	}

	public void dispose() {
		this.spriteBatch.dispose();
	}
}
