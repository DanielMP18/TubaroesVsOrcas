package entity;

import game.Elemento; 
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
    
    // --- Variáveis de Upgrade ---
    protected Elemento elemento;
    protected int nivel;
    protected int custoUpgrade;
    protected int danoBase; 

    public Torre(int col, int row, int tamanho, List<Inimigo> inimigos, List<Projetil> projeteis) {
        this.col = col;
        this.row = row;
        this.tamanho = tamanho;
        this.x = col * tamanho + tamanho / 2; // Centro da torre
        this.y = row * tamanho + tamanho / 2; // Centro da torre
        this.inimigos = inimigos;
        this.projeteis = projeteis;
        this.ultimoDisparo = 0;
        this.alvo = null;
        
        
        this.elemento = Elemento.NEUTRO; 
        this.nivel = 1;
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
        if (alvo == null || !alvo.isAtivo() || Point.distance(this.x, this.y, alvo.getX(), alvo.getY()) > this.alcance) {
             encontrarAlvo();
        }
       
        long agora = System.nanoTime();
        if (alvo != null && (agora - ultimoDisparo) > cadenciaDeTiro) {
            atirar();
            ultimoDisparo = agora;
        }
    }

    // --- LÓGICA DE UPGRADE (EFEITOS DE STATUS) ---
    public void especializar(Elemento novoElemento) {
        if (this.nivel != 1 || this.elemento != Elemento.NEUTRO) {
            System.out.println("Esta torre já foi especializada!");
            return;
        }

        this.elemento = novoElemento; !
        this.nivel = 2; 

        // Bônus geral por evoluir
        this.danoBase = (int) (this.danoBase * 1.2); // +20% dano
        this.alcance = (int) (this.alcance * 1.1); // +10% alcance
        
        this.custoUpgrade = this.custo * 2; // Custo do Nível 3
        System.out.println("Torre especializada em " + novoElemento);
    }
    
    public abstract void draw(Graphics2D g2);

    protected abstract void atirar();

    // --- Getters ---
    public int getCusto() { return custo; }
    public int getCol() { return col; }
    public int getRow() { return row; }
    public Elemento getElemento() { return elemento; }
    public int getNivel() { return nivel; }
    public int getCustoUpgrade() { return custoUpgrade; }
    public boolean isEspecializada() { return this.elemento != Elemento.NEUTRO; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAlcance() { return alcance; }
}
