package my.company.my.safarigame.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

public class SoundManager {
    private static SoundManager instance;
    private boolean soundEnabled = true;
    private float volume = 1.0f;
    private Clip currentClip = null; // Track the current playing clip

    private SoundManager() {
        // Private constructor to enforce singleton pattern
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playSound(String soundFile) {
        if (!soundEnabled) return;

        try (InputStream audioSrc = getClass().getClassLoader().getResourceAsStream("sounds/" + soundFile)) {
            if (audioSrc == null) {
                System.err.println("Sound file not found: " + soundFile);
                return;
            }

            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc))) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                // Save the current clip to adjust volume later if needed
                this.currentClip = clip;

                // Apply volume
                setVolumeToClip(clip);

                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    private void setVolumeToClip(Clip clip) {
        // Apply volume to the clip if supported
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));

        // If there's a currently playing clip, immediately adjust its volume
        if (currentClip != null && currentClip.isActive()) {
            setVolumeToClip(currentClip);
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public float getVolume() {
        return volume;
    }
}
