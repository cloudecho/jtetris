

package com.github.cloudecho.jtetris;

import java.applet.Applet;
import java.applet.AudioClip;

public class GameAudio {
    private AudioClip audioClip;
    private static final GameAudio GA = new GameAudio();

    private GameAudio() {
        try {
            final String audioFile = "/di-dar.mid";
            this.audioClip = Applet.newAudioClip(this.getClass().getResource(audioFile));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static GameAudio getInstance() {
        return GA;
    }

    public void play() {
        if (this.audioClip != null) {
            this.audioClip.loop();
        }

    }

    public void stop() {
        if (this.audioClip != null) {
            this.audioClip.stop();
        }
    }
}
