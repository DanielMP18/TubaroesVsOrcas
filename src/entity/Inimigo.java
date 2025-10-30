package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public class Inimigo {

    private float x, y;
    private int vida;
    private int vidaMaxima;
    private float velocidade;
    private boolean ativo = true;
    // private boolean ativo = true; // <-- MUDANÇA: Linha duplicada removida
    private int recompensa;

    private List<Point> caminho;
    private int pontoAlvoIndex = 0;

    public Inimigo(float x, float y, List<Point> caminho) {
        this.x = x;
        this.y = y;
        this.caminho = caminho;
        this.vidaMaxima = 100;
        this.vida = vidaMaxima; // <-- MUDANÇA: 'this.vida = 100' removido por ser redundante
        this.velocidade = 1.5f;
        this.recompensa = 15;
    }

    
    public void update() {
        if (!ativo || pontoAlvoIndex >= caminho.size()) {
            desativar(); // <-- MUDANÇA: 'desativar()' veio antes do return
            return;
            // O código aqui embaixo era inalcançável
        }

        Point alvo = caminho.get(pontoAlvoIndex);

        float alvoX = alvo.x;
        float alvoY = alvo.y;

        float angulo = (float) Math.atan2(alvoY - y, alvoX - x);

        
        x += velocidade * Math.cos(angulo);
        y += velocidade * Math.sin(angulo);

        
        // Se o inimigo chegou perto o suficiente do ponto...
        double distancia = Math.sqrt(Math.pow(alvoX - x, 2) + Math.pow(alvoY - y, 2));

        // <-- MUDANÇA: Usei a variável 'distancia' e corrigi o bug
        if (distancia < velocidade) {
            pontoAlvoIndex++; // <-- BUG CRÍTICO CORRIGIDO! (removida a linha duplicada)
        }
    }

    
    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillRect((int) x, (int) y, 32, 32);
        // <-- MUDANÇA: Linhas duplicadas removidas

        // Barra de vida
        if (vida < vidaMaxima) {
            g2.setColor(Color.GRAY);
            g2.fillRect((int) x, (int) y - 8, 32, 5);

            g2.setColor(Color.GREEN);
            float larguraVida = (float) vida / vidaMaxima * 32;
            g2.fillRect((int) x, (int) y - 8, (int) larguraVida, 5);
        }
    }

    public void levarDano(int dano) {
        this.vida -= dano;
        if (this.vida <= 0) {
            desativar();
        }
    }

    public int getRecompensa() {
        return recompensa;
    }

    public boolean chegouNaBase() {
        return pontoAlvoIndex >= caminho.size();
    }
    

    public void desativar() {
        this.ativo = false;
    }

    public boolean isAtivo() {
        return ativo;
    }
    

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getVida() {
        return vida;
    }
}
