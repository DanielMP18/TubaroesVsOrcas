package entity;

import game.Elemento;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public class InimigoBasico extends Inimigo {

    public InimigoBasico(float x, float y, List<Point> caminho) {
        super(x, y, caminho, 100, 1.5f, 15, 32, 32);
        this.elemento = Elemento.FOGO; // <--- Elemento
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillRect((int) x, (int) y, largura, altura);
    }
}
