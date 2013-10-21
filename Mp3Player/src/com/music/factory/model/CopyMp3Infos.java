package com.music.factory.model;

import java.util.List;

public class CopyMp3Infos {
	public static List<Mp3Info> mp3Infos = null;
	public static List<Mp3Info> getMP3INFOS() {
		return mp3Infos;
	}

	public static void setMP3INFOS(List<Mp3Info> infos) {
		mp3Infos = infos;
	}
}
