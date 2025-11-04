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

    
    private final int[][] mapGrid;
    private final List<Point> caminho;

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

        desenharSegmento(0, 4, 6, 4);    // Termina em: (6, 4)
        desenharSegmento(6, 4, 6, 1);    // Começa em: (6, 4). Termina em: (6, 1)
        desenharSegmento(6, 1, 10, 1);   // Começa em: (6, 1). Termina em: (10, 1)
        desenharSegmento(10, 1, 10, 6);  // Começa em: (10, 1). Termina em: (10, 7)
        desenharSegmento(10, 6, 15, 6);  // Começa em: (10, 7). Termina em: (15, 7)
        desenharSegmento(15, 6, 15, 4);  // Começa em: (15, 7). Termina em: (15, 4)
        desenharSegmento(15, 4, 18, 4);  // Começa em: (15, 4). Termina em: (18, 4)
        desenharSegmento(18, 4, 18, 9);  // Começa em: (18, 4). Termina em: (18, 9)
        desenharSegmento(18, 9, 8, 9);   // Começa em: (18, 9). Termina em: (8, 9)
        desenharSegmento(8, 9, 8, 12);   // Começa em: (8, 9). Termina em: (8, 12)
        desenharSegmento(8, 12, 21, 12); // Começa em: (8, 12). Termina em: (21, 12)
        desenharSegmento(21, 12, 21, 9); // Começa em: (21, 12). Termina em: (21, 9)
        desenharSegmento(21, 9, 27, 9);  // Começa em: (21, 9). Termina em: (27, 9)


        // ponto final(destino)
        caminho.add(new Point(27 * tamanhoDoTitulo, 9 * tamanhoDoTitulo));
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
                    g2.setColor(new Color(88, 153, 255)); // Cor do caminho
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
