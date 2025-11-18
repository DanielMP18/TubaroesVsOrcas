package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class TorreLaser extends Torre {

    public static final int CUSTO = 180;

    public TorreLaser(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);
        
        // Stats Base (Nível 1)
        this.custo = CUSTO;
        this.alcance = 220;
        this.cadenciaDeTiro = 1_500_000_000L; // 1.5 segundos
        this.danoBase = 70;
        this.custoUpgrade = 120; // Custo Nv1 -> Nv2
    }

    @Override
    protected void atirar() {
        Projetil p = new Projetil(x, y, 12f, this.danoBase, alvo, Color.CYAN, this.elemento);
        projeteis.add(p);
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(50, 50, 150));
        g2.fillRect(col * tamanho, row * tamanho, tamanho, tamanho);
        g2.setColor(Color.CYAN);
        g2.fillOval(x - 8, y - 8, 16, 16);
    }
}
