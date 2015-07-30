package maxfat.spacesurvival.overlap2d;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.IBaseItem;
import com.uwsoft.editor.renderer.actor.ImageItem;

public class Overlap2DUtil {

	public static void replaceElement(ImageItem placeholder, Actor newItem) {
		float x = placeholder.getX();
		float y = placeholder.getY();
		int zIndex = placeholder.getZIndex();
		float width = placeholder.getWidth() * placeholder.getScaleX();
		float height = placeholder.getHeight() * placeholder.getScaleY();
		Group group = placeholder.getParent();
		group.removeActor(placeholder);

		group.addActor(newItem);
		newItem.setBounds(x, y, width, height);
		newItem.setZIndex(zIndex);
	}

	public static String findCustomVariable(String key, IBaseItem item) {
		String result = null;
		while (item != null
				&& (result = item.getCustomVariables().getStringVariable(key)) == null) {
			item = item.getParentItem();
		}
		return result;
	}

	public static IBaseItem findItemByIdentifier(String id,
			ArrayList<IBaseItem> items) {
		for (IBaseItem item : items) {
			if (item.getDataVO().itemIdentifier.equals(id)) {
				return item;
			}
			if (item.isComposite()) {
				CompositeItem cmp = (CompositeItem) item;
				IBaseItem recursedItem = findItemByIdentifier(id,
						cmp.getItems());
				if (recursedItem != null)
					return recursedItem;
			}
		}
		return null;
	}
}
