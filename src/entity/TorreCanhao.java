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

public class TorreCanhao extends Torre {

    public static final int CUSTO = 100;

    private AnimatedSprite baseSprite;   // OVO
    private AnimatedSprite evolveSprite; // ANIMAÇÃO DE EVOLUÇÃO
    private AnimatedSprite finalSprite;  // TUBARÃO FINAL
    
    private boolean upgrading = false;
    private int pendingLevel = -1;
    private long lastSpriteTime = System.currentTimeMillis();

    public TorreCanhao(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);

        this.custo = CUSTO;
        this.alcance = 180;
        this.cadenciaDeTiro = 800_000_000L; 
        this.danoBase = 25;
        this.custoUpgrade = 75; 

        try {
            // 1. OVO
            BufferedImage base = ImageIO.read(getClass().getResourceAsStream("/sprites/ovoTub1.png"));
            if (base != null) {
                SpriteSheet ss = new SpriteSheet(base, 32, 32);
                baseSprite = new AnimatedSprite(new Animation(new BufferedImage[] { ss.getSprite(0, 0) }, 1000, true));
            }

            // 2. EVOLUÇÃO (Usa evolve3 se não tiver específico)
            BufferedImage evo = ImageIO.read(getClass().getResourceAsStream("/sprites/evolve3.png"));
            if (evo != null) {
                SpriteSheet ess = new SpriteSheet(evo, 32, 32);
                evolveSprite = new AnimatedSprite(new Animation(ess.getSprites(), 100, false));
            }
            
            // 3. TUBARÃO FINAL
            BufferedImage finalImg = ImageIO.read(getClass().getResourceAsStream("/sprites/tubarao1.png"));
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
        Projetil p = new Projetil(x, y, 8f, this.danoBase, alvo, Color.ORANGE, this.elemento);
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

        // Posição base do Tile (48x48)
        int tileX = col * tamanho;
        int tileY = row * tamanho;

        // CÁLCULO DE CENTRALIZAÇÃO: (48 - 32) / 2 = 8 pixels de margem
        int offset = (tamanho - 32) / 2;

        if (upgrading && evolveSprite != null) {
            evolveSprite.update(delta);
            // Desenha com offset, tamanho original (32x32)
            evolveSprite.render(g2, tileX + offset, tileY + offset);
            
            if (evolveSprite.isFinished()) {
                this.setLevel(pendingLevel);
                pendingLevel = -1;
                upgrading = false;
            }
        } 
        else if (getLevel() >= 2 && finalSprite != null) {
            // Desenha Tubarão
            finalSprite.update(delta);
            finalSprite.render(g2, tileX + offset, tileY + offset);
        } 
        else if (baseSprite != null) {
            // Desenha Ovo
            baseSprite.update(delta);
            baseSprite.render(g2, tileX + offset, tileY + offset);
        } 
        else {
            // Fallback se imagem falhar
            g2.setColor(Color.GREEN);
            g2.fillRect(tileX, tileY, tamanho, tamanho);
        }
    }
}
