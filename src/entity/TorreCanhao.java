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

public class TorreCanhao extends Torre {

    public static final int CUSTO = 100;

    private AnimatedSprite baseSprite;   
    private AnimatedSprite evolveSprite; 
    private AnimatedSprite finalSprite;  
    
    private boolean upgrading = false;
    private int pendingLevel = -1;
    private long lastSpriteTime = System.currentTimeMillis();

    public TorreCanhao(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);
        this.custo = CUSTO;
        this.alcance = 200;
        this.cadenciaDeTiro = 800_000_000L; 
        this.danoBase = 25;
        this.custoUpgrade = 75; 

        try {
            carregarBase();
            carregarEvolucao();
            carregarFinal();
        } catch (Exception e) {
            System.err.println("ERRO ao carregar sprites da Torre Canhão: " + e.getMessage());
        }
    }

    private void carregarBase() throws Exception {
        File arquivo = new File("res/sprites/ovoTub1.png");
        if (arquivo.exists()) {
            BufferedImage img = ImageIO.read(arquivo);
            if (img != null) {
                SpriteSheet ss = new SpriteSheet(img, 32, 32);
                // CORREÇÃO: ss.getSprites() pega a animação toda agora
                baseSprite = new AnimatedSprite(new Animation(ss.getSprites(), 150, true));
            }
        }
    }

    private void carregarEvolucao() throws Exception {
        File arquivo = new File("res/sprites/evolve3.png");
        if (arquivo.exists()) {
            BufferedImage img = ImageIO.read(arquivo);
            if (img != null) {
                SpriteSheet ss = new SpriteSheet(img, 32, 32);
                evolveSprite = new AnimatedSprite(new Animation(ss.getSprites(), 100, false));
            }
        }
    }

    private void carregarFinal() throws Exception {
        File arquivo = new File("res/sprites/tubarao1.png");
        if (arquivo.exists()) {
            BufferedImage img = ImageIO.read(arquivo);
            if (img != null) {
                SpriteSheet ss = new SpriteSheet(img, 32, 32);
                finalSprite = new AnimatedSprite(new Animation(ss.getSprites(), 150, true));
            }
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
            g2.setColor(Color.GREEN);
            g2.fillRect(tileX, tileY, tamanho, tamanho);
            g2.setColor(Color.BLACK);
            g2.drawRect(tileX, tileY, tamanho, tamanho);
        }
    }
}
