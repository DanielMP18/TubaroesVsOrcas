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

public class TorreLaser extends Torre {

    public static final int CUSTO = 180;

    private AnimatedSprite baseSprite;
    private AnimatedSprite evolveSprite;
    private AnimatedSprite finalSprite;
    
    private boolean upgrading = false;
    private int pendingLevel = -1;
    private long lastSpriteTime = System.currentTimeMillis();

    public TorreLaser(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);

        this.custo = CUSTO;
        this.alcance = 220;
        this.cadenciaDeTiro = 1_500_000_000L; 
        this.danoBase = 70;
        this.custoUpgrade = 120; 

        try {
            // 1. OVO
            BufferedImage base = ImageIO.read(getClass().getResourceAsStream("/sprites/ovoSniper.png"));
            if (base != null) {
                SpriteSheet ss = new SpriteSheet(base, 32, 32);
                baseSprite = new AnimatedSprite(new Animation(new BufferedImage[] { ss.getSprite(0, 0) }, 1000, true));
            }

            // 2. EVOLUÇÃO
            BufferedImage evo;
            try {
                evo = ImageIO.read(getClass().getResourceAsStream("/sprites/evolve1.png"));
            } catch (Exception e) {
                evo = ImageIO.read(getClass().getResourceAsStream("/sprites/evolve3.png"));
            }
            if (evo != null) {
                SpriteSheet ess = new SpriteSheet(evo, 32, 32);
                evolveSprite = new AnimatedSprite(new Animation(ess.getSprites(), 100, false));
            }
            
            // 3. TUBARÃO FINAL
            BufferedImage finalImg = ImageIO.read(getClass().getResourceAsStream("/sprites/tubaraoSniper.png"));
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
        Projetil p = new Projetil(x, y, 12f, this.danoBase, alvo, Color.CYAN, this.elemento);
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
        
        // Offset para centralizar
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
            g2.setColor(Color.BLUE);
            g2.fillRect(tileX, tileY, tamanho, tamanho);
        }
    }
}
