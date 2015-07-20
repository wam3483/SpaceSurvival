package maxfat.spacesurvival.gamesystem;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class ScanProgressComponent extends Component {
	public float scanProgress = 0;
	// this is the game state planet entity being scanned
	public Entity planetEntity;

	public ScanProgressComponent(Entity e) {
		this.planetEntity = e;
	}
}