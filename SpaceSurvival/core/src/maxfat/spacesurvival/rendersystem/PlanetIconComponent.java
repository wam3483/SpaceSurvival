package maxfat.spacesurvival.rendersystem;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlanetIconComponent extends Component {
	public Actor exclaimationActor;
	public float gameSpaceX;
	public float gameSpaceY;
	public Entity planetEntity;

	public PlanetIconComponent(Entity planetEntity, Actor actor,
			float gameSpaceX, float gameSpaceY) {
		this.planetEntity = planetEntity;
		this.exclaimationActor = actor;
		this.gameSpaceX = gameSpaceX;
		this.gameSpaceY = gameSpaceY;
	}
}
