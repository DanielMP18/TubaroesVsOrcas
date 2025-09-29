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
    
    // 0 = grama, 1 = caminho
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
        // Exemplo de um caminho simples em forma de "S"
        // Preenche tudo com grama (0)
        for (int col = 0; col < maxColunas; col++) {
            for (int row = 0; row < maxLinhas; row++) {
                mapGrid[col][row] = 0;
            }
        }
        
        // Define o caminho (1)
        for(int i = 0; i < 8; i++) mapGrid[i][3] = 1;
        for(int i = 3; i < 10; i++) mapGrid[7][i] = 1;
        for(int i = 7; i < 20; i++) mapGrid[i][9] = 1;
        
        // Define os pontos (waypoints) que os inimigos devem seguir
        // Multiplicamos pelo tamanho do título para obter a coordenada em pixels
        caminho.add(new Point(0 * tamanhoDoTitulo, 3 * tamanhoDoTitulo));
        caminho.add(new Point(7 * tamanhoDoTitulo, 3 * tamanhoDoTitulo));
        caminho.add(new Point(7 * tamanhoDoTitulo, 9 * tamanhoDoTitulo));
        caminho.add(new Point(19 * tamanhoDoTitulo, 9 * tamanhoDoTitulo));
        caminho.add(new Point(24 * tamanhoDoTitulo, 9 * tamanhoDoTitulo)); // Ponto final fora da tela
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

                // Descomente a linha abaixo para ver a grade
                // g2.setColor(Color.BLACK);
                // g2.drawRect(tileX, tileY, tamanhoDoTitulo, tamanhoDoTitulo);
            }
        }
    }
    
    public List<Point> getCaminho() {
        return this.caminho;
    }
}
