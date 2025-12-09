package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import sprites.SpriteSheet;
import sprites.Animation;
import sprites.AnimatedSprite;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

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
            carregarSprite("res/sprites/ovoSniper.png", 1);
            if (!carregarSprite("res/sprites/evolve1.png", 2)) {
                carregarSprite("res/sprites/evolve3.png", 2);
            }
            carregarSprite("res/sprites/tubaraoSniper.png", 3);
        } catch (Exception e) {
            System.err.println("ERRO Torre Laser: " + e.getMessage());
        }
    }
    
    private boolean carregarSprite(String path, int tipo) {
        try {
            File arquivo = new File(path);
            if (!arquivo.exists()) return false;
            
            BufferedImage img = ImageIO.read(arquivo);
            if (img == null) return false;

            SpriteSheet ss = new SpriteSheet(img, 32, 32);
            
            // CORREÇÃO: Todos os tipos agora usam animação completa
            if (tipo == 1) baseSprite = new AnimatedSprite(new Animation(ss.getSprites(), 150, true));
            else if (tipo == 2) evolveSprite = new AnimatedSprite(new Animation(ss.getSprites(), 100, false));
            else if (tipo == 3) finalSprite = new AnimatedSprite(new Animation(ss.getSprites(), 150, true));
            return true;
        } catch (Exception e) { return false; }
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
        int offset = (tamanho - 32) / 2;

        if (upgrading && evolveSprite != null) {
            evolveSprite.update(delta);
            evolveSprite.render(g2, tileX + offset, tileY + offset);
            if (evolveSprite.isFinished()) {
                this.setLevel(pendingLevel);
                pendingLevel = -1;
                upgrading = false;
            }
        } else if (getLevel() >= 2 && finalSprite != null) {
            finalSprite.update(delta);
            finalSprite.render(g2, tileX + offset, tileY + offset);
        } else if (baseSprite != null) {
            baseSprite.update(delta);
            baseSprite.render(g2, tileX + offset, tileY + offset);
        } else {
            g2.setColor(Color.BLUE);
            g2.fillRect(tileX, tileY, tamanho, tamanho);
            g2.setColor(Color.BLACK);
            g2.drawRect(tileX, tileY, tamanho, tamanho);
        }
    }
}
