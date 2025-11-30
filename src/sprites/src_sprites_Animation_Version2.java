package sprites;

import java.awt.image.BufferedImage;

public class Animation {
    private final BufferedImage[] frames;
    private final long frameDurationMs; // duração de cada frame em ms
    private int currentFrame = 0;
    private long accumulatorMs = 0;

    public Animation(BufferedImage[] frames, long frameDurationMs) {
        if (frames == null || frames.length == 0) throw new IllegalArgumentException("frames vazios");
        this.frames = frames;
        this.frameDurationMs = Math.max(1, frameDurationMs);
    }

    // delta em milissegundos
    public void update(long deltaMs) {
        accumulatorMs += deltaMs;
        while (accumulatorMs >= frameDurationMs) {
            accumulatorMs -= frameDurationMs;
            currentFrame = (currentFrame + 1) % frames.length;
        }
    }

    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }

    public void reset() {
        currentFrame = 0;
        accumulatorMs = 0;
    }
}