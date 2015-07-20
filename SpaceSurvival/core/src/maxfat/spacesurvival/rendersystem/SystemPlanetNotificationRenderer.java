package maxfat.spacesurvival.rendersystem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SystemPlanetNotificationRenderer extends IteratingSystem implements
		Disposable {
	private final Stage stage;

	final OrthographicCamera gameCamera;
	final Viewport iconViewport;
	final Vector3 temp = new Vector3();

	private ComponentMapper<PlanetIconComponent> iconMapper = ComponentMapper
			.getFor(PlanetIconComponent.class);

	@SuppressWarnings("unchecked")
	public SystemPlanetNotificationRenderer(Viewport gameViewport,
			Viewport iconViewport) {
		super(Family.all(PlanetIconComponent.class).get());
		this.iconViewport = iconViewport;
		this.stage = new Stage(iconViewport, new SpriteBatch());
		this.gameCamera = (OrthographicCamera) gameViewport.getCamera();
	}

	@Override
	public void update(float time) {
		if (this.getEntities().size() > 0) {
			super.update(time);
			stage.act(time);
			stage.draw();
			stage.clear();
		}
	}

	void setActorPosition(Actor a, float x, float y) {
		// project from game world space to screen space.
		temp.set(x, y, 0);
		gameCamera.project(temp);
		// screen space is returned with origin at bottom left.
		temp.y = Gdx.graphics.getHeight() - temp.y;

		// project from screen space to icon ui space.
		Camera uiCamera = this.iconViewport.getCamera();
		uiCamera.unproject(temp);

		// finally set position in icon ui space,
		// as that's the space the actor will be rendered in
		a.setPosition(temp.x - a.getWidth() / 2 + 4, temp.y);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PlanetIconComponent icon = iconMapper.get(entity);
		Actor a = icon.exclaimationActor;
		setActorPosition(a, icon.gameSpaceX, icon.gameSpaceY);
		stage.addActor(a);
	}

	public void dispose() {
		this.stage.dispose();
	}
}