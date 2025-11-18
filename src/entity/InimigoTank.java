package entity;

import game.Elemento;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public class InimigoTank extends Inimigo {

    public InimigoTank(float x, float y, List<Point> caminho) {
        super(x, y, caminho, 500, 0.8f, 50, 40, 40);
        this.elemento = Elemento.AGUA; // <--- Elemento
    }

    @Override
    public void drawInimigo(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect((int) x, (int) y, largura, altura);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect((int) x + 2, (int) y + 2, largura - 5, altura - 5);
    }
}
