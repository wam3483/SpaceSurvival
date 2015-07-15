package maxfat.spacesurvival.overlap2d;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.uwsoft.editor.renderer.resources.FontSizePair;
import com.uwsoft.editor.renderer.resources.ResourceManager;

public class CustomResourceManager extends ResourceManager {
	@Override
	public void loadFont(FontSizePair pair) {
		String path = "bitmapfonts/" + packResolutionName + "/";
		BitmapFont fontBitmap = new BitmapFont(Gdx.files.getFileHandle(path
				+ pair.fontName + "_" + pair.fontSize + ".fnt",
				FileType.Internal), false);
		bitmapFonts.put(pair, fontBitmap);
	}
}
