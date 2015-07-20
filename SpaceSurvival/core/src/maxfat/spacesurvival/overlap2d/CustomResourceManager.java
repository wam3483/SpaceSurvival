package maxfat.spacesurvival.overlap2d;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.uwsoft.editor.renderer.resources.FontSizePair;
import com.uwsoft.editor.renderer.resources.ResourceManager;

public class CustomResourceManager extends ResourceManager {
	@Override
	public void loadFont(FontSizePair pair) {
		String path = "bitmapfonts/" + packResolutionName + "/";
		FileHandle handle = Gdx.files.getFileHandle(path + pair.fontName + "_"
				+ pair.fontSize + ".fnt", FileType.Internal);
		if (handle.exists()) {
			BitmapFont fontBitmap = new BitmapFont(handle, false);
			bitmapFonts.put(pair, fontBitmap);
		} else {
			super.loadFont(pair);
		}
	}
}
