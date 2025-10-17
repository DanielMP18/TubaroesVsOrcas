package game;

import entity.Inimigo;
import entity.Projetil;
import entity.Torre;
import entity.TorreCanhao;
import entity.TorreLaser;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import map.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // CONFIGURAÇÕES DE TELA
    final int tamanhoOriginalTile = 16;
    final int escala = 3;
    public final int tamanhoDoTile = tamanhoOriginalTile * escala;
    public final int maxColunasTela = 24;
    public final int maxLinhasTela = 16;
    public final int larguraTela = tamanhoDoTile * maxColunasTela;
    public final int alturaTela = tamanhoDoTile * maxLinhasTela;

    // GAME LOOP
    private Thread gameThread;
    private final int FPS = 60;

    // GERENCIADORES E ENTIDADES
    private TileManager tileManager;
    private List<Inimigo> inimigos;
    private List<Torre> torres;
    private List<Projetil> projeteis;

    // ESTADO DO JOGO
    private int vidaBase = 20;
    private int ondaAtual = 1;
    private int dinheiro = 250; // Dinheiro inicial

    public static final int ESTADO_JOGANDO = 1;
    public static final int ESTADO_FIM_DE_JOGO = 2;
    private int estadoDoJogo;

    // SPAWN DE INIMIGOS
    private long ultimoSpawnTime = System.nanoTime();
    private long spawnCooldown = 1_500_000_000L;

    // CONTROLES DO JOGADOR
    private Point mousePos = new Point();
    private int tipoTorreSelecionada = 1; // 1 para Canhão, 2 para Laser

    // CONSTANTES E OBJETOS PARA A HUD DE SELEÇÃO
    private final int alturaHUD = 80;
    private final Rectangle boxTorreCanhao = new Rectangle(20, alturaTela - alturaHUD + 10, 140, 60);
    private final Rectangle boxTorreLaser = new Rectangle(170, alturaTela - alturaHUD + 10, 140, 60);


    public GamePanel() {
    //config tela
        this.setPreferredSize(new Dimension(larguraTela, alturaTela));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        this.tileManager = new TileManager(tamanhoDoTile, maxColunasTela, maxLinhasTela);
        this.inimigos = new ArrayList<>();
        this.torres = new ArrayList<>();
        this.projeteis = new ArrayList<>();

        this.estadoDoJogo = ESTADO_JOGANDO;

        adicionarControles();
    }

    //config mouse
    private void adicionarControles() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (estadoDoJogo != ESTADO_JOGANDO) return;

                if (e.getY() < alturaTela - alturaHUD) { // Clicou na área do mapa
                    int col = e.getX() / tamanhoDoTile;
                    int row = e.getY() / tamanhoDoTile;

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        construirTorre(col, row);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        venderTorre(col, row);
                    }
                } else { // Clicou na área da HUD
                    if (boxTorreCanhao.contains(e.getPoint())) {
                        tipoTorreSelecionada = 1;
                    } else if (boxTorreLaser.contains(e.getPoint())) {
                        tipoTorreSelecionada = 2;
                    }
                }
            }
        });
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (estadoDoJogo == ESTADO_JOGANDO) {
            spawnInimigo();

            Iterator<Inimigo> inimigoIt = inimigos.iterator();
            while (inimigoIt.hasNext()) {
                Inimigo inimigo = inimigoIt.next();
                inimigo.update();
                if (inimigo.chegouNaBase()) {
                    vidaBase--;
                    inimigoIt.remove();
                    if (vidaBase <= 0) {
                        estadoDoJogo = ESTADO_FIM_DE_JOGO;
                    }
                } else if (!inimigo.isAtivo()) {
                    dinheiro += inimigo.getRecompensa();
                    inimigoIt.remove();
                }
            }

            for (Torre torre : torres) {
                torre.update();
            }

            Iterator<Projetil> projetilIt = projeteis.iterator();
            while (projetilIt.hasNext()) {
                Projetil p = projetilIt.next();
                if (p.isAtivo()) {
                    p.update();
                } else {
                    projetilIt.remove();
                }
            }
        }
    }

    private void spawnInimigo() {
        long agora = System.nanoTime();
        if (agora - ultimoSpawnTime > spawnCooldown) {
            Point pontoInicial = tileManager.getCaminho().get(0);
            inimigos.add(new Inimigo(pontoInicial.x, pontoInicial.y, tileManager.getCaminho()));
            ultimoSpawnTime = agora;
        }
    }

    //constroi e verifica se é possivel construir
    private void construirTorre(int col, int row) {
        if (tileManager.isTileValidoParaConstrucao(col, row) && !existeTorreNoLocal(col, row)) {
            Torre novaTorre = null;
            if (tipoTorreSelecionada == 1) {
                novaTorre = new TorreCanhao(col, row, tamanhoDoTile, inimigos, projeteis);
            } else if (tipoTorreSelecionada == 2) {
                novaTorre = new TorreLaser(col, row, tamanhoDoTile, inimigos, projeteis);
            }

            if (novaTorre != null && dinheiro >= novaTorre.getCusto()) {
                torres.add(novaTorre);
                dinheiro -= novaTorre.getCusto();
            }
        }
    }

    //vende torre botao direito do mouse
    private void venderTorre(int col, int row) {
        Iterator<Torre> torreIt = torres.iterator();
        while (torreIt.hasNext()) {
            Torre t = torreIt.next();
            if (t.getCol() == col && t.getRow() == row) {
                dinheiro += t.getCusto() / 2;
                torreIt.remove();
                break;
            }
        }
    }

    private boolean existeTorreNoLocal(int col, int row) {
        for (Torre torre : torres) {
            if (torre.getCol() == col && torre.getRow() == row) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);

        for (Torre torre : torres) {
            torre.draw(g2);
        }

        for (Projetil projetil : projeteis) {
            projetil.draw(g2);
        }

        for (Inimigo inimigo : inimigos) {
            inimigo.draw(g2);
        }

        drawHUD(g2);
        drawPlacementGhost(g2);
        drawTorreSelectionHUD(g2);

        if (estadoDoJogo == ESTADO_FIM_DE_JOGO) {
            drawTelaFimDeJogo(g2);
        }

        g2.dispose();
    }

    private void drawHUD(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);

        String textoVida = "Vida: " + vidaBase;
        g2.drawString(textoVida, 20, 30);

        String textoDinheiro = "Dinheiro: $" + dinheiro;
        g2.drawString(textoDinheiro, 20, 60);

        String textoOnda = "Onda: " + ondaAtual;
        g2.drawString(textoOnda, larguraTela - 150, 30);
    }


    private void drawTorreSelectionHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, alturaTela - alturaHUD, larguraTela, alturaHUD);

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        if (tipoTorreSelecionada == 1) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.draw(boxTorreCanhao);
        g2.drawString("Canhão", boxTorreCanhao.x + 10, boxTorreCanhao.y + 20);
        g2.drawString("Custo: $" + TorreCanhao.CUSTO, boxTorreCanhao.x + 10, boxTorreCanhao.y + 45);

        if (tipoTorreSelecionada == 2) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.draw(boxTorreLaser);
        g2.drawString("Laser", boxTorreLaser.x + 10, boxTorreLaser.y + 20);
        g2.drawString("Custo: $" + TorreLaser.CUSTO, boxTorreLaser.x + 10, boxTorreLaser.y + 45);
    }

    private void drawPlacementGhost(Graphics2D g2) {
        if (estadoDoJogo != ESTADO_JOGANDO) return;

        int col = mousePos.x / tamanhoDoTile;
        int row = mousePos.y / tamanhoDoTile;

        if (mousePos.y < alturaTela - alturaHUD) {
            if (tileManager.isTileValidoParaConstrucao(col, row) && !existeTorreNoLocal(col, row)) {
                g2.setColor(new Color(0, 255, 0, 100));
            } else {
                g2.setColor(new Color(255, 0, 0, 100));
            }
            g2.fillRect(col * tamanhoDoTile, row * tamanhoDoTile, tamanhoDoTile, tamanhoDoTile);
        }
    }

    private void drawTelaFimDeJogo(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, larguraTela, alturaTela);

        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.RED);

        String texto = "FIM DE JOGO";
        int textoLargura = g2.getFontMetrics().stringWidth(texto);
        int x = (larguraTela - textoLargura) / 2;
        int y = alturaTela / 2;

        g2.drawString(texto, x, y);
    }
}