package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public class InimigoRapido extends Inimigo {

    public InimigoRapido(float x, float y, List<Point> caminho) {
        super(x, y, caminho, 50, 3.0f, 5, 24, 24);
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.fillOval((int) x, (int) y, largura, altura);
    }
}