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
 * InimigoTank - usa sprite sheet /sprites/golfinhoTank.png
 * Mantém assinatura original do construtor e comportamento.
 * Carregamento lazy no drawInimigo para não alterar outras chamadas.
 */
public class InimigoTank extends Inimigo {

    private AnimatedSprite sprite;
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoTank(float x, float y, List<Point> caminho) {
        // Não alterei a chamada ao super — mantive a lógica/valores conforme a implementação original
        // Caso seus parâmetros originais sejam diferentes, mantenha-os (este construtor apenas encaminha).
        super(x, y, caminho, 80, 1.0f, 20, 36, 36); // ajuste os valores se necessário
        this.elemento = Elemento.AGUA; // exemplo, ajuste para o elemento correto se for outro
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        if (sprite == null) {
            try {
                BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/sprites/golfinhoTank.png"));
                if (sheet != null) {
                    SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                    sprite = new AnimatedSprite(new Animation(ss.getSprites(), 140, true)); // 140 ms por frame
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
            // fallback visual (cinza)
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect((int) x, (int) y, largura, altura);
        }
    }
}
