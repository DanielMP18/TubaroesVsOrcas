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
        // 1. Preenche tudo com "água" (0) onde se pode construir
        for (int col = 0; col < maxColunas; col++) {
            for (int row = 0; row < maxLinhas; row++) {
                mapGrid[col][row] = 0;
            }
        }

        // 2. Define os segmentos do caminho
        // Seus valores de maxColunas=25 e maxLinhas=15 são perfeitos para este mapa.
        
        desenharSegmento(0, 5, 3, 5);   // Começa em (0,5) e vai até (3,5)
        desenharSegmento(3, 5, 3, 3);   // Curva 1: de (3,5) vai até (3,3)
        desenharSegmento(3, 3, 7, 3);   // Curva 2: de (3,3) vai até (7,3)
        desenharSegmento(7, 3, 7, 7);   // Curva 3: de (7,3) vai até (7,7)
        desenharSegmento(7, 7, 12, 7);  // Curva 4: de (7,7) vai até (12,7)
        desenharSegmento(12, 7, 12, 5); // Curva 5: de (12,7) vai até (12,5)
        desenharSegmento(12, 5, 15, 5); // Curva 6: de (12,5) vai até (15,5)
        desenharSegmento(15, 5, 15, 10); // Curva 7: de (15,5) vai até (15,10)
        desenharSegmento(15, 10, 5, 10); // Curva 8: de (15,10) vai até (5,10)
        desenharSegmento(5, 10, 5, 13); // Curva 9: de (5,10) vai até (5,13)
        desenharSegmento(5, 13, 18, 13); // Curva 10: de (5,13) vai até (18,13)
        desenharSegmento(18, 13, 18, 10); // Curva 11: de (18,13) vai até (18,10)
        desenharSegmento(18, 10, 24, 10); // Curva 12: de (18,10) vai até (24,10) (fim)

        // 3. Adiciona o ponto final (o destino)
        // (Usei 24, 10 como ponto final, ajuste se necessário)
        caminho.add(new Point(24 * tamanhoDoTitulo, 10 * tamanhoDoTitulo));
    }

    /**
     * NOVO MÉTODO AUXILIAR
     * Desenha um segmento de caminho reto (horizontal ou vertical) no mapGrid
     * e adiciona o ponto inicial (c1, r1) à lista de waypoints.
     */
    private void desenharSegmento(int c1, int r1, int c2, int r2) {
        // Adiciona o ponto inicial do segmento (a curva)
        // Verifica os limites antes de adicionar
        if (c1 >= 0 && c1 < maxColunas && r1 >= 0 && r1 < maxLinhas) {
             caminho.add(new Point(c1 * tamanhoDoTitulo, r1 * tamanhoDoTitulo));
        }
       
        // Determina a direção e desenha no grid
        if (c1 == c2) { // Movimento Vertical
            for (int r = Math.min(r1, r2); r <= Math.max(r1, r2); r++) {
                if (c1 >= 0 && c1 < maxColunas && r >= 0 && r < maxLinhas) {
                    mapGrid[c1][r] = 1; // 1 = caminho
                }
            }
        } else if (r1 == r2) { // Movimento Horizontal
            for (int c = Math.min(c1, c2); c <= Math.max(c1, c2); c++) {
                if (c >= 0 && c < maxColunas && r1 >= 0 && r1 < maxLinhas) {
                    mapGrid[c][r1] = 1; // 1 = caminho
                }
            }
        }
    }


    // --- SEUS MÉTODOS ORIGINAIS (sem mudança) ---

    public void draw(Graphics2D g2) {
        for (int col = 0; col < maxColunas; col++) {
            for (int row = 0; row < maxLinhas; row++) {
                int tileX = col * tamanhoDoTitulo;
                int tileY = row * tamanhoDoTitulo;

                if (mapGrid[col][row] == 1) {
                    g2.setColor(new Color(100, 100, 100)); // Cor do caminho
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
