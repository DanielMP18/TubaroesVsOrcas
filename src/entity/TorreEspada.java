package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import sprites.SpriteSheet;
import sprites.Animation;
import sprites.AnimatedSprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TorreEspada extends Torre {

    public static final int CUSTO = 120;

    private AnimatedSprite baseSprite;
    private AnimatedSprite evolveSprite;
    private AnimatedSprite finalSprite;
    
    private boolean upgrading = false;
    private int pendingLevel = -1;
    private long lastSpriteTime = System.currentTimeMillis();

    public TorreEspada(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);

        this.custo = CUSTO;
        this.alcance = 100;
        this.cadenciaDeTiro = 600_000_000L; 
        this.danoBase = 10;
        this.custoUpgrade = 90;

        try {
            // 1. OVO
            BufferedImage base = ImageIO.read(getClass().getResourceAsStream("/sprites/ovoLixa.png"));
            if (base != null) {
                SpriteSheet ss = new SpriteSheet(base, 32, 32);
                baseSprite = new AnimatedSprite(new Animation(new BufferedImage[] { ss.getSprite(0, 0) }, 1000, true));
            }

            // 2. EVOLUÇÃO (Tenta evolve2, se falhar usa evolve3)
            BufferedImage evo;
            try {
                evo = ImageIO.read(getClass().getResourceAsStream("/sprites/evolve2.png"));
            } catch (Exception e) {
                evo = ImageIO.read(getClass().getResourceAsStream("/sprites/evolve3.png"));
            }
            if (evo != null) {
                SpriteSheet ess = new SpriteSheet(evo, 32, 32);
                evolveSprite = new AnimatedSprite(new Animation(ess.getSprites(), 100, false));
            }
            
            // 3. TUBARÃO FINAL
            BufferedImage finalImg = ImageIO.read(getClass().getResourceAsStream("/sprites/tubaraoLixa.png"));
            if (finalImg != null) {
                SpriteSheet fss = new SpriteSheet(finalImg, 32, 32);
                finalSprite = new AnimatedSprite(new Animation(fss.getSprites(), 150, true));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void atirar() {
        Projetil p = new Projetil(x, y, 10f, this.danoBase, alvo, Color.WHITE, this.elemento);
        projeteis.add(p);
    }

    @Override
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

        int tileX = col * tamanho;
        int tileY = row * tamanho;
        
        // Offset para centralizar 32px em 48px
        int offset = (tamanho - 32) / 2;

        if (upgrading && evolveSprite != null) {
            evolveSprite.update(delta);
            evolveSprite.render(g2, tileX + offset, tileY + offset);
            if (evolveSprite.isFinished()) {
                this.setLevel(pendingLevel);
                pendingLevel = -1;
                upgrading = false;
            }
        } 
        else if (getLevel() >= 2 && finalSprite != null) {
            finalSprite.update(delta);
            finalSprite.render(g2, tileX + offset, tileY + offset);
        } 
        else if (baseSprite != null) {
            baseSprite.update(delta);
            baseSprite.render(g2, tileX + offset, tileY + offset);
        } 
        else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(tileX, tileY, tamanho, tamanho);
        }
    }
}
