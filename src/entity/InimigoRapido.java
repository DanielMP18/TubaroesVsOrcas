package entity;

import game.Elemento;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import sprites.SpriteSheet;
import sprites.Animation;
import sprites.AnimatedSprite;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class InimigoRapido extends Inimigo {

    private AnimatedSprite sprite;
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoRapido(float x, float y, List<Point> caminho) {
        // Largura/Altura física = 24, Sprite = 32. 
        super(x, y, caminho, 100, 3.0f, 5, 24, 24);
        this.elemento = Elemento.ALGA; 
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        if (sprite == null) {
            try {
                BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/sprites/golfinhoRapido.png"));
                if (sheet != null) {
                    SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                    sprite = new AnimatedSprite(new Animation(ss.getSprites(), 80, true));
                }
            } catch (IOException | NullPointerException e) {
                sprite = null;
            }
        }

        long now = System.currentTimeMillis();
        long delta = now - lastSpriteTime;
        lastSpriteTime = now;

        if (sprite != null) {
            sprite.update(delta);
            
            // Centralização no tile de 48px
            int drawX = (int) x + 8;
            int drawY = (int) y + 8;
            
            sprite.render(g2, drawX, drawY);
        } else {
            g2.setColor(Color.YELLOW);
            g2.fillOval((int) x, (int) y, largura, altura);
        }
    }
}
