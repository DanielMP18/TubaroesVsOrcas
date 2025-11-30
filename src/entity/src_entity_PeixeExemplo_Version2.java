package entity;

import sprites.SpriteSheet;
import sprites.Animation;
import sprites.AnimatedSprite;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PeixeExemplo {
    private AnimatedSprite sprite;
    private int x, y;
    private long lastUpdateTime;

    public PeixeExemplo() {
        try {
            // Coloque seu arquivo em resources/sprites/ (ou dentro do classpath)
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/sprites/shark_sheet.png"));
            if (sheet == null) throw new IOException("recurso /sprites/shark_sheet.png não encontrado no classpath");
            // frames 32x32, conforme você informou
            SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
            BufferedImage[] frames = ss.getSprites();
            Animation anim = new Animation(frames, 100); // 100 ms por frame (ajuste se quiser mais rápido/devagar)
            sprite = new AnimatedSprite(anim);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            // Em desenvolvimento, lançar exceção ajuda a ver o problema; em produção troque por fallback
        }
        lastUpdateTime = System.currentTimeMillis();
    }

    // método de conveniência que calcula delta internamente (mantive para minimizar mudanças no restante do projeto)
    public void update() {
        long now = System.currentTimeMillis();
        long delta = now - lastUpdateTime;
        lastUpdateTime = now;

        if (sprite != null) sprite.update(delta);

        // lógica de movimento...
    }

    public void render(Graphics g) {
        if (sprite != null) sprite.render(g, x, y);
    }
}
