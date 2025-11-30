package sprites;

import java.awt.image.BufferedImage;

public class Animation {
    private final BufferedImage[] frames;
    private final long frameDurationMs;
    private final boolean loop;
    private int currentFrame = 0;
    private long accumulatorMs = 0;
    private boolean finished = false;

    public Animation(BufferedImage[] frames, long frameDurationMs, boolean loop) {
        if (frames == null || frames.length == 0) throw new IllegalArgumentException("frames vazios");
        this.frames = frames;
        this.frameDurationMs = Math.max(1, frameDurationMs);
        this.loop = loop;
    }

    public void update(long deltaMs) {
        if (finished) return;
        accumulatorMs += deltaMs;
        while (accumulatorMs >= frameDurationMs) {
            accumulatorMs -= frameDurationMs;
            currentFrame++;
            if (currentFrame >= frames.length) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                    finished = true;
                    break;
                }
            }
        }
    }

    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }

    public void reset() {
        currentFrame = 0;
        accumulatorMs = 0;
        finished = false;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getFrameCount() {
        return frames.length;
    }
}
