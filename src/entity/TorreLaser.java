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
 * TorreLaser (modificada para suportar sprites 32x32 e animação de evolução)
 * Mantive a lógica de atirar e stats originais. Alterei apenas o desenho para usar sprites
 * e adicionei startUpgrade().
 */
public class TorreLaser extends Torre {

    public static final int CUSTO = 180;

    // campos novos para sprites
    private AnimatedSprite baseSprite;
    private AnimatedSprite evolveSprite;
    private boolean upgrading = false;
    private int pendingLevel = -1;
    private long lastSpriteTime = System.currentTimeMillis();

    public TorreLaser(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);

        // Stats Base (Nível 1)
        this.custo = CUSTO;
        this.alcance = 220;
        this.cadenciaDeTiro = 1_500_000_000L; // 1.5 segundos
        this.danoBase = 70;
        this.custoUpgrade = 120; // Custo Nv1 -> Nv2

        // carregar sprites (pré-evolução e evolução)
        try {
            BufferedImage base = ImageIO.read(getClass().getResourceAsStream("/sprites/ovoSniper.png"));
            if (base != null) {
                SpriteSheet ss = new SpriteSheet(base, 32, 32);
                baseSprite = new AnimatedSprite(new Animation(new BufferedImage[] { ss.getSprite(0, 0) }, 1000, true));
            }

            BufferedImage evo = ImageIO.read(getClass().getResourceAsStream("/sprites/evolve1.png")); // evolve1 -> sniper
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
        Projetil p = new Projetil(x, y, 12f, this.danoBase, alvo, Color.CYAN, this.elemento);
        projeteis.add(p);
    }

    public void startUpgrade(int targetLevel) {
        if (upgrading) return;
        if (targetLevel <= this.getLevel()) return;
        pendingLevel = targetLevel;
        upgrading = true;
        if (evolveSprite != null) evolveSprite.reset();
    }

    @Override
    public void draw(Graphics2D g2) {
        long now = System.currentTimeMillis();
        long delta = now - lastSpriteTime;
        lastSpriteTime = now;

        if (upgrading && evolveSprite != null) {
            evolveSprite.update(delta);
            evolveSprite.render(g2, col * tamanho, row * tamanho);
            if (evolveSprite.isFinished()) {
                this.setLevel(pendingLevel); // se Torre tiver setLevel; caso não, remova ou adapte
                pendingLevel = -1;
                upgrading = false;
            }
        } else if (baseSprite != null) {
            baseSprite.update(delta);
            baseSprite.render(g2, col * tamanho, row * tamanho);
        } else {
            // fallback desenho original
            g2.setColor(new Color(50, 50, 150));
            g2.fillRect(col * tamanho, row * tamanho, tamanho, tamanho);
            g2.setColor(Color.CYAN);
            g2.fillOval(x - 8, y - 8, 16, 16);
        }
    }
}
