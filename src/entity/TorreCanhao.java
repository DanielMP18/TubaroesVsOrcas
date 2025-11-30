package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

// novos imports para sprites
import sprites.SpriteSheet;
import sprites.Animation;
import sprites.AnimatedSprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * TorreCanhao (modificada para suportar sprites 32x32 e animação de evolução)
 * Mantive toda lógica de atirar e stats tal qual o arquivo original.
 * Apenas substituí o render (draw) para desenhar sprites e adicionei método startUpgrade().
 */
public class TorreCanhao extends Torre {

    public static final int CUSTO = 100;

    // campos novos para sprites
    private AnimatedSprite baseSprite;
    private AnimatedSprite evolveSprite;
    private boolean upgrading = false;
    private int pendingLevel = -1;
    private long lastSpriteTime = System.currentTimeMillis();

    public TorreCanhao(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);

        // Stats Base (Nível 1)
        this.custo = CUSTO;
        this.alcance = 180;
        this.cadenciaDeTiro = 800_000_000L; // 0.8 segundos
        this.danoBase = 25;
        this.custoUpgrade = 75; // Custo Nv1 -> Nv2

        // carregar sprites (pré-evolução e evolução)
        try {
            BufferedImage base = ImageIO.read(getClass().getResourceAsStream("/sprites/ovoTub1.png"));
            if (base != null) {
                SpriteSheet ss = new SpriteSheet(base, 32, 32);
                // usamos o primeiro frame como idle
                baseSprite = new AnimatedSprite(new Animation(new BufferedImage[] { ss.getSprite(0, 0) }, 1000, true));
            }

            BufferedImage evo = ImageIO.read(getClass().getResourceAsStream("/sprites/evolve3.png")); // evolve3 -> tub1
            if (evo != null) {
                SpriteSheet ess = new SpriteSheet(evo, 32, 32);
                evolveSprite = new AnimatedSprite(new Animation(ess.getSprites(), 80, false));
            }
        } catch (IOException e) {
            e.printStackTrace();
            baseSprite = null;
            evolveSprite = null;
        }
    }

    @Override
    protected void atirar() {
        // Passa o elemento da torre (que será NEUTRO ou FOGO/AGUA/ALGA)
        Projetil p = new Projetil(x, y, 8f, this.danoBase, alvo, Color.ORANGE, this.elemento);
        projeteis.add(p);
    }

    /**
     * Inicia o processo de upgrade visual. targetLevel deve ser > level atual.
     * Chame este método quando jogador comprar a evolução.
     */
    public void startUpgrade(int targetLevel) {
        if (upgrading) return;
        if (targetLevel <= this.getLevel()) return;
        pendingLevel = targetLevel;
        upgrading = true;
        if (evolveSprite != null) evolveSprite.reset();
    }

    @Override
    public void draw(Graphics2D g2) {
        // atualiza sprite com delta calculado aqui para não precisar alterar o loop principal
        long now = System.currentTimeMillis();
        long delta = now - lastSpriteTime;
        lastSpriteTime = now;

        if (upgrading && evolveSprite != null) {
            evolveSprite.update(delta);
            // desenha frame atual da animação de evolução
            // posiciona no tile (col * tamanho, row * tamanho) para manter comportamento original
            evolveSprite.render(g2, col * tamanho, row * tamanho);
            if (evolveSprite.isFinished()) {
                // aplicar mudanças de nível (apenas marca o fim da animação; a lógica do level
                // e estatísticas de upgrade devem ser tratadas pela lógica existente do jogo)
                this.setLevel(pendingLevel); // se Torre tiver setLevel; caso não, remova esta linha
                pendingLevel = -1;
                upgrading = false;
                // opcional: trocar baseSprite para um novo idle correspondente ao novo nível
            }
        } else if (baseSprite != null) {
            baseSprite.update(delta);
            baseSprite.render(g2, col * tamanho, row * tamanho);
        } else {
            // fallback: manter desenho original (círculo + quadrado)
            g2.setColor(new Color(0, 150, 0));
            g2.fillOval(col * tamanho, row * tamanho, tamanho, tamanho);
            g2.setColor(Color.GRAY);
            g2.fillRect(x - 5, y - 5, 10, 10);
        }
    }
}
