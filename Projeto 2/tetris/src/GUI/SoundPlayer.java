package GUI;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class SoundPlayer {
	private AudioClip song; // Sound player
	private URL songPath; // Sound path
	/**
	* Retrieves sound to be played.
	*/
	public SoundPlayer(URL path) {
		try {
			songPath = path;
			song = Applet.newAudioClip(songPath); // Load the Sound
		} catch (Exception e) {
		} // Satisfy the catch
	}

	public void play() {
		song.loop(); // Play
	}

	public void stop() {
		song.stop(); // Stop
	}

	public void playSoundOnce() {
		song.play(); // Play only once
	}

}

