package entity;

import game.Elemento; // Importar o Enum
import java.awt.Color;
import java.awt.Graphics2D;

public class Projetil {

    private float x, y;
    private float velocidade;
    private int dano;
    private Inimigo alvo;
    private boolean ativo = true;
    private Color cor;
    
    private Elemento elemento; // Armazena o elemento do projétil

    public Projetil(float x, float y, float velocidade, int dano, Inimigo alvo, Color cor, Elemento elemento) {
        this.x = x;
        this.y = y;
        this.velocidade = velocidade;
        this.dano = dano;
        this.alvo = alvo;
        this.cor = cor;
        this.elemento = elemento; // Armazena o elemento
    }

    public void update() {
        if (!ativo || alvo == null || !alvo.isAtivo()) {
            desativar();
            return;
        }
        
        // Mira no centro do inimigo
        float alvoX = alvo.getX() + ((float) alvo.getLargura() / 2);
        float alvoY = alvo.getY() + ((float) alvo.getAltura() / 2);

        float angulo = (float) Math.atan2(alvoY - y, alvoX - x);

        x += velocidade * Math.cos(angulo);
        y += velocidade * Math.sin(angulo);

        // Checa colisão
        if (Math.sqrt(Math.pow(alvoX - x, 2) + Math.pow(alvoY - y, 2)) < velocidade) {
            // --- APLICA O DANO E O EFEITO ---
            alvo.levarDano(dano, this.elemento); 
            desativar();
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(cor);
        g2.fillOval((int) x - 4, (int) y - 4, 8, 8);
    }

    public void desativar() { this.ativo = false; }
    public boolean isAtivo() { return ativo; }
}
