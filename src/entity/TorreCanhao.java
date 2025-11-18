package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class TorreCanhao extends Torre {

    public static final int CUSTO = 100;

    public TorreCanhao(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);
        
        // Stats Base (Nível 1)
        this.custo = CUSTO;
        this.alcance = 180;
        this.cadenciaDeTiro = 800_000_000L; // 0.8 segundos
        this.danoBase = 25; 
        this.custoUpgrade = 75; // Custo Nv1 -> Nv2
    }

    @Override
    protected void atirar() {
        // Passa o elemento da torre (que será NEUTRO ou FOGO/AGUA/ALGA)
        Projetil p = new Projetil(x, y, 8f, this.danoBase, alvo, Color.ORANGE, this.elemento);
        projeteis.add(p);
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 150, 0));
        g2.fillOval(col * tamanho, row * tamanho, tamanho, tamanho);
        g2.setColor(Color.GRAY);
        g2.fillRect(x - 5, y - 5, 10, 10);
    }
}
