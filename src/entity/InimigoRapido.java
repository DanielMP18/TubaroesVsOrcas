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

public class InimigoRapido extends Inimigo {

    private AnimatedSprite sprite;
    private boolean jaTentouCarregar = false;
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoRapido(float x, float y, List<Point> caminho) {
        // Mantive seus valores originais: vida 100, velocidade 3.0f, recompensa 5
        super(x, y, caminho, 100, 3.0f, 5, 24, 24);
        this.elemento = Elemento.ALGA; 
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        if (sprite == null && !jaTentouCarregar) {
            jaTentouCarregar = true;
            try {
                // --- CORREÇÃO AQUI ---
                // Carrega "golfinhoRapido.png" da pasta local
                File arquivoImagem = new File("res/sprites/golfinhoRapido.png");
                
                if (!arquivoImagem.exists()) {
                    System.err.println("ERRO: Imagem não encontrada: " + arquivoImagem.getAbsolutePath());
                } else {
                    BufferedImage sheet = ImageIO.read(arquivoImagem);
                    if (sheet != null) {
                        SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                        // Mantive sua velocidade de animação: 80
                        sprite = new AnimatedSprite(new Animation(ss.getSprites(), 80, true));
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler golfinhoRapido:");
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
            // Se falhar, desenha a bolinha amarela
            g2.setColor(Color.YELLOW);
            g2.fillOval((int) x, (int) y, largura, altura);
        }
    }
}
