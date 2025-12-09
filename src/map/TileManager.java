package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import sprites.AnimatedTile;
import sprites.SpriteSheet;

public class TileManager {

    private final int tamanhoDoTitulo;
    private final int maxColunas;
    private final int maxLinhas;

    // A matriz que diz o que é cada quadrado (0 = Água/Construção, 1 = Caminho)
    private final int[][] mapGrid;
    private final LinkedList<Point> caminho;

    // Imagens e Animações
    private AnimatedTile waterTile; // OTIMIZAÇÃO: Apenas UMA instância para toda a água
    private BufferedImage[] caminhoTiles = null;

    public TileManager(int tamanhoDoTitulo, int maxColunas, int maxLinhas) {
        this.tamanhoDoTitulo = tamanhoDoTitulo;
        this.maxColunas = maxColunas;
        this.maxLinhas = maxLinhas;

        this.mapGrid = new int[maxColunas][maxLinhas];
        this.caminho = new LinkedList<>();

        System.out.println("--- INICIANDO TILEMANAGER ---");

        // 1. Cria a lógica (Define o desenho do mapa na memória)
        criarMapaFixo();
        System.out.println("Mapa Lógico Criado com Sucesso.");

        // 2. Carrega as imagens
        loadMapSprites();
    }

    private void loadMapSprites() {
        try {
            // --- CARREGAR FUNDO (Água - Animado) ---
            File arquivoFundo = new File("res/sprites/fundoDoMar.png");

            if (arquivoFundo.exists()) {
                BufferedImage fundo = ImageIO.read(arquivoFundo);
                if (fundo != null) {
                    // SpriteSheet 2x2 (Imaginando imagem 64x64px com tiles de 32x32px)
                    SpriteSheet sfundo = new SpriteSheet(fundo, 32, 32);
                    BufferedImage[] fundoFrames = sfundo.getSprites(); // Pega os 4 frames

                    // Inicializa a instância única do tile de água
                    waterTile = new AnimatedTile(fundoFrames, tamanhoDoTitulo);

                    System.out.println("✅ Animação de Fundo (Water) carregada.");
                }
            } else {
                System.err.println("❌ ERRO: Não achou a imagem: " + arquivoFundo.getAbsolutePath());
            }

            // --- CARREGAR CAMINHO (Areia - Estático) ---
            File arquivoCaminho = new File("res/sprites/caminho.png");

            if (arquivoCaminho.exists()) {
                BufferedImage caminhoImg = ImageIO.read(arquivoCaminho);
                if (caminhoImg != null) {
                    SpriteSheet scaminho = new SpriteSheet(caminhoImg, 32, 32);
                    caminhoTiles = scaminho.getSprites();
                    System.out.println("✅ Imagem carregada: caminho.png");
                }
            } else {
                System.err.println("❌ ERRO: Não achou a imagem: " + arquivoCaminho.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro fatal ao carregar imagens do mapa.");
        }
    }

    private void criarMapaFixo() {
        // Pinta tudo de 0 (Água)
        for (int col = 0; col < maxColunas; col++) {
            for (int row = 0; row < maxLinhas; row++) {
                mapGrid[col][row] = 0;
            }
        }

        // Desenha os segmentos do caminho (Marca como 1)
        desenharSegmento(0, 4, 6, 4);
        desenharSegmento(6, 4, 6, 1);
        desenharSegmento(6, 1, 10, 1);
        desenharSegmento(10, 1, 10, 6);
        desenharSegmento(10, 6, 15, 6);
        desenharSegmento(15, 6, 15, 4);
        desenharSegmento(15, 4, 18, 4);
        desenharSegmento(18, 4, 18, 9);
        desenharSegmento(18, 9, 8, 9);
        desenharSegmento(8, 9, 8, 12);
        desenharSegmento(8, 12, 21, 12);
        desenharSegmento(21, 12, 21, 9);
        desenharSegmento(21, 9, 27, 9);

        caminho.add(new Point(27 * tamanhoDoTitulo, 9 * tamanhoDoTitulo));
    }

    private void desenharSegmento(int c1, int r1, int c2, int r2) {
        if (validarCoords(c1, r1)) caminho.add(new Point(c1 * tamanhoDoTitulo, r1 * tamanhoDoTitulo));

        if (c1 == c2) {
            for (int r = Math.min(r1, r2); r <= Math.max(r1, r2); r++) {
                if (validarCoords(c1, r)) mapGrid[c1][r] = 1;
            }
        } else if (r1 == r2) {
            for (int c = Math.min(c1, c2); c <= Math.max(c1, c2); c++) {
                if (validarCoords(c, r1)) mapGrid[c][r1] = 1;
            }
        }
    }

    private boolean validarCoords(int c, int r) {
        return c >= 0 && c < maxColunas && r >= 0 && r < maxLinhas;
    }

    // Atualiza a animação da água (apenas uma vez por frame)
    public void update(long deltaMs) {
        if (waterTile != null) {
            waterTile.update(deltaMs);
        }
    }

    public void draw(Graphics2D g2) {
        for (int col = 0; col < maxColunas; col++) {
            for (int row = 0; row < maxLinhas; row++) {
                int tileX = col * tamanhoDoTitulo;
                int tileY = row * tamanhoDoTitulo;

                // Se for 1, é CAMINHO
                if (mapGrid[col][row] == 1) {
                    if (caminhoTiles != null && caminhoTiles.length > 0) {
                        BufferedImage frame = caminhoTiles[(col + row) % caminhoTiles.length];
                        g2.drawImage(frame, tileX, tileY, tamanhoDoTitulo, tamanhoDoTitulo, null);
                    } else {
                        // FALLBACK: BEGE
                        g2.setColor(new Color(194, 178, 128));
                        g2.fillRect(tileX, tileY, tamanhoDoTitulo, tamanhoDoTitulo);
                        g2.setColor(Color.BLACK);
                        g2.drawRect(tileX, tileY, tamanhoDoTitulo, tamanhoDoTitulo);
                    }
                }
                // Se for 0, é ÁGUA (Fundo)
                else {
                    if (waterTile != null) {
                        // Desenha o frame atual da animação na posição X, Y
                        waterTile.draw(g2, tileX, tileY);
                    } else {
                        // FALLBACK: AZUL
                        g2.setColor(new Color(0, 100, 200));
                        g2.fillRect(tileX, tileY, tamanhoDoTitulo, tamanhoDoTitulo);
                        g2.setColor(Color.WHITE);
                        g2.drawRect(tileX, tileY, tamanhoDoTitulo, tamanhoDoTitulo);
                    }
                }
            }
        }
    }

    public boolean isTileValidoParaConstrucao(int col, int row) {
        if (validarCoords(col, row)) {
            return mapGrid[col][row] == 0;
        }
        return false;
    }

    public LinkedList<Point> getCaminho() {
        return this.caminho;
    }
}
