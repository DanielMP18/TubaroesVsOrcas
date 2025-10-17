package entity;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public abstract class Torre {

    protected int x, y;
    protected int col, row;
    protected int tamanho;
    protected int custo;
    protected int alcance;
    protected long ultimoDisparo;
    protected long cadenciaDeTiro;
    protected Inimigo alvo;

    protected List<Inimigo> inimigos;
    protected List<Projetil> projeteis;

    public Torre(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        this.col = col;
        this.row = row;
        this.tamanho = tamanho;
        this.x = col * tamanho + tamanho / 2;
        this.y = row * tamanho + tamanho / 2;
        this.inimigos = inimigos;
        this.projeteis = projeteis;
        this.ultimoDisparo = 0;
        this.alvo = null;
    }


    protected void encontrarAlvo() {
        Inimigo inimigoMaisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Inimigo inimigo : inimigos) {
            if (inimigo.isAtivo()) {
                double distancia = Point.distance(this.x, this.y, inimigo.getX(), inimigo.getY());
                if (distancia < menorDistancia && distancia <= this.alcance) {
                    menorDistancia = distancia;
                    inimigoMaisProximo = inimigo;
                }
            }
        }
        this.alvo = inimigoMaisProximo;
    }


    public void update() {
        encontrarAlvo();
        long agora = System.nanoTime();
        if (alvo != null && (agora - ultimoDisparo) > cadenciaDeTiro) {
            atirar();
            ultimoDisparo = agora;
        }
    }

    public abstract void draw(Graphics2D g2);

    protected abstract void atirar();

    public int getCusto() {
        return custo;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}