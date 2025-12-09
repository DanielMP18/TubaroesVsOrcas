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

    public void reset() {
        animation.reset();
    }

    public boolean isFinished() {
        return animation.isFinished();
    }

    public int getWidth() {
        return animation.getCurrentFrame().getWidth();
    }

    public int getHeight() {
        return animation.getCurrentFrame().getHeight();
    }

    // --- ESSE É O MÉTODO QUE O AnimatedTile ESTÁ PROCURANDO E NÃO ACHAVA ---
    public BufferedImage getCurrentFrame() {
        return animation.getCurrentFrame();
    }
}
