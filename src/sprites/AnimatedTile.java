package sprites;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class AnimatedTile {
    private final AnimatedSprite sprite;
    private final int tamanhoDoTile;

    // A duração de cada frame (ajuste se quiser mais rápido ou devagar)
    // 250ms = 4 frames por segundo
    private static final long DURACAO_FRAME_MS = 250;

    public AnimatedTile(BufferedImage[] frames, int tamanhoDoTile) {
        if (frames == null || frames.length == 0) {
            throw new IllegalArgumentException("Frames vazios para AnimatedTile");
        }

        // Cria a animação em loop
        Animation animation = new Animation(frames, DURACAO_FRAME_MS, true);
        this.sprite = new AnimatedSprite(animation);
        this.tamanhoDoTile = tamanhoDoTile;
    }

    public void update(long deltaMs) {
        sprite.update(deltaMs);
    }

    // Otimização: Recebe X e Y aqui, permitindo reutilizar o mesmo objeto para todo o mapa
    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(sprite.getCurrentFrame(), x, y, tamanhoDoTile, tamanhoDoTile, null);
    }
}