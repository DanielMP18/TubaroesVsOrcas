package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;


public abstract class Inimigo {

    protected float x, y;
    protected int vida;
    protected int vidaMaxima;
    protected float velocidade;
    protected boolean ativo = true;
    protected int recompensa;
    protected int largura; // Tamanho (para desenho e mira)
    protected int altura;  // Tamanho (para desenho e mira)

    protected List<Point> caminho;
    protected int pontoAlvoIndex = 0;

    public Inimigo(float x, float y, List<Point> caminho, int vidaMaxima, float velocidade, int recompensa, int largura, int altura) {
        this.x = x;
        this.y = y;
        this.caminho = caminho;
        this.vidaMaxima = vidaMaxima;
        this.vida = this.vidaMaxima;
        this.velocidade = velocidade;
        this.recompensa = recompensa;
        this.largura = largura;
        this.altura = altura;
    }

    public abstract void drawInimigo(Graphics2D g2);

    public void update() {
        if (!ativo || pontoAlvoIndex >= caminho.size()) {
            desativar();
            return;
        }

        Point alvo = caminho.get(pontoAlvoIndex);
        float alvoX = alvo.x;
        float alvoY = alvo.y;

        float angulo = (float) Math.atan2(alvoY - y, alvoX - x);
        x += velocidade * Math.cos(angulo);
        y += velocidade * Math.sin(angulo);

        double distancia = Math.sqrt(Math.pow(alvoX - x, 2) + Math.pow(alvoY - y, 2));

        if (distancia < velocidade) {
            pontoAlvoIndex++;
        }
    }


    public void draw(Graphics2D g2) {
        drawInimigo(g2);


        if (vida < vidaMaxima) {
            g2.setColor(Color.GRAY);
            g2.fillRect((int) x, (int) y - 8, largura, 5); // Usa a largura do inimigo

            g2.setColor(Color.GREEN);
            float larguraVida = (float) vida / vidaMaxima * largura; // Usa a largura do inimigo
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

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }
}