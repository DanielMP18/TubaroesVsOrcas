package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class TorreEspada extends Torre {

    public static final int CUSTO = 70;

    public TorreEspada(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        super(col, row, tamanho, inimigos, projeteis);
        
        // Stats Base (Nível 1)
        this.custo = CUSTO;
        this.alcance = 120;
        this.cadenciaDeTiro = 200_000_000L; // 0.2 segundos
        this.danoBase = 7;
        this.custoUpgrade = 50; // Custo Nv1 -> Nv2
    }

    @Override
    protected void atirar() {
        Projetil p = new Projetil(x, y, 8f, this.danoBase, alvo, Color.GRAY, this.elemento);
        projeteis.add(p);
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(157, 46, 0));
        g2.fillOval(col * tamanho, row * tamanho, tamanho, tamanho);
        g2.setColor(Color.GRAY);
        g2.fillRect(x - 5, y - 5, 10, 10);
    }
}