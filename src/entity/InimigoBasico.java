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
import java.io.File; // <--- Importante: Adicionamos isso para ler o arquivo da pasta

public class InimigoBasico extends Inimigo {

    private AnimatedSprite sprite;
    private boolean jaTentouCarregar = false; 
    private long lastSpriteTime = System.currentTimeMillis();

    public InimigoBasico(float x, float y, List<Point> caminho) {
        // Ajuste os atributos aqui se precisar (vida, velocidade, recompensa, tamanho)
        super(x, y, caminho, 200, 1.5f, 10, 32, 32);
        this.elemento = Elemento.FOGO; 
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        // Tenta carregar a imagem apenas uma vez
        if (sprite == null && !jaTentouCarregar) {
            jaTentouCarregar = true;
            try {
                // --- MUDANÇA AQUI ---
                // Agora procuramos na pasta "res" que está na raiz do projeto.
                // Note que NÃO tem a barra "/" no começo.
                File arquivoImagem = new File("res/sprites/golfinhoNormal.png");
                
                if (!arquivoImagem.exists()) {
                    System.err.println("ERRO CRÍTICO: Imagem não encontrada!");
                    System.err.println("O jogo procurou em: " + arquivoImagem.getAbsolutePath());
                } else {
                    BufferedImage sheet = ImageIO.read(arquivoImagem);
                    if (sheet != null) {
                        // Cria a animação (32x32 é o tamanho de cada quadro do golfinho)
                        SpriteSheet ss = new SpriteSheet(sheet, 32, 32);
                        sprite = new AnimatedSprite(new Animation(ss.getSprites(), 120, true));
                    }
                }
                // --------------------
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo de imagem:");
                e.printStackTrace();
            }
        }

        // Lógica de tempo para animação
        long now = System.currentTimeMillis();
        long delta = now - lastSpriteTime;
        lastSpriteTime = now;

        if (sprite != null) {
            // Se a imagem carregou, desenha o golfinho
            sprite.update(delta);
            int drawX = (int) x + 8; // Centraliza um pouco se necessário
            int drawY = (int) y + 8;
            sprite.render(g2, drawX, drawY); 
        } else {
            // Se NÃO carregou (deu erro), desenha o quadrado vermelho para o jogo não travar
            g2.setColor(Color.RED);
            g2.fillRect((int) x, (int) y, largura, altura);
        }
    }
}
