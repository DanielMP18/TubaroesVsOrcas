package entity;

import game.Elemento;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

// imports para carregar e animar sprites
import sprites.SpriteSheet;
import sprites.Animation;
import sprites.AnimatedSprite;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * InimigoBasico - agora usa AnimatedSprite carregado do sprite sheet /sprites/golfinhoNormal.png
 * Mantive o construtor original (assinatura intacta) e usei carregamento lazy no drawInimigo para
 * evitar tocar em outras partes do código.
 */
public class InimigoBasico extends Inimigo {

    private AnimatedSprite sprite;
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoBasico(float x, float y, List<Point> caminho) {
        super(x, y, caminho, 200, 1.5f, 10, 32, 32);
        this.elemento = Elemento.FOGO; // <--- Elemento
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        // lazy-load do sprite sheet (carrega apenas na primeira vez que desenha)
        if (sprite == null) {
            try {
                BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/sprites/golfinhoNormal.png"));
                if (sheet != null) {
                    SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                    sprite = new AnimatedSprite(new Animation(ss.getSprites(), 120, true)); // 120 ms por frame
                }
            } catch (IOException e) {
                e.printStackTrace();
                sprite = null;
            } catch (NullPointerException npe) {
                // recurso não encontrado -> manter fallback
                sprite = null;
            }
        }

        // atualizar animação e desenhar
        long now = System.currentTimeMillis();
        long delta = now - lastSpriteTime;
        lastSpriteTime = now;

        if (sprite != null) {
            sprite.update(delta);
            sprite.render(g2, (int) x, (int) y);
        } else {
            // fallback visual (original)
            g2.setColor(Color.RED);
            g2.fillRect((int) x, (int) y, largura, altura);
        }
    }
}
