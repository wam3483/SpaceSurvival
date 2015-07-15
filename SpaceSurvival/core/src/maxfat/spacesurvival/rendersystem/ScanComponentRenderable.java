package maxfat.spacesurvival.rendersystem;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class ScanComponentRenderable extends Component {
	public Array<Actor> actors;

	public ScanComponentRenderable(Array<Actor> ary) {
		this.actors = ary;
	}
}