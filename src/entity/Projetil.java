package entity;

import java.awt.Color;
import java.awt.Graphics2D;

public class Projetil {

    private float x, y;
    private float velocidade;
    private int dano;
    private Inimigo alvo;
    private boolean ativo = true;
    private Color cor;

    public Projetil(float x, float y, float velocidade, int dano, Inimigo alvo, Color cor) {
        this.x = x;
        this.y = y;
        this.velocidade = velocidade;
        this.dano = dano;
        this.alvo = alvo;
        this.cor = cor;
    }

    public void update() {
        if (!ativo || alvo == null || !alvo.isAtivo()) {
            desativar();
            return;
        }

        float alvoX = alvo.getX() + 16;
        float alvoY = alvo.getY() + 16;

        float angulo = (float) Math.atan2(alvoY - y, alvoX - x);

        x += velocidade * Math.cos(angulo);
        y += velocidade * Math.sin(angulo);

        if (Math.sqrt(Math.pow(alvoX - x, 2) + Math.pow(alvoY - y, 2)) < velocidade) {
            alvo.levarDano(dano);
            desativar();
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(cor);
        g2.fillOval((int) x - 4, (int) y - 4, 8, 8);
    }

    public void desativar() {
        this.ativo = false;
    }

    public boolean isAtivo() {
        return ativo;
    }
}