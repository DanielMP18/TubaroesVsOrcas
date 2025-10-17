package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TileManager {

    private final int tamanhoDoTitulo;
    private final int maxColunas;
    private final int maxLinhas;

    // 0 = água (construível), 1 = caminho (não construível)
    private int[][] mapGrid;
    private List<Point> caminho;

    public TileManager(int tamanhoDoTitulo, int maxColunas, int maxLinhas) {
        this.tamanhoDoTitulo = tamanhoDoTitulo;
        this.maxColunas = maxColunas;
        this.maxLinhas = maxLinhas;

        this.mapGrid = new int[maxColunas][maxLinhas];
        this.caminho = new ArrayList<>();

        criarMapaFixo();
    }

    private void criarMapaFixo() {
        for (int col = 0; col < maxColunas; col++) {
            for (int row = 0; row < maxLinhas; row++) {
                mapGrid[col][row] = 0;
            }
        }

        for (int i = 0; i < 8; i++) mapGrid[i][3] = 1;
        for (int i = 3; i < 10; i++) mapGrid[7][i] = 1;
        for (int i = 7; i < 20; i++) mapGrid[i][9] = 1;

        caminho.add(new Point(0 * tamanhoDoTitulo, 3 * tamanhoDoTitulo));
        caminho.add(new Point(7 * tamanhoDoTitulo, 3 * tamanhoDoTitulo));
        caminho.add(new Point(7 * tamanhoDoTitulo, 9 * tamanhoDoTitulo));
        caminho.add(new Point(19 * tamanhoDoTitulo, 9 * tamanhoDoTitulo));
        caminho.add(new Point(24 * tamanhoDoTitulo, 9 * tamanhoDoTitulo));
    }

    public void draw(Graphics2D g2) {
        for (int col = 0; col < maxColunas; col++) {
            for (int row = 0; row < maxLinhas; row++) {
                int tileX = col * tamanhoDoTitulo;
                int tileY = row * tamanhoDoTitulo;

                if (mapGrid[col][row] == 1) {
                    g2.setColor(new Color(210, 180, 140)); // Cor de areia para o caminho
                } else {
                    g2.setColor(new Color(0, 100, 200)); // Cor de água para o fundo
                }
                g2.fillRect(tileX, tileY, tamanhoDoTitulo, tamanhoDoTitulo);
            }
        }
    }

    public boolean isTileValidoParaConstrucao(int col, int row) {
        if (col >= 0 && col < maxColunas && row >= 0 && row < maxLinhas) {
            return mapGrid[col][row] == 0; // Pode construir na água (0)
        }
        return false;
    }

    public List<Point> getCaminho() {
        return this.caminho;
    }
}