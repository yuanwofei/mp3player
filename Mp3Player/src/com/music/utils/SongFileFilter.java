package com.music.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

public class SongFileFilter implements FileFilter{

	public boolean accept(File pathName) {
		String fileName = pathName.getName();
		if(pathName.isDirectory()) {
			return true;
		}	
		return fileName.toLowerCase(Locale.getDefault()).endsWith(".mp3");	
	}
}
