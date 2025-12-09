package game;

import entity.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;    // <--- IMPORT NOVO
import java.awt.event.MouseAdapter; // <--- IMPORT NOVO
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File; // <--- IMPORT NOVO
import java.io.IOException;               // <--- IMPORT NOVO
import java.util.ArrayList;        // <--- IMPORT NOVO
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;      // <--- IMPORT NOVO
import map.TileManager;

// Adicionamos "KeyListener" aqui para detectar o ENTER
public class GamePanel extends JPanel implements Runnable, KeyListener {

    // --- Configurações de Tela ---
    final int tamanhoOriginalTile = 16;
    final int escala = 3;
    public final int tamanhoDoTile = tamanhoOriginalTile * escala; // 48
    public final int maxColunasTela = 25;
    public final int maxLinhasTela = 15;
    public final int larguraTela = tamanhoDoTile * maxColunasTela; // 1200
    public final int alturaTela = tamanhoDoTile * maxLinhasTela; // 720

    private Thread gameThread;

    // --- Gerenciadores e Entidades ---
    private TileManager tileManager;
    private WaveManager waveManager;
    private List<Inimigo> inimigos;
    private List<Torre> torres;
    private List<Projetil> projeteis;

    // --- Estado do Jogo ---
    private int vidaBase = 5;
    private int dinheiro = 500;

    public static final int ESTADO_PREPARACAO = 0;
    public static final int ESTADO_JOGANDO = 1;
    public static final int ESTADO_FIM_DE_JOGO = 2;
    public static final int ESTADO_VITORIA = 3;
    public static final int ESTADO_MENU = 4; // <--- NOVO ESTADO
    
    private int estadoDoJogo;
    private BufferedImage menuImagem; // <--- Imagem do Menu

    // --- Spawn de Inimigos e Ondas ---
    private long tempoEntreOndas = 5_000_000_000L;
    private long proximaOndaTimer = 0;

    // --- Controles e HUD ---
    private Point mousePos = new Point();
    private int tipoTorreSelecionada = 1;
    private final int alturaHUD = 80;

    private final Rectangle boxTorreCanhao = new Rectangle(20, alturaTela - alturaHUD + 10, 140, 60);
    private final Rectangle boxTorreLaser = new Rectangle(170, alturaTela - alturaHUD + 10, 140, 60);
    private final Rectangle boxTorreEspada = new Rectangle(320, alturaTela - alturaHUD + 10, 140, 60);

    // --- Sistema de Upgrade ---
    private Torre torreSelecionada = null;
    private final Rectangle boxUpgradeOpcao1 = new Rectangle(20, alturaTela - alturaHUD + 10, 140, 60);
    private final Rectangle boxUpgradeOpcao2 = new Rectangle(170, alturaTela - alturaHUD + 10, 140, 60);
    private final Rectangle boxUpgradeOpcao3 = new Rectangle(320, alturaTela - alturaHUD + 10, 140, 60);


    public GamePanel() {
        this.setPreferredSize(new Dimension(larguraTela, alturaTela));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true); // Importante para o teclado funcionar
        this.addKeyListener(this); // Adiciona o ouvinte de teclado

        this.tileManager = new TileManager(tamanhoDoTile, maxColunasTela, maxLinhasTela);
        this.waveManager = new WaveManager();

        this.inimigos = new ArrayList<>();
        this.torres = new ArrayList<>();
        this.projeteis = new ArrayList<>();

        // COMEÇA NO MENU
        this.estadoDoJogo = ESTADO_MENU;
        this.proximaOndaTimer = System.nanoTime() + tempoEntreOndas;

        // Carrega a imagem do menu
        carregarImagemMenu();

        adicionarControles();
    }

    private void carregarImagemMenu() {
        try {
            // Garanta que a imagem 'menu.png' esteja na pasta 'res/sprites/'
            File arq = new File("res/sprites/menu.png");
            if (arq.exists()) {
                menuImagem = ImageIO.read(arq);
            } else {
                System.out.println("AVISO: Imagem menu.png não encontrada em res/sprites/");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                // Se estiver no MENU, o clique do mouse não faz nada (não constrói torre)
                if (estadoDoJogo == ESTADO_MENU) return;

                if (estadoDoJogo == ESTADO_FIM_DE_JOGO || estadoDoJogo == ESTADO_VITORIA) return;

                // --- CLIQUE NO MAPA ---
                if (e.getY() < alturaTela - alturaHUD) {
                    int col = e.getX() / tamanhoDoTile;
                    int row = e.getY() / tamanhoDoTile;

                    Torre torreClicada = getTorreNoLocal(col, row);

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (torreClicada != null) {
                            torreSelecionada = (torreSelecionada == torreClicada) ? null : torreClicada;
                        } else {
                            construirTorre(col, row);
                            torreSelecionada = null;
                        }
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        if (torreClicada != null) {
                            venderTorre(col, row);
                            torreSelecionada = null;
                        }
                    }
                }
                // --- CLIQUE NA HUD ---
                else {
                    if (torreSelecionada == null) {
                        if (boxTorreCanhao.contains(e.getPoint())) tipoTorreSelecionada = 1;
                        else if (boxTorreLaser.contains(e.getPoint())) tipoTorreSelecionada = 2;
                        else if (boxTorreEspada.contains(e.getPoint())) tipoTorreSelecionada = 3;
                    } else {
                        if (!torreSelecionada.isEspecializada()) {
                            int custoUpg = torreSelecionada.getCustoUpgrade();
                            if (boxUpgradeOpcao1.contains(e.getPoint()) && dinheiro >= custoUpg) {
                                dinheiro -= custoUpg;
                                torreSelecionada.especializar(Elemento.FOGO);
                                torreSelecionada = null;
                            }
                            else if (boxUpgradeOpcao2.contains(e.getPoint()) && dinheiro >= custoUpg) {
                                dinheiro -= custoUpg;
                                torreSelecionada.especializar(Elemento.AGUA);
                                torreSelecionada = null;
                            }
                            else if (boxUpgradeOpcao3.contains(e.getPoint()) && dinheiro >= custoUpg) {
                                dinheiro -= custoUpg;
                                torreSelecionada.especializar(Elemento.ALGA);
                                torreSelecionada = null;
                            }
                        }
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
        // --- LOOP PRINCIPAL DO JOGO ---
        final double UPS_LIMIT = 60.0;
        final double NS_PER_UPDATE = 1_000_000_000.0 / UPS_LIMIT;
        final long FIXED_UPDATE_MS = (long) (1000 / UPS_LIMIT);

        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / NS_PER_UPDATE;
            lastTime = currentTime;

            if (delta >= 1) {
                update(FIXED_UPDATE_MS);
                repaint();
                delta--;
            }
        }
    }

    public void update(long deltaMs) {
        // Se estiver no MENU, para tudo e espera
        if (estadoDoJogo == ESTADO_MENU) return;

        if (estadoDoJogo == ESTADO_FIM_DE_JOGO || estadoDoJogo == ESTADO_VITORIA) {
            return;
        }

        // Atualiza a animação do fundo
        tileManager.update(deltaMs);

        // --- FASE DE PREPARAÇÃO ---
        if (estadoDoJogo == ESTADO_PREPARACAO) {
            for (Torre torre : torres) {
                torre.update();
            }
            if (System.nanoTime() > proximaOndaTimer) {
                estadoDoJogo = ESTADO_JOGANDO;
            }
            return;
        }

        // --- FASE JOGANDO ---
        int tipoParaSpawnar = waveManager.getProximoInimigoParaSpawnar();
        if (tipoParaSpawnar != -1) {
            spawnInimigo(tipoParaSpawnar);
        }

        // Atualiza Inimigos
        Iterator<Inimigo> inimigoIt = inimigos.iterator();
        while (inimigoIt.hasNext()) {
            Inimigo inimigo = inimigoIt.next();
            inimigo.update();

            if (inimigo.chegouNaBase()) {
                vidaBase--;
                inimigoIt.remove();
                if (vidaBase <= 0) {
                    estadoDoJogo = ESTADO_FIM_DE_JOGO;
                    return;
                }
            } else if (!inimigo.isAtivo()) {
                dinheiro += inimigo.getRecompensa();
                inimigoIt.remove();
            }
        }

        // Atualiza Torres e Projéteis
        for (Torre torre : torres) {
            torre.update();
        }

        Iterator<Projetil> projetilIt = projeteis.iterator();
        while (projetilIt.hasNext()) {
            Projetil p = projetilIt.next();
            if (p.isAtivo()) p.update();
            else projetilIt.remove();
        }

        // Checa Fim da Onda
        if (inimigos.isEmpty() && waveManager.isOndaCompletaDeSpawnar()) {
            waveManager.proximaOnda();

            if (waveManager.isUltimaOndaFinalizada()) {
                estadoDoJogo = ESTADO_VITORIA;
            } else {
                estadoDoJogo = ESTADO_PREPARACAO;
                proximaOndaTimer = System.nanoTime() + tempoEntreOndas;
                dinheiro += 150;
            }
        }
    }

    private void spawnInimigo(int tipo) {
        Point pontoInicial = tileManager.getCaminho().getFirst();
        Inimigo novoInimigo = null;

        switch (tipo) {
            case Wave.TIPO_BASICO:
                novoInimigo = new InimigoBasico(pontoInicial.x, pontoInicial.y, tileManager.getCaminho());
                break;
            case Wave.TIPO_RAPIDO:
                novoInimigo = new InimigoRapido(pontoInicial.x, pontoInicial.y, tileManager.getCaminho());
                break;
            case Wave.TIPO_TANK:
                novoInimigo = new InimigoTank(pontoInicial.x, pontoInicial.y, tileManager.getCaminho());
                break;
        }

        if (novoInimigo != null) {
            inimigos.add(novoInimigo);
        }
    }

    private void construirTorre(int col, int row) {
        if (tileManager.isTileValidoParaConstrucao(col, row) && !existeTorreNoLocal(col, row)) {
            Torre novaTorre = null;
            if (tipoTorreSelecionada == 1)
                novaTorre = new TorreCanhao(col, row, tamanhoDoTile, inimigos, projeteis);
            else if (tipoTorreSelecionada == 2)
                novaTorre = new TorreLaser(col, row, tamanhoDoTile, inimigos, projeteis);
            else if (tipoTorreSelecionada == 3)
                novaTorre = new TorreEspada(col, row, tamanhoDoTile, inimigos, projeteis);

            if (novaTorre != null && dinheiro >= novaTorre.getCusto()) {
                torres.add(novaTorre);
                dinheiro -= novaTorre.getCusto();
            }
        }
    }

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

    private Torre getTorreNoLocal(int col, int row) {
        for (Torre torre : torres) {
            if (torre.getCol() == col && torre.getRow() == row) {
                return torre;
            }
        }
        return null;
    }

    private boolean existeTorreNoLocal(int col, int row) {
        return getTorreNoLocal(col, row) != null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // --- DESENHO DO MENU ---
        if (estadoDoJogo == ESTADO_MENU) {
            if (menuImagem != null) {
                // Desenha a imagem preenchendo a tela toda
                g2.drawImage(menuImagem, 0, 0, larguraTela, alturaTela, null);
            } else {
                // Se não achar a imagem, pinta de azul
                g2.setColor(new Color(0, 20, 60));
                g2.fillRect(0, 0, larguraTela, alturaTela);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 50));
                g2.drawString("TUBARÕES VS ORCAS", larguraTela/2 - 250, alturaTela/2);
            }
            
            // Texto Piscando "PRESS ENTER"
            if ((System.currentTimeMillis() / 500) % 2 == 0) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                String msg = "PRESS ENTER TO START";
                int larg = g2.getFontMetrics().stringWidth(msg);
                g2.drawString(msg, (larguraTela - larg) / 2, alturaTela - 100);
            }
            
            g2.dispose();
            return;
        }

        // --- DESENHO DO JOGO NORMAL ---

        tileManager.draw(g2);
        for (Torre torre : torres) torre.draw(g2);
        for (Projetil projetil : projeteis) projetil.draw(g2);
        for (Inimigo inimigo : inimigos) inimigo.draw(g2);

        drawHUD(g2);

        if (torreSelecionada != null) {
            drawUpgradeHUD(g2);
            drawTorreRange(g2, torreSelecionada);
        } else if (estadoDoJogo != ESTADO_FIM_DE_JOGO && estadoDoJogo != ESTADO_VITORIA) {
            drawTorreSelectionHUD(g2);
            drawPlacementGhost(g2);
        }

        if (estadoDoJogo == ESTADO_FIM_DE_JOGO) drawTelaFimDeJogo(g2);
        else if (estadoDoJogo == ESTADO_VITORIA) drawTelaVitoria(g2);

        g2.dispose();
    }

    // --- MÉTODOS DO TECLADO (DETECTA O ENTER) ---
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Se apertar ENTER na tela de MENU, começa o jogo
        if (estadoDoJogo == ESTADO_MENU) {
            if (code == KeyEvent.VK_ENTER) {
                estadoDoJogo = ESTADO_PREPARACAO;
                // Reseta o timer da primeira onda para dar tempo de se preparar
                proximaOndaTimer = System.nanoTime() + tempoEntreOndas; 
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // --- HUD (Sem alterações) ---
    private void drawHUD(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        g2.drawString("Vida: " + vidaBase, 20, 30);
        g2.drawString("Dinheiro: $" + dinheiro, 20, 60);

        int ondaAtual = waveManager.getOndaAtualNumero();
        int totalOndas = waveManager.getTotalOndas();

        if (ondaAtual > totalOndas) ondaAtual = totalOndas;

        g2.drawString("Onda: " + ondaAtual + " / " + totalOndas, larguraTela - 180, 30);

        if (estadoDoJogo == ESTADO_PREPARACAO) {
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.setColor(Color.YELLOW);
            long tempoRestante = (proximaOndaTimer - System.nanoTime()) / 1_000_000_000L;
            String textoTimer = "Próxima onda em: " + (tempoRestante + 1) + "s";
            int textoLargura = g2.getFontMetrics().stringWidth(textoTimer);
            int x = (larguraTela - textoLargura) / 2;
            g2.drawString(textoTimer, x, alturaTela / 2);
        }
    }

    private void drawTorreSelectionHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, alturaTela - alturaHUD, larguraTela, alturaHUD);
        g2.setFont(new Font("Arial", Font.BOLD, 14));

        g2.setColor(tipoTorreSelecionada == 1 ? Color.YELLOW : Color.WHITE);
        g2.draw(boxTorreCanhao);
        g2.drawString("Tridente", boxTorreCanhao.x + 10, boxTorreCanhao.y + 20);
        g2.drawString("Custo: $" + TorreCanhao.CUSTO, boxTorreCanhao.x + 10, boxTorreCanhao.y + 45);

        g2.setColor(tipoTorreSelecionada == 2 ? Color.YELLOW : Color.WHITE);
        g2.draw(boxTorreLaser);
        g2.drawString("Sniper", boxTorreLaser.x + 10, boxTorreLaser.y + 20);
        g2.drawString("Custo: $" + TorreLaser.CUSTO, boxTorreLaser.x + 10, boxTorreLaser.y + 45);

        g2.setColor(tipoTorreSelecionada == 3 ? Color.YELLOW : Color.WHITE);
        g2.draw(boxTorreEspada);
        g2.drawString("Lixa", boxTorreEspada.x + 10, boxTorreEspada.y + 20);
        g2.drawString("Custo: $" + TorreEspada.CUSTO, boxTorreEspada.x + 10, boxTorreEspada.y + 45);
    }

    private void drawPlacementGhost(Graphics2D g2) {
        int col = mousePos.x / tamanhoDoTile;
        int row = mousePos.y / tamanhoDoTile;

        if (mousePos.y < alturaTela - alturaHUD) {
            Color cor = (tileManager.isTileValidoParaConstrucao(col, row) && !existeTorreNoLocal(col, row))
                    ? new Color(0, 255, 0, 100)
                    : new Color(255, 0, 0, 100);
            g2.setColor(cor);
            g2.fillRect(col * tamanhoDoTile, row * tamanhoDoTile, tamanhoDoTile, tamanhoDoTile);
        }
    }

    private void drawTorreRange(Graphics2D g2, Torre t) {
        g2.setColor(new Color(255, 255, 255, 100)); // Branco
        int xCentro = t.getX();
        int yCentro = t.getY();
        int alcance = t.getAlcance();
        int diametro = alcance * 2;
        g2.drawOval(xCentro - alcance, yCentro - alcance, diametro, diametro);
    }

    private void drawUpgradeHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, alturaTela - alturaHUD, larguraTela, alturaHUD);
        g2.setFont(new Font("Arial", Font.BOLD, 14));

        if (!torreSelecionada.isEspecializada()) {
            int custoUpg = torreSelecionada.getCustoUpgrade();
            Color corCusto;

            corCusto = (dinheiro >= custoUpg) ? Color.WHITE : Color.GRAY;
            g2.setColor(corCusto);
            g2.draw(boxUpgradeOpcao1);
            g2.setColor(Color.RED);
            g2.drawString("FOGO (Queimadura)", boxUpgradeOpcao1.x + 10, boxUpgradeOpcao1.y + 20);
            g2.setColor(corCusto);
            g2.drawString("Custo: $" + custoUpg, boxUpgradeOpcao1.x + 10, boxUpgradeOpcao1.y + 45);

            corCusto = (dinheiro >= custoUpg) ? Color.WHITE : Color.GRAY;
            g2.setColor(corCusto);
            g2.draw(boxUpgradeOpcao2);
            g2.setColor(Color.CYAN);
            g2.drawString("ÁGUA (Molhado)", boxUpgradeOpcao2.x + 10, boxUpgradeOpcao2.y + 20);
            g2.setColor(corCusto);
            g2.drawString("Custo: $" + custoUpg, boxUpgradeOpcao2.x + 10, boxUpgradeOpcao2.y + 45);

            corCusto = (dinheiro >= custoUpg) ? Color.WHITE : Color.GRAY;
            g2.setColor(corCusto);
            g2.draw(boxUpgradeOpcao3);
            g2.setColor(Color.GREEN);
            g2.drawString("ALGA (Lentidão)", boxUpgradeOpcao3.x + 10, boxUpgradeOpcao3.y + 20);
            g2.setColor(corCusto);
            g2.drawString("Custo: $" + custoUpg, boxUpgradeOpcao3.x + 10, boxUpgradeOpcao3.y + 45);

        } else {
            g2.setColor(Color.WHITE);
            g2.drawString("Torre Nível " + torreSelecionada.getNivel(), 20, alturaTela - alturaHUD + 30);
            g2.drawString("Elemento: " + torreSelecionada.getElemento(), 20, alturaTela - alturaHUD + 50);
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

    private void drawTelaVitoria(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, larguraTela, alturaTela);
        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.GREEN);
        String texto = "VOCÊ VENCEU!";
        int textoLargura = g2.getFontMetrics().stringWidth(texto);
        int x = (larguraTela - textoLargura) / 2;
        int y = alturaTela / 2;
        g2.drawString(texto, x, y);
    }
}
