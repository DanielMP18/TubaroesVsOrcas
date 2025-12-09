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
import java.io.File; // <--- Import Adicionado

public class InimigoTank extends Inimigo {

    private AnimatedSprite sprite;
    private boolean jaTentouCarregar = false;
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoTank(float x, float y, List<Point> caminho) {
        
        super(x, y, caminho, 750, 1.0f, 20, 36, 36); 
        this.elemento = Elemento.AGUA; 
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        if (sprite == null && !jaTentouCarregar) {
            jaTentouCarregar = true;
            try {
                // --- CORREÇÃO AQUI ---
                // Carrega "golfinhoTank.png" da pasta local
                File arquivoImagem = new File("res/sprites/golfinhoTank.png");
                
                if (!arquivoImagem.exists()) {
                    System.err.println("ERRO: Imagem não encontrada: " + arquivoImagem.getAbsolutePath());
                } else {
                    BufferedImage sheet = ImageIO.read(arquivoImagem);
                    if (sheet != null) {
                        SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                        // Mantive sua velocidade de animação: 140
                        sprite = new AnimatedSprite(new Animation(ss.getSprites(), 140, true)); 
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler golfinhoTank:");
                e.printStackTrace();
            }
        }

        long now = System.currentTimeMillis();
        long delta = now - lastSpriteTime;
        lastSpriteTime = now;

        if (sprite != null) {
            sprite.update(delta);
            int drawX = (int) x + 8;
            int drawY = (int) y + 8;
            sprite.render(g2, drawX, drawY);
        } else {
            // Se falhar, desenha o retângulo cinza escuro
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect((int) x, (int) y, largura, altura);
        }
    }
}
