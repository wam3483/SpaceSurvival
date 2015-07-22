package maxfat.spacesurvival.gamesystem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class SystemPlayerGoldUpdater extends IteratingSystem {

	private ComponentMapper<PlanetComponent> planetMapper = ComponentMapper
			.getFor(PlanetComponent.class);
	private ComponentMapper<PlayerComponent> playerMapper = ComponentMapper
			.getFor(PlayerComponent.class);
	private ComponentMapper<PopulationComponent> populationMapper = ComponentMapper
			.getFor(PopulationComponent.class);

	@SuppressWarnings("unchecked")
	public SystemPlayerGoldUpdater() {
		super(Family.all(PlayerComponent.class, PlanetComponent.class,
				PopulationComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PlanetComponent planet = this.planetMapper.get(entity);
		PopulationComponent pop = this.populationMapper.get(entity);
		PlayerComponent player = this.playerMapper.get(entity);

		float goldPerPerson = pop.goldMiningSpeed;
		long earnedGold = (long) (planet.getIdlePopulation() * goldPerPerson);
		if (planet.amountGold < earnedGold) {
			player.gold += planet.amountGold;
			planet.amountGold = 0;
		} else {
			player.gold += earnedGold;
			planet.amountGold -= earnedGold;
		}
	}
}
