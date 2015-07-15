package maxfat.spacesurvival.gamesystem;

import java.util.HashSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class PlayerComponent extends Component {
	public long id;
	public long gold;
	public Color color;
	private HashSet<PlanetComponent> planetInfo;

	public PlayerComponent(long id) {
		this.id = id;
		this.gold = 0;
		this.planetInfo = new HashSet<PlanetComponent>();
	}

	public boolean hasKnowledgeOfPlanet(PlanetComponent p) {
		return this.planetInfo.contains(p);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof PlayerComponent) {
			PlayerComponent p = (PlayerComponent) other;
			return p.id == this.id;
		}
		return false;
	}
}
