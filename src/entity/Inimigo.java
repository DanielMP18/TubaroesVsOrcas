package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public class Inimigo {

    private float x, y;
    private int vida;
    private float velocidade;
    private boolean ativo = true; 

    
    private List<Point> caminho;
    private int pontoAlvoIndex = 0;

    public Inimigo(float x, float y, List<Point> caminho) {
        this.x = x;
        this.y = y;
        this.vida = 100; 
        this.velocidade = 1.5f; 
        this.caminho = caminho;
    }

   
    public void update() {
        if (!ativo || pontoAlvoIndex >= caminho.size()) {
            return; 
        }

        Point alvo = caminho.get(pontoAlvoIndex);

        float alvoX = alvo.x;
        float alvoY = alvo.y;


        float angulo = (float) Math.atan2(alvoY - y, alvoX - x);

        
        x += velocidade * Math.cos(angulo);
        y += velocidade * Math.sin(angulo);

        
        if (Math.sqrt(Math.pow(alvoX - x, 2) + Math.pow(alvoY - y, 2)) < velocidade) {
            pontoAlvoIndex++; 
        }
    }

    
    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED); 
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
