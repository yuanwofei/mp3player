package com.music.factory.model;

import java.util.ArrayList;

public class CopyMp3Infos {
	public static ArrayList<Mp3Info> mp3Infos = null;
	public static ArrayList<Mp3Info> getMP3INFOS() {
		return mp3Infos;
	}

	public static void setMP3INFOS(ArrayList<Mp3Info> infos) {
		mp3Infos = infos;
	}
}
