# 🦈 Tubarões vs Orcas - Tower Defense 2D

Um jogo **Tower Defense 2D** temático onde tubarões enfrentam orcas em batalhas aquáticas.

---

## 🚀 Como Executar o Projeto

### 1. Pré-requisitos
- [Java JDK 17+](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) instalado  
- Um editor ou IDE (recomendado: [IntelliJ IDEA](https://www.jetbrains.com/idea/)) ou apenas terminal  

### 2. Clonar o repositório
```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```
3. Compilar o código

No terminal, dentro da pasta onde estão os arquivos .java, rode:
```bash
javac Main.java GamePanel.java TileManager.java Inimigo.java
```
4. Executar o jogo

Agora execute o programa principal:
```bash
java Main
```

🎨 Justificativa de Design

Visão Geral do Sistema

Este projeto implementa um jogo de Tower Defense com sistema elemental, onde o jogador posiciona torres estrategicamente para defender sua base contra ondas de inimigos. O design foi estruturado para promover estratégia profunda, escalabilidade e manutenibilidade.
Princípios de Design Aplicados

// Arquitetura modularizada:
GamePanel      → Lógica principal e UI
WaveManager    → Gerenciamento de ondas
TileManager    → Mapa e caminhos
Entity classes → Comportamento de entidades
Elemento Enum  → Regras do sistema elemental


5. Sistema de Componentes para Efeitos

    Status Effects (queimadura, lentidão, molhado) implementados como estado na classe base Inimigo

    Benefício: Efeitos podem ser combinados e interagem entre si de forma previsível

Decisões de Design Principais
Sistema Elemental Triádico
java

// Pedra-Papel-Tesoura balanceado:
AGUA > FOGO > ALGA > AGUA

    Combos: Estado "Molhado" permite interações especiais (vaporização, super crescimento)

    Modificadores: 2.0x (forte), 0.5x (fraco), 1.0x (normal)

    Efeitos Únicos: Cada elemento aplica status effects distintos

Sistema de Upgrade de Torres
java

// Design de especialização:
Nível 1: Torre básica (NEUTRO)
Nível 2: Escolha elemental (FOGO/AGUA/ALGA) + bônus

    Custo progressivo: Incentiva planejamento econômico

    Diferenciação estratégica: Cada elemento serve a propósito tático diferente

Gerenciamento de Ondas Baseado em Dados

    Wave Manager separa configuração de ondas da lógica de spawn

    List-based spawning: Fácil criação de novas ondas via código

    Cooldown dinâmico: Inimigos rápidos spawnam mais rápido


Game Loop Clássico
java

public void run() {
    while (gameThread != null) {
        update();
        repaint();
        // ... controle de FPS
    }
}

Gerenciamento de Estado do Jogo
java

ESTADO_PREPARAÇÃO → ESTADO_JOGANDO → ESTADO_FIM_DE_JOGO/ESTADO_VITORIA

    Transições claras: Interface adapta-se ao estado atual

    Fluxo controlado: Impede ações inválidas durante estados específicos

Decisões Técnicas Notáveis
Targeting

    Nearest-target: Torres priorizam inimigos mais próximos

    Projectile homing: Projéteis seguem alvos em movimento

Sistema de Economia

    Recompensa por eliminação: Incentiva eficiência

    Custo de oportunidade: Upgrade vs. novas torres

    Reembolso parcial: Venda retorna 50% do investimento

Renderização e UI

    Range indicators: Visualização de alcance das torres

    HUD contextual: Mostra opções relevantes ao estado atual

Extensibilidade e Manutenibilidade
Pontos de Extensão Fáceis

    Novos Inimigos: Estender classe Inimigo + adicionar ao WaveManager

    Novas Torres: Estender classe Torre + adicionar à HUD

    Novos Elementos: Adicionar ao enum Elemento + regras de modificador

    Novos Mapas: Modificar TileManager.criarMapaFixo()

Tratamento de Erros Robusto
java

// Em Elemento.getModificador():
if (atacante == null || defensor == null || ...) {
    return MOD_NORMAL; // Previne NullPointerException
}

Logging para Debug

    Mensagens console para combos elementais

    Avisos para elementos não definidos

Conclusão

Este design demonstra uma arquitetura bem pensada que equilibra complexidade de recursos com código de facil manutenção. O sistema elemental adiciona profundidade estratégica sem sobrecarregar o jogador, enquanto a estrutura orientada a objetos permite facilidade em expandir. A separação clara de responsabilidades entre as classes garante que modificações em uma área tenham impacto mínimo nas outras.


---

## Diagrama de Classes UML

# 🏰 Tower Defense - Tubarões Vs Orcas

## 📊 Diagrama de Arquitetura

```mermaid
classDiagram
    direction TB
    
    %% ========== ENUMS ==========
    class Elemento {
        <<enumeration>>
        AGUA
        FOGO
        ALGA
        NEUTRO
        +getModificador(Elemento atacante, Elemento defensor) double
    }

    %% ========== ENTITY CLASSES ==========
    class Torre {
        <<abstract>>
        #int x, y
        #int col, row
        #int tamanho
        #int custo
        #int alcance
        #long ultimoDisparo
        #long cadenciaDeTiro
        #Inimigo alvo
        #List~Inimigo~ inimigos
        #List~Projetil~ projeteis
        #Elemento elemento
        #int nivel
        #int custoUpgrade
        #int danoBase
        +update() void
        +especializar(Elemento) void
        +getCusto() int
        +getCol() int
        +getRow() int
        +getElemento() Elemento
        +getNivel() int
        +getCustoUpgrade() int
        +isEspecializada() boolean
        +getX() int
        +getY() int
        +getAlcance() int
        <<abstract>>
        #atirar() void
        <<abstract>>
        +draw(Graphics2D) void
    }

    class TorreCanhao {
        +CUSTO: int = 100
        +TorreCanhao(int, int, int, List~Inimigo~, List~Projetil~)
        #atirar() void
        +draw(Graphics2D) void
    }

    class TorreLaser {
        +CUSTO: int = 180
        +TorreLaser(int, int, int, List~Inimigo~, List~Projetil~)
        #atirar() void
        +draw(Graphics2D) void
    }

    class TorreEspada {
        +CUSTO: int = 70
        +TorreEspada(int, int, int, List~Inimigo~, List~Projetil~)
        #atirar() void
        +draw(Graphics2D) void
    }

    class Inimigo {
        <<abstract>>
        #float x, y
        #int vida
        #int vidaMaxima
        #float velocidade
        #boolean ativo
        #int recompensa
        #int largura, altura
        #List~Point~ caminho
        #int pontoAlvoIndex
        #Elemento elemento
        #float originalVelocidade
        #long slowEffectEndTime
        #long burnEffectEndTime
        #int burnDamagePerTick
        #long lastBurnTickTime
        #boolean isMolhado
        +update() void
        +draw(Graphics2D) void
        +levarDano(int, Elemento) void
        +getRecompensa() int
        +chegouNaBase() boolean
        +desativar() void
        +isAtivo() boolean
        +getX() float
        +getY() float
        +getLargura() int
        +getAltura() int
        <<abstract>>
        +drawInimigo(Graphics2D) void
    }

    class InimigoBasico {
        +InimigoBasico(float, float, List~Point~)
        +drawInimigo(Graphics2D) void
        -vidaMaxima: int = 200
        -velocidade: float = 1.5f
        -recompensa: int = 10
        -largura: int = 32
        -altura: int = 32
        -elemento: Elemento = FOGO
    }

    class InimigoRapido {
        +InimigoRapido(float, float, List~Point~)
        +drawInimigo(Graphics2D) void
        -vidaMaxima: int = 100
        -velocidade: float = 3.0f
        -recompensa: int = 5
        -largura: int = 24
        -altura: int = 24
        -elemento: Elemento = ALGA
    }

    class InimigoTank {
        +InimigoTank(float, float, List~Point~)
        +drawInimigo(Graphics2D) void
        -vidaMaxima: int = 1000
        -velocidade: float = 0.8f
        -recompensa: int = 25
        -largura: int = 40
        -altura: int = 40
        -elemento: Elemento = AGUA
    }

    class Projetil {
        -float x, y
        -float velocidade
        -int dano
        -Inimigo alvo
        -boolean ativo
        -Color cor
        -Elemento elemento
        +Projetil(float, float, float, int, Inimigo, Color, Elemento)
        +update() void
        +draw(Graphics2D) void
        +desativar() void
        +isAtivo() boolean
    }

    %% ========== GAME MANAGEMENT ==========
    class GamePanel {
        -Thread gameThread
        -TileManager tileManager
        -WaveManager waveManager
        -List~Inimigo~ inimigos
        -List~Torre~ torres
        -List~Projetil~ projeteis
        -int vidaBase
        -int dinheiro
        -int estadoDoJogo
        -long tempoEntreOndas
        -long proximaOndaTimer
        -Point mousePos
        -int tipoTorreSelecionada
        -Torre torreSelecionada
        -int alturaHUD
        -Rectangle boxTorreCanhao, boxTorreLaser, boxTorreEspada
        -Rectangle boxUpgradeOpcao1, boxUpgradeOpcao2, boxUpgradeOpcao3
        +GamePanel()
        +startGameThread() void
        +run() void
        +update() void
        +paintComponent(Graphics) void
        -spawnInimigo(int) void
        -construirTorre(int, int) void
        -venderTorre(int, int) void
        -getTorreNoLocal(int, int) Torre
        -existeTorreNoLocal(int, int) boolean
        -drawHUD(Graphics2D) void
        -drawTorreSelectionHUD(Graphics2D) void
        -drawUpgradeHUD(Graphics2D) void
        -drawPlacementGhost(Graphics2D) void
        -drawTorreRange(Graphics2D, Torre) void
        -drawTelaFimDeJogo(Graphics2D) void
        -drawTelaVitoria(Graphics2D) void
    }

    class WaveManager {
        -List~Wave~ waves
        -int ondaAtualIndex
        -int inimigoAtualIndex
        -long ultimoSpawnTime
        -long spawnCooldown
        +WaveManager()
        +getProximoInimigoParaSpawnar() int
        +isOndaCompletaDeSpawnar() boolean
        +proximaOnda() void
        +getOndaAtualNumero() int
        +getTotalOndas() int
        +isUltimaOndaFinalizada() boolean
        -criarOndas() void
    }

    class Wave {
        -List~Integer~ listaInimigos
        +TIPO_BASICO: int = 0
        +TIPO_RAPIDO: int = 1
        +TIPO_TANK: int = 2
        +Wave()
        +adicionarInimigo(int, int) void
        +getListaInimigos() List~Integer~
    }

    class TileManager {
        -int tamanhoDoTitulo
        -int maxColunas
        -int maxLinhas
        -int[][] mapGrid
        -List~Point~ caminho
        +TileManager(int, int, int)
        +draw(Graphics2D) void
        +isTileValidoParaConstrucao(int, int) boolean
        +getCaminho() List~Point~
        -criarMapaFixo() void
        -desenharSegmento(int, int, int, int) void
    }

    class Main {
        +main(String[] args) void
    }

    %% ========== RELATIONSHIPS ==========
    Torre <|-- TorreCanhao
    Torre <|-- TorreLaser
    Torre <|-- TorreEspada
    
    Inimigo <|-- InimigoBasico
    Inimigo <|-- InimigoRapido
    Inimigo <|-- InimigoTank

    GamePanel "1" *-- "1" TileManager
    GamePanel "1" *-- "1" WaveManager
    GamePanel "1" *-- "*" Inimigo
    GamePanel "1" *-- "*" Torre
    GamePanel "1" *-- "*" Projetil
    
    WaveManager "1" *-- "*" Wave
    
    Torre "1" *-- "*" Inimigo : targets
    Torre "1" *-- "*" Projetil : shoots
    Torre "1" *-- "1" Elemento : has
    
    Inimigo "1" *-- "1" Elemento : has
    Inimigo "1" *-- "*" Point : follows path
    
    Projetil "1" *-- "1" Inimigo : targets
    Projetil "1" *-- "1" Elemento : has
    
    Wave "1" *-- "*" Integer : contains enemy types
```

## 🎯 Legenda do Diagrama

- **🔷 Classes Abstratas**: `Torre`, `Inimigo` (métodos e atributos compartilhados)
- **🔶 Classes Concretas**: Especializações com stats específicos
- **📦 Enumerações**: `Elemento` com sistema de combate triádico
- **🔗 Composição**: Relação "tem-um" (losango preto)
- **🔄 Herança**: Relação "é-um" (seta oca)

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   └── Main.java                 # Ponto de entrada
├── game/
│   ├── GamePanel.java           # Loop principal e UI (60 FPS)
│   ├── Elemento.java            # Sistema elemental (Fogo > Alga > Água > Fogo)
│   ├── WaveManager.java         # Gerenciador de 10 ondas progressivas
│   └── Wave.java                # Configuração de ondas com tipos de inimigos
├── entity/
│   ├── Torre.java               # Classe base com targeting e upgrade system
│   ├── TorreCanhao.java         # Torre balanceada (alcance médio, dano médio)
│   ├── TorreLaser.java          # Torre de alto dano (cadência lenta)
│   ├── TorreEspada.java         # Torre rápida (dano baixo, ataque rápido)
│   ├── Inimigo.java             # Classe base com sistema de status effects
│   ├── InimigoBasico.java       # Inimigo básico (elemento Fogo)
│   ├── InimigoRapido.java       # Inimigo rápido (elemento Alga)  
│   ├── InimigoTank.java         # Inimigo tanque (elemento Água)
│   └── Projetil.java            # Projéteis com homing e sistema elemental
└── map/
    └── TileManager.java          # Mapa fixo com caminho pré-definido
```

