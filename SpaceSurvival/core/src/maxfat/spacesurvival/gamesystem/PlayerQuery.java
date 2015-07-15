package maxfat.spacesurvival.gamesystem;

import maxfat.graph.Graph;
import maxfat.graph.Node;
import maxfat.graph.PlanetData;
import maxfat.spacesurvival.screens.GameStateEngine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

public class PlayerQuery {
	private final PlayerComponent playerComponent;
	GameStateEngine gameState;

	public PlayerQuery(PlayerComponent playerComp, GameStateEngine gameState) {
		this.playerComponent = playerComp;
		this.gameState = gameState;
	}

	public PlayerComponent getPlayerComponent() {
		return this.playerComponent;
	}

	public PlanetComponent getPlanetComponent(Entity e) {
		return e.getComponent(PlanetComponent.class);
	}

	@SuppressWarnings("unchecked")
	public Array<Entity> getOwnedPlanets() {
		Engine engine = gameState.getEngine();
		ImmutableArray<Entity> ary = engine.getEntitiesFor(Family.all(
				PlayerComponent.class, PlanetComponent.class).get());
		Array<Entity> ownedPlanets = new Array<Entity>();
		for (Entity e : ary) {
			PlayerComponent player = e.getComponent(PlayerComponent.class);
			if (player.equals(this.playerComponent)) {
				ownedPlanets.add(e);
			}
		}
		return ownedPlanets;
	}

	public Node<PlanetData> getNodeFor(PlanetComponent p) {
		Graph<PlanetData> graph = this.gameState.getPlanetGraph();
		for (Node<PlanetData> node : graph) {
			PlanetData data = node.getData();
			if (data.getPlanetComponent().equals(p)) {
				return node;
			}
		}
		return null;
	}

	public boolean ownsPlanet(Entity e) {
		PlayerComponent planetOwner = e.getComponent(PlayerComponent.class);
		return this.playerComponent.equals(planetOwner);
	}

	public boolean hasLimitedKnowledgeOfPlanet(Entity e) {
		PlanetComponent planetComp = e.getComponent(PlanetComponent.class);
		return this.playerComponent.hasKnowledgeOfPlanet(planetComp);
	}

	public boolean canPlayerScanPlanet(Entity entity) {
		PlanetComponent planetComp = entity.getComponent(PlanetComponent.class);
		// has knowledge of planet if its current owned, previously owned, or
		// previously scanned.
		if (this.playerComponent.hasKnowledgeOfPlanet(planetComp)) {
			return false;
		}
		// have no knowledge of planet, and planet is neighbor of an owned
		// planet.
		else {
			Node<PlanetData> canScanNode = getNodeFor(planetComp);
			for (Entity e : getOwnedPlanets()) {
				PlanetComponent ownedComp = e
						.getComponent(PlanetComponent.class);
				Node<PlanetData> ownedNode = getNodeFor(ownedComp);
				if (ownedNode.getEdges().contains(canScanNode)) {
					return true;
				}
			}
			return false;
		}
	}
}