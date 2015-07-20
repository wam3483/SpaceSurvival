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

	private ComponentMapper<ScanProgressComponent> scanMapper = ComponentMapper
			.getFor(ScanProgressComponent.class);
	private ComponentMapper<PlanetComponent> planetMapper = ComponentMapper
			.getFor(PlanetComponent.class);

	@SuppressWarnings("unchecked")
	public SystemScanningUpdater() {
		super(Family.all(ScanProgressComponent.class, PlanetComponent.class).get());
		listener = NullListener;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ScanProgressComponent scan = scanMapper.get(entity);
		PlanetComponent planet = planetMapper.get(entity);
		scan.scanProgress += deltaTime;
		if (scan.scanProgress >= planet.scanTime) {
			this.listener.scanComplete(entity, planet);
		}
	}

	public interface ScanCompleteListener {
		void scanComplete(Entity scanEntity, PlanetComponent planetScanned);
	}
}