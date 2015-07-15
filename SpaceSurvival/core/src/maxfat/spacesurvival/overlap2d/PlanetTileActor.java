package maxfat.spacesurvival.overlap2d;

import maxfat.spacesurvival.rendersystem.AnimatingPolygonRegionComponent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlanetTileActor extends Actor {
	private Entity planetEntity;
	ShapeRenderer shapeRenderer;
	float recordDotTime = 0;

	public PlanetTileActor(Entity planetEntity, ShapeRenderer shapeRenderer) {
		this.planetEntity = planetEntity;
		this.shapeRenderer = shapeRenderer;
	}

	private void drawAt(PolygonSpriteBatch batch, float x, float y, float size,
			AnimatingPolygonRegionComponent c) {
		for (AnimatingPolygonRegionComponent.PolygonLayer layer : c.layers) {
			batch.draw(layer.polygonRegion, x, y, size, size);
		}
	}

	public void act(float time) {
		super.act(time);
		this.recordDotTime += time;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		PolygonSpriteBatch spriteBatch = (PolygonSpriteBatch) batch;
		spriteBatch.setColor(Color.WHITE);
		AnimatingPolygonRegionComponent component = planetEntity
				.getComponent(AnimatingPolygonRegionComponent.class);
		float width = getWidth();
		float height = getHeight();
		float size = Math.max(width, height) * .8f;
		float x = getX();
		float y = getY();
		x = x + width / 2;
		y = y + height / 2;
		drawAt(spriteBatch, x, y, size, component);
		spriteBatch.flush();

	}
}