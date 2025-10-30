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

    // ... (Configurações de tela permanecem iguais)
    final int tamanhoOriginalTile = 16;
    final int escala = 3;
    public final int tamanhoDoTile = tamanhoOriginalTile * escala; // 48
    public final int maxColunasTela = 25;
    public final int maxLinhasTela = 15;
    public final int larguraTela = tamanhoDoTile * maxColunasTela; // 1200
    public final int alturaTela = tamanhoDoTile * maxLinhasTela; // 720

    private Thread gameThread;

    // GERENCIADORES E ENTIDADES
    private TileManager tileManager;
    private List<Inimigo> inimigos;
    private List<Torre> torres;
    private List<Projetil> projeteis;

    // ESTADO DO JOGO
    private int vidaBase = 20;
    private int ondaAtual = 1;
    private int dinheiro = 250; 

    // <-- MUDANÇA: Novos estados de jogo
    public static final int ESTADO_PREPARACAO = 0; // Esperando a onda começar
    public static final int ESTADO_JOGANDO = 1;      // Onda em progresso
    public static final int ESTADO_FIM_DE_JOGO = 2;  // Perdeu
    public static final int ESTADO_VITORIA = 3;    // Ganhou
    private int estadoDoJogo;

    // SPAWN DE INIMIGOS
    private long ultimoSpawnTime = System.nanoTime();
    private long spawnCooldown = 1_500_000_000L; // 1.5 segundos

    // <-- MUDANÇA: Definição das Ondas
    private final int maxOndas = 3; // O jogo terá 3 ondas
    private int[] inimigosPorOnda = { 10, 15, 25 }; // Onda 1=10, Onda 2=15, Onda 3=25
    private int inimigosSpawndosNaOnda = 0;
    private long tempoEntreOndas = 5_000_000_000L; // 10 segundos
    private long proximaOndaTimer = 0;

    // ... (Controles do Jogador e HUD permanecem iguais)
    private Point mousePos = new Point();
    private int tipoTorreSelecionada = 1;
    private final int alturaHUD = 80;
    private final Rectangle boxTorreCanhao = new Rectangle(20, alturaTela - alturaHUD + 10, 140, 60);
    private final Rectangle boxTorreLaser = new Rectangle(170, alturaTela - alturaHUD + 10, 140, 60);


    public GamePanel() {
        this.setPreferredSize(new Dimension(larguraTela, alturaTela));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        this.tileManager = new TileManager(tamanhoDoTile, maxColunasTela, maxLinhasTela);
        this.inimigos = new ArrayList<>();
        this.torres = new ArrayList<>();
        this.projeteis = new ArrayList<>();

        this.estadoDoJogo = ESTADO_PREPARACAO; // <-- MUDANÇA: Começa em modo de preparação
        this.proximaOndaTimer = System.nanoTime() + tempoEntreOndas; // <-- MUDANÇA: Prepara o timer para a Onda 1
        
        adicionarControles();
    }

    // ... (Métodos adicionarControles, startGameThread, run permanecem os mesmos) ...
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
                // <-- MUDANÇA: Só pode clicar se não tiver perdido ou ganhado
                if (estadoDoJogo == ESTADO_FIM_DE_JOGO || estadoDoJogo == ESTADO_VITORIA) return;

                if (e.getY() < alturaTela - alturaHUD) { 
                    int col = e.getX() / tamanhoDoTile;
                    int row = e.getY() / tamanhoDoTile;

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        construirTorre(col, row);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        venderTorre(col, row);
                    }
                } else { 
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
        double drawInterval = 1000000000.0 / 60.0;
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


    // ATUALIZA A LÓGICA DO JOGO
    // <-- MUDANÇA: Lógica de update totalmente refeita para suportar ondas
    public void update() {
        // Se o jogo acabou (venceu ou perdeu), não faz mais nada
        if (estadoDoJogo == ESTADO_FIM_DE_JOGO || estadoDoJogo == ESTADO_VITORIA) {
            return;
        }

        // Se está em preparação, apenas checa o timer
        if (estadoDoJogo == ESTADO_PREPARACAO) {
            if (System.nanoTime() > proximaOndaTimer) {
                estadoDoJogo = ESTADO_JOGANDO; // Começa a onda!
                inimigosSpawndosNaOnda = 0; // Reseta o contador de spawn
            }
            return; // Não atualiza torres, inimigos, etc.
        }

        // Se chegou aqui, o estado é ESTADO_JOGANDO
        
        // 1. Tenta "spawnar" inimigos
        spawnInimigo();

        // 2. Atualiza Inimigos
        Iterator<Inimigo> inimigoIt = inimigos.iterator();
        while (inimigoIt.hasNext()) {
            Inimigo inimigo = inimigoIt.next();
            inimigo.update();
            
            if (inimigo.chegouNaBase()) {
                vidaBase--;
                inimigoIt.remove(); 
                System.out.println("Inimigo alcançou a base! Vida restante: " + vidaBase);

                if (vidaBase <= 0) {
                    estadoDoJogo = ESTADO_FIM_DE_JOGO; // PERDEU
                    System.out.println("FIM DE JOGO!");
                    return; // Sai imediatamente do update
                }
            } else if (!inimigo.isAtivo()) {
                dinheiro += inimigo.getRecompensa();
                inimigoIt.remove(); 
            }
        }

        // 3. Atualiza Torres
        for (Torre torre : torres) {
            torre.update();
        }

        // 4. Atualiza Projéteis
        Iterator<Projetil> projetilIt = projeteis.iterator();
        while (projetilIt.hasNext()) {
            Projetil p = projetilIt.next();
            if (p.isAtivo()) {
                p.update();
            } else {
                projetilIt.remove();
            }
        }

        // 5. Verifica se a onda terminou
        // Condição: Todos os inimigos da onda foram spawnados E não há mais inimigos na tela
        if (inimigos.isEmpty() && inimigosSpawndosNaOnda >= inimigosPorOnda[ondaAtual - 1]) {
            // ONDA VENCIDA!
            
            // Verifica se foi a ÚLTIMA onda
            if (ondaAtual >= maxOndas) {
                estadoDoJogo = ESTADO_VITORIA;
                System.out.println("VOCÊ VENCEU!");
            } else {
                // Prepara para a próxima onda
                estadoDoJogo = ESTADO_PREPARACAO;
                ondaAtual++;
                proximaOndaTimer = System.nanoTime() + tempoEntreOndas;
                dinheiro += 100; // Bônus por vencer a onda
            }
        }
    }
    
    // <-- MUDANÇA: Lógica de spawn refeita para ondas
    private void spawnInimigo() {
        // 1. Checa se a onda já foi totalmente "spawnada"
        // (Lembre-se: ondaAtual é 1, 2, 3... mas o array é 0, 1, 2)
        if (inimigosSpawndosNaOnda >= inimigosPorOnda[ondaAtual - 1]) {
            return; // Todos os inimigos da onda já foram criados. Não faz mais nada.
        }

        // 2. Se ainda não, checa o cooldown para o próximo inimigo
        long agora = System.nanoTime();
        if (agora - ultimoSpawnTime > spawnCooldown) {
            Point pontoInicial = tileManager.getCaminho().get(0);
            Inimigo novoInimigo = new Inimigo(pontoInicial.x, pontoInicial.y, tileManager.getCaminho());
            inimigos.add(novoInimigo);
            
            ultimoSpawnTime = agora;
            inimigosSpawndosNaOnda++; // <-- MUDANÇA: Incrementa o contador da onda
        }
    }

    // ... (Métodos construirTorre, venderTorre, existeTorreNoLocal permanecem os mesmos) ...
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

        // Desenha o mapa
        tileManager.draw(g2);

        // Desenha as torres
        for (Torre torre : torres) {
            torre.draw(g2);
        }

        // Desenha os projéteis
        for (Projetil projetil : projeteis) {
            projetil.draw(g2);
        }

        // Desenha os inimigos
        for (Inimigo inimigo : inimigos) {
            inimigo.draw(g2);
        }
        
        // Desenha a HUD (agora desenha o timer também)
        drawHUD(g2);
        drawPlacementGhost(g2);
        drawTorreSelectionHUD(g2);

        // <-- MUDANÇA: Verifica o estado de FIM DE JOGO ou VITÓRIA
        if (estadoDoJogo == ESTADO_FIM_DE_JOGO) {
            drawTelaFimDeJogo(g2);
        } else if (estadoDoJogo == ESTADO_VITORIA) { // <-- MUDANÇA
            drawTelaVitoria(g2); // <-- MUDANÇA
        }

        g2.dispose();
    }
    
    // DESENHA A HUD
    // <-- MUDANÇA: drawHUD agora desenha o timer de preparação
    private void drawHUD(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        
        String textoVida = "Vida: " + vidaBase;
        g2.drawString(textoVida, 20, 30);
        
        String textoDinheiro = "Dinheiro: $" + dinheiro;
        g2.drawString(textoDinheiro, 20, 60);

        String textoOnda = "Onda: " + ondaAtual + " / " + maxOndas; // <-- MUDANÇA
        g2.drawString(textoOnda, larguraTela - 150, 30);

        // <-- MUDANÇA: Adiciona o timer de preparação
        if (estadoDoJogo == ESTADO_PREPARACAO) {
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.setColor(Color.YELLOW);
            // Calcula o tempo restante em segundos
            long tempoRestante = (proximaOndaTimer - System.nanoTime()) / 1_000_000_000L;
            String textoTimer = "Próxima onda em: " + (tempoRestante + 1) + "s";

            // Centraliza o timer na tela
            int textoLargura = g2.getFontMetrics().stringWidth(textoTimer);
            int x = (larguraTela - textoLargura) / 2;
            g2.drawString(textoTimer, x, alturaTela / 2);
        }
    }
    
    // ... (drawTorreSelectionHUD e drawPlacementGhost permanecem os mesmos) ...
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
        // <-- MUDANÇA: Não desenha o "fantasma" se o jogo acabou
        if (estadoDoJogo == ESTADO_FIM_DE_JOGO || estadoDoJogo == ESTADO_VITORIA) return;

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


    // ... (drawTelaFimDeJogo permanece o mesmo) ...
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

    // <-- MUDANÇA: Novo método para desenhar a tela de Vitória
    private void drawTelaVitoria(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, larguraTela, alturaTela);
        
        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.GREEN); // Vitória é verde!
        
        String texto = "VOCÊ VENCEU!";
        
        int textoLargura = g2.getFontMetrics().stringWidth(texto);
        int x = (larguraTela - textoLargura) / 2;
        int y = alturaTela / 2;
        
        g2.drawString(texto, x, y);
    }
}
