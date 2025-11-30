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

public class InimigoTank extends Inimigo {

    private AnimatedSprite sprite;
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoTank(float x, float y, List<Point> caminho) {
        super(x, y, caminho, 80, 1.0f, 20, 36, 36); 
        this.elemento = Elemento.AGUA; 
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        if (sprite == null) {
            try {
                BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/sprites/golfinhoTank.png"));
                if (sheet != null) {
                    SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                    sprite = new AnimatedSprite(new Animation(ss.getSprites(), 140, true)); 
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
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect((int) x, (int) y, largura, altura);
        }
    }
}
