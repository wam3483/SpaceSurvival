package maxfat.spacesurvival.overlap2d;

import maxfat.spacesurvival.game.SpaceShip;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;

public class SpaceShipSelectionDialogBuilder {
	SceneLoader loader;

	public SpaceShipSelectionDialogBuilder(SceneLoader loader) {
		this.loader = loader;
	}

	private CompositeItem loadShipWidget(SpaceShip ship) {
		// TODO set UI values based on ship values.
		loader.loadScene("ShipWidget");
		final CompositeItem shipWidget = loader.getRoot();
		return shipWidget;
	}

	public CompositeItem buildDialog(SpaceShip... ships) {
		loader.loadScene("ShipManagementFrame");
		final CompositeItem root = loader.getRoot();
		final Table table = new Table();

		for (int i = 0; i < ships.length; i++) {
			final CompositeItem shipWidget = loadShipWidget(ships[i]);
			table.add(shipWidget);
			table.row();
		}
		final ScrollPane scrollPane = new ScrollPane(table);
		Overlap2DUtil.replaceElement(root.getItems(), "shipScrollPane",
				scrollPane);
		return root;
	}
}
