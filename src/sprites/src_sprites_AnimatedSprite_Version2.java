package sprites;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class AnimatedSprite {
    private final Animation animation;

    public AnimatedSprite(Animation animation) {
        this.animation = animation;
    }

    public void update(long deltaMs) {
        animation.update(deltaMs);
    }

    public void render(Graphics g, int x, int y) {
        BufferedImage img = animation.getCurrentFrame();
        g.drawImage(img, x, y, null);
    }

    public int getWidth() {
        return animation.getCurrentFrame().getWidth();
    }

    public int getHeight() {
        return animation.getCurrentFrame().getHeight();
    }
}