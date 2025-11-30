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
 * TorreEspada (modificada para suportar sprites 32x32 e animação de evolução)
 * Mantive a lógica original de atirar/estatísticas (ajuste se quiser valores diferentes).
 */
public class TorreEspada extends Torre {

    public static final int CUSTO = 120; // ajuste se sua versão original for diferente

    // campos de sprite
    private AnimatedSprite baseSprite;
    private AnimatedSprite evolveSprite;
    private boolean upgrading = false;
    private int pendingLevel = -1;
    private long lastSpriteTime = System.currentTimeMillis();

    public TorreEspada(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);

        // Stats base (adapte se necessário para corresponder ao original)
        this.custo = CUSTO;
        this.alcance = 100;
        this.cadenciaDeTiro = 600_000_000L; // 0.6s
        this.danoBase = 10;
        this.custoUpgrade = 90;

        // carregar sprites
        try {
            BufferedImage base = ImageIO.read(getClass().getResourceAsStream("/sprites/ovoLixa.png"));
            if (base != null) {
                SpriteSheet ss = new SpriteSheet(base, 32, 32);
                baseSprite = new AnimatedSprite(new Animation(new BufferedImage[] { ss.getSprite(0, 0) }, 1000, true));
            }

            BufferedImage evo = ImageIO.read(getClass().getResourceAsStream("/sprites/evolve2.png")); // evolve2 -> lixa/espada
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
        // lógica simples de projétil para TorreEspada (ajuste conforme seu original)
        Projetil p = new Projetil(x, y, 10f, this.danoBase, alvo, Color.WHITE, this.elemento);
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
                this.setLevel(pendingLevel); // se Torre tiver setLevel; caso não, remova/adapte
                pendingLevel = -1;
                upgrading = false;
            }
        } else if (baseSprite != null) {
            baseSprite.update(delta);
            baseSprite.render(g2, col * tamanho, row * tamanho);
        } else {
            // fallback desenho original (verde/retângulo)
            g2.setColor(new Color(100, 100, 100));
            g2.fillRect(col * tamanho, row * tamanho, tamanho, tamanho);
        }
    }
}
