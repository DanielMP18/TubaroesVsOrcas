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

public class InimigoBasico extends Inimigo {

    private AnimatedSprite sprite;
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoBasico(float x, float y, List<Point> caminho) {
        super(x, y, caminho, 200, 1.5f, 10, 32, 32);
        this.elemento = Elemento.FOGO; 
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        if (sprite == null) {
            try {
                BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/sprites/golfinhoNormal.png"));
                if (sheet != null) {
                    SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                    sprite = new AnimatedSprite(new Animation(ss.getSprites(), 120, true));
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
            
            // Centralização: (48px do Tile - 32px do Sprite) / 2 = 8
            // Ajuste fino: Se a posição x,y do inimigo for o topo-esquerda do tile
            int drawX = (int) x + 8;
            int drawY = (int) y + 8;
            
            sprite.render(g2, drawX, drawY); 
        } else {
            g2.setColor(Color.RED);
            g2.fillRect((int) x, (int) y, largura, altura);
        }
    }
}
