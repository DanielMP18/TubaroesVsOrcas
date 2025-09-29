package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public class Inimigo {

    private float x, y;
    private int vida;
    private float velocidade;
    private boolean ativo = true; // Flag para saber se o inimigo está no jogo

    // Lógica para seguir o caminho
    private List<Point> caminho;
    private int pontoAlvoIndex = 0;

    public Inimigo(float x, float y, List<Point> caminho) {
        this.x = x;
        this.y = y;
        this.vida = 100; // Vida inicial
        this.velocidade = 1.5f; // Pixels por tick
        this.caminho = caminho;
    }

    // Método para atualizar a lógica do inimigo (movimento)
    public void update() {
        if (!ativo || pontoAlvoIndex >= caminho.size()) {
            return; // Se não estiver ativo ou já terminou o caminho, não faz nada
        }

        Point alvo = caminho.get(pontoAlvoIndex);

        float alvoX = alvo.x;
        float alvoY = alvo.y;

        // Calcula a direção para o próximo ponto
        float angulo = (float) Math.atan2(alvoY - y, alvoX - x);

        // Move o inimigo na direção calculada
        x += velocidade * Math.cos(angulo);
        y += velocidade * Math.sin(angulo);

        // Verifica se o inimigo alcançou (ou passou) o ponto alvo
        if (Math.sqrt(Math.pow(alvoX - x, 2) + Math.pow(alvoY - y, 2)) < velocidade) {
            pontoAlvoIndex++; // Define o próximo ponto do caminho como alvo
        }
    }

    // Método para desenhar o inimigo na tela
    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED); // Inimigos serão vermelhos por enquanto
        g2.fillRect((int)x, (int)y, 32, 32);
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