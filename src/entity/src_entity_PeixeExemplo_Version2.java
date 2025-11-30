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
            // supondo frames de 64x64 — ajuste para sua imagem
            SpriteSheet ss = new SpriteSheet(sheet, 64, 64);
            BufferedImage[] frames = ss.getSprites();
            Animation anim = new Animation(frames, 100); // 100 ms por frame
            sprite = new AnimatedSprite(anim);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            // se getResourceAsStream retornou null, verifique o caminho (veja checklist abaixo)
        }
        lastUpdateTime = System.currentTimeMillis();
    }

    public void update() {
        long now = System.currentTimeMillis();
        long delta = now - lastUpdateTime;
        lastUpdateTime = now;

        sprite.update(delta);

        // lógica de movimento...
    }

    public void render(Graphics g) {
        sprite.render(g, x, y);
    }
}