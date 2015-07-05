package maxfat.spacesurvival.gamesystem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class SystemScanningUpdater extends IteratingSystem {
	public ScanCompleteListener listener;
	private static final ScanCompleteListener NullListener = new ScanCompleteListener() {
		@Override
		public void scanComplete(Entity planetEntity,
				PlanetComponent planetScanned) {
		}
	};

	private ComponentMapper<ScanComponent> scanMapper = ComponentMapper
			.getFor(ScanComponent.class);
	private ComponentMapper<PlanetComponent> planetMapper = ComponentMapper
			.getFor(PlanetComponent.class);

	@SuppressWarnings("unchecked")
	public SystemScanningUpdater() {
		super(Family.all(ScanComponent.class, PlanetComponent.class).get());
		listener = NullListener;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ScanComponent scan = scanMapper.get(entity);
		PlanetComponent planet = planetMapper.get(entity);
		scan.scanProgress += deltaTime;
		if (scan.scanProgress >= planet.scanTime) {
			entity.remove(ScanComponent.class);
			this.listener.scanComplete(entity, planet);
		}
	}

	public interface ScanCompleteListener {
		void scanComplete(Entity planetEntity, PlanetComponent planetScanned);
	}
}