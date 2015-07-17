package maxfat.spacesurvival.rendersystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SystemRenderPlanetScanProgress extends EntitySystem implements
		Disposable {

	private ImmutableArray<Entity> entities;
	private final Viewport viewport;
	private final SpriteBatch batch;

	public SystemRenderPlanetScanProgress(Viewport viewport) {
		this.batch = new SpriteBatch();
		this.viewport = viewport;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.entities = engine.getEntitiesFor(Family.all(
				ScanComponentRenderable.class).get());
	}

	@Override
	public void update(float deltaTime) {
		Camera camera = this.viewport.getCamera();
		this.batch.setProjectionMatrix(camera.projection);
		this.batch.setTransformMatrix(camera.view);
		batch.begin();
		for (int i = 0; i < this.entities.size(); ++i) {
			Entity entity = entities.get(i);
			ScanComponentRenderable scanRenderable = entity
					.getComponent(ScanComponentRenderable.class);
			for (int j = 0; j < scanRenderable.actors.size; j++) {
				Actor actor = scanRenderable.actors.get(j);
				actor.act(deltaTime);
				actor.draw(this.batch, 1);
			}
		}
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}