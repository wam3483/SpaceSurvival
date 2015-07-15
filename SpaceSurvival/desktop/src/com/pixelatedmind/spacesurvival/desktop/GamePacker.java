package com.pixelatedmind.spacesurvival.desktop;

import java.io.File;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class GamePacker {
	public static void main(String[] args) {
		pack("../../GameImages/Scan", "../android/assets", "scanProgress");
	}

	static void pack(String inputDir, String output, String outputFileName) {
		File scanDir = new File(inputDir);
		File outputDir = new File(output);
		File imgFile = new File(output + File.separatorChar + outputFileName
				+ ".png");
		File packFile = new File(output + File.separatorChar + outputFileName
				+ ".atlas");
		if (!scanDir.exists())
			throw new RuntimeException("Input file does not exist");
		if (!outputDir.exists())
			throw new RuntimeException("Output directory does not exist");
		if (imgFile.exists() && !imgFile.delete())
			throw new RuntimeException("failed to delete image file");
		if (packFile.exists() && !packFile.delete())
			throw new RuntimeException("failed to delete pack file");

		TexturePacker.Settings settings = new TexturePacker.Settings();
		TexturePacker.process(settings, inputDir, output, outputFileName);
	}
}