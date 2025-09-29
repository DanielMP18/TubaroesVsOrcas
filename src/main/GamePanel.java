package game;

import entity.Inimigo;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import map.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // ... (suas configurações de tela continuam as mesmas)
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

    // ESTADO DO JOGO
    private int vidaBase = 20; // Vida inicial
    private int ondaAtual = 1;
    
    // NOVO: Variáveis de Estado do Jogo
    public static final int ESTADO_JOGANDO = 1;
    public static final int ESTADO_FIM_DE_JOGO = 2;
    private int estadoDoJogo;


    // SPAWN DE INIMIGOS
    private long ultimoSpawnTime = System.nanoTime();
    private long spawnCooldown = 1_500_000_000L;

    public GamePanel() {
        this.setPreferredSize(new Dimension(larguraTela, alturaTela));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.tileManager = new TileManager(tamanhoDoTile, maxColunasTela, maxLinhasTela);
        this.inimigos = new ArrayList<>();
        
        // NOVO: Inicia o jogo no estado "JOGANDO"
        this.estadoDoJogo = ESTADO_JOGANDO;
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

    // ATUALIZA A LÓGICA DO JOGO
    public void update() {
        // ALTERADO: Só executa a lógica do jogo se estivermos no estado "JOGANDO"
        if (estadoDoJogo == ESTADO_JOGANDO) {
            spawnInimigo();
            
            List<Inimigo> inimigosParaRemover = new ArrayList<>();

            for (Inimigo inimigo : inimigos) {
                inimigo.update();
                if (inimigo.chegouNaBase()) {
                    vidaBase--;
                    inimigosParaRemover.add(inimigo);
                    System.out.println("Inimigo alcançou a base! Vida restante: " + vidaBase);
                    
                    // NOVO: Verifica a condição de fim de jogo
                    if (vidaBase <= 0) {
                        estadoDoJogo = ESTADO_FIM_DE_JOGO;
                        System.out.println("FIM DE JOGO!");
                    }
                }
            }
            inimigos.removeAll(inimigosParaRemover);
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

    // DESENHA TUDO NA TELA
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Desenha o mapa
        tileManager.draw(g2);

        // Desenha os inimigos
        for (Inimigo inimigo : inimigos) {
            inimigo.draw(g2);
        }
        
        // Desenha a HUD
        drawHUD(g2);
        
        // NOVO: Desenha a tela de Fim de Jogo se o estado for o correto
        if (estadoDoJogo == ESTADO_FIM_DE_JOGO) {
            drawTelaFimDeJogo(g2);
        }

        g2.dispose();
    }
    
    // DESENHA A HUD MINIMALISTA
    private void drawHUD(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        
        String textoVida = "Vida da Base: " + vidaBase;
        g2.drawString(textoVida, 20, 30);
        
        String textoOnda = "Onda: " + ondaAtual;
        g2.drawString(textoOnda, larguraTela - 150, 30);
    }
    
    // NOVO: Método para desenhar a tela de Fim de Jogo
    private void drawTelaFimDeJogo(Graphics2D g2) {
        // Cria um fundo escuro semi-transparente para focar na mensagem
        g2.setColor(new Color(0, 0, 0, 150)); // Cor preta com 150 de transparência
        g2.fillRect(0, 0, larguraTela, alturaTela);
        
        // Configura a fonte e a cor da mensagem
        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.RED);
        
        String texto = "FIM DE JOGO";
        
        // Centraliza o texto na tela
        int textoLargura = g2.getFontMetrics().stringWidth(texto);
        int x = (larguraTela - textoLargura) / 2;
        int y = alturaTela / 2;
        
        g2.drawString(texto, x, y);
    }
}