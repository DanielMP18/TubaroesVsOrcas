package entity;

import game.Elemento;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

// imports para sprite/animation
import sprites.SpriteSheet;
import sprites.Animation;
import sprites.AnimatedSprite;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * InimigoRapido - agora usa AnimatedSprite carregado do sprite sheet /sprites/golfinhoRapido.png
 * Mantive construtor original; carregamento lazy no drawInimigo.
 */
public class InimigoRapido extends Inimigo {

    private AnimatedSprite sprite;
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoRapido(float x, float y, List<Point> caminho) {
        super(x, y, caminho, 100, 3.0f, 5, 24, 24);
        this.elemento = Elemento.ALGA; // <--- Elemento
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        if (sprite == null) {
            try {
                BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/sprites/golfinhoRapido.png"));
                if (sheet != null) {
                    SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                    sprite = new AnimatedSprite(new Animation(ss.getSprites(), 80, true)); // 80 ms por frame (mais rápido)
                }
            } catch (IOException e) {
                e.printStackTrace();
                sprite = null;
            } catch (NullPointerException npe) {
                sprite = null;
            }
        }

        long now = System.currentTimeMillis();
        long delta = now - lastSpriteTime;
        lastSpriteTime = now;

        if (sprite != null) {
            sprite.update(delta);
            sprite.render(g2, (int) x, (int) y);
        } else {
            // fallback original (círculo amarelo)
            g2.setColor(Color.YELLOW);
            g2.fillOval((int) x, (int) y, largura, altura);
        }
    }
}
