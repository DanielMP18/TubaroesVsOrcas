package entity;

import game.Elemento; // Importar o Enum
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public abstract class Inimigo {

    protected float x, y;
    protected int vida;
    protected int vidaMaxima;
    protected float velocidade;
    protected boolean ativo = true;
    protected int recompensa;
    protected int largura, altura;
    protected List<Point> caminho;
    protected int pontoAlvoIndex = 0;
    
    protected Elemento elemento; 

    // --- Variáveis de Efeito de Status ---
    private float originalVelocidade;
    private long slowEffectEndTime = 0;   
    private long burnEffectEndTime = 0;   
    private int burnDamagePerTick = 0;
    private long lastBurnTickTime = 0;
    private final long BURN_TICK_INTERVAL = 1_000_000_000L; // 1 seg
    private boolean isMolhado = false; // "Molhado" é consumido

    public Inimigo(float x, float y, List<Point> caminho, int vidaMaxima, float velocidade, int recompensa, int largura, int altura) {
        this.x = x;
        this.y = y;
        this.caminho = caminho;
        this.vidaMaxima = vidaMaxima;
        this.vida = this.vidaMaxima;
        this.velocidade = velocidade;
        this.recompensa = recompensa;
        this.largura = largura;
        this.altura = altura;
        
        this.originalVelocidade = velocidade; // Salva a velocidade original
    }

    public abstract void drawInimigo(Graphics2D g2);

    public void update() {
        if (!ativo) return; 
        
        long agora = System.nanoTime();
        
        
        
        // Queimadura (Fogo)
        if (agora < burnEffectEndTime) {
            if (agora - lastBurnTickTime > BURN_TICK_INTERVAL) {
                this.vida -= burnDamagePerTick;
                this.lastBurnTickTime = agora;
                if (this.vida <= 0) {
                    desativar();
                    return; // Morreu pela queimadura
                }
            }
        }
        
        // Lentidão (Alga)
        if (agora > slowEffectEndTime) {
            this.velocidade = this.originalVelocidade; // Efeito acabou
        }
        
        // --- 2. Lógica de Movimento ---
        if (pontoAlvoIndex >= caminho.size()) {
            desativar(); // Chegou na base (será removido no GamePanel)
            return;
        }

        Point alvo = caminho.get(pontoAlvoIndex);
        float alvoX = alvo.x;
        float alvoY = alvo.y;

        float angulo = (float) Math.atan2(alvoY - y, alvoX - x);
        x += velocidade * Math.cos(angulo);
        y += velocidade * Math.sin(angulo);

        double distancia = Math.sqrt(Math.pow(alvoX - x, 2) + Math.pow(alvoY - y, 2));
        if (distancia < velocidade) {
            pontoAlvoIndex++;
        }
    }

    public void draw(Graphics2D g2) {
        drawInimigo(g2); // Desenha a forma do inimigo

        // Desenha a barra de vida
        if (vida < vidaMaxima) {
            g2.setColor(Color.GRAY);
            g2.fillRect((int) x, (int) y - 8, largura, 5); 
            g2.setColor(Color.GREEN);
            float larguraVida = (float) vida / vidaMaxima * largura;
            g2.fillRect((int) x, (int) y - 8, (int) larguraVida, 5);
        }
        
        // --- Desenha ícones de status ---
        long agora = System.nanoTime();
        int iconX = (int) x;
        
        if (isMolhado) {
            g2.setColor(Color.BLUE);
            g2.fillOval(iconX, (int) y - 16, 8, 8); // Pingo
            iconX += 10;
        }
        if (agora < burnEffectEndTime) {
            g2.setColor(Color.ORANGE);
            g2.fillOval(iconX, (int) y - 16, 8, 8); // Chama
            iconX += 10;
        }
        if (agora < slowEffectEndTime) {
            g2.setColor(Color.CYAN); // (Usando Ciano para lentidão)
            g2.fillOval(iconX, (int) y - 16, 8, 8); // Gelo
        }
    }

    // --- LÓGICA DE DANO E EFEITOS ---
    // Em Inimigo.java

    public void levarDano(int danoBase, Elemento elementoAtaque) {
    if (!ativo) return; // Sai se o inimigo já estiver inativo
    
    // (Opcional: Um aviso no console se você esquecer de definir um elemento)
    if (this.elemento == null) {
        System.out.println("AVISO: " + this.getClass().getName() + " não tem um 'elemento' definido! O dano será NEUTRO.");
    }
    
    double multiplicadorExtra = 1.0; 
    
    // 1. Checa "Molhado" (Lógica Combo)
    if (this.isMolhado) {
        if (elementoAtaque == Elemento.FOGO) { 
            multiplicadorExtra = 1.5; 
            System.out.println("VAPORIZOU! Dano extra.");
        } else if (elementoAtaque == Elemento.ALGA) {
            multiplicadorExtra = 1.5;
            System.out.println("SUPER CRESCIMENTO! Dano extra.");
        }
        this.isMolhado = false; // Consome o "Molhado"
    }
    
    // 2. Calcula Dano (Pedra-Papel-Tesoura)
    // Esta linha agora chama o 'getModificador' (Parte 1) que é seguro
    double modElemental = Elemento.getModificador(elementoAtaque, this.elemento);
    int danoFinal = (int) (danoBase * modElemental * multiplicadorExtra);
    this.vida -= danoFinal;

    // 3. Aplica Novos Efeitos
    long agora = System.nanoTime();
    
    // Verificação 'if' para o switch
    if (elementoAtaque != null) {
        switch (elementoAtaque) { 
            case FOGO:
                this.burnEffectEndTime = agora + 3_000_000_000L; // 3 seg
                this.burnDamagePerTick = 5; // 5 dano/seg
                this.lastBurnTickTime = agora;
                this.isMolhado = false; // Evapora
                break;
            case ALGA:
                this.slowEffectEndTime = agora + 2_000_000_000L; // 2 seg
                this.velocidade = this.originalVelocidade * 0.5f; // 50% lento
                break;
            case AGUA:
                this.isMolhado = true;
                this.burnEffectEndTime = 0; // Apaga o fogo
                this.burnDamagePerTick = 0;
                break;
            case NEUTRO:
                // Nenhum efeito
                break;
        }
    }

    // 4. Checa se o inimigo morreu
    if (this.vida <= 0) {
        desativar();
        }
    }

    // --- Getters ---
    public int getRecompensa() { return recompensa; }
    public boolean chegouNaBase() { return pontoAlvoIndex >= caminho.size() && ativo; }
    public void desativar() { this.ativo = false; }
    public boolean isAtivo() { return ativo; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getLargura() { return largura; }
    public int getAltura() { return altura; }
}
