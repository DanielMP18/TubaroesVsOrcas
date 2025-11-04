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

O objetivo principal é desenvolver um jogo do subgênero Tower Defense (TD) utilizando a linguagem Java para aplicar e consolidar conceitos fundamentais de programação orientada a objetos (POO) e estruturas de desenvolvimento de jogos 2D. O foco está na criação de um Game Loop funcional, um sistema de movimentação de inimigos com pathing e a implementação de mecânicas centrais do gênero (ondas, defesa de base e colocação estratégica de torres).

O projeto Tubarões vs Orcas – Tower Defense 2D foi desenvolvido seguindo princípios de organização modular buscando facilidade na manutenção.
Cada classe representa um componente independente dentro da arquitetura do jogo, contribuindo para um sistema coeso e expansível.


---

## Diagrama de Classes UML

```mermaid
    classDiagram
    %% Entidades principais do jogo
    class GamePanel {
        -Thread gameThread
        -TileManager tileManager
        -List~Inimigo~ inimigos
        -List~Torre~ torres
        -List~Projetil~ projeteis
        -int vidaBase
        -int ondaAtual
        -int dinheiro
        -int estadoDoJogo
        -int maxOndas
        -int[] inimigosPorOnda
        -startGameThread()
        +update()
        +paintComponent(Graphics g)
        -spawnInimigo()
        -construirTorre(int col, int row)
        -venderTorre(int col, int row)
    }

    class TileManager {
        -int tamanhoDoTitulo
        -int maxColunas
        -int maxLinhas
        -int[][] mapGrid
        -List~Point~ caminho
        +draw(Graphics2D g2)
        +isTileValidoParaConstrucao(int col, int row)
        +getCaminho() List~Point~
    }

    class Inimigo {
        -float x, y
        -int vida
        -int vidaMaxima
        -float velocidade
        -boolean ativo
        -int recompensa
        -List~Point~ caminho
        -int pontoAlvoIndex
        +update()
        +draw(Graphics2D g2)
        +levarDano(int dano)
        +chegouNaBase() boolean
        +desativar()
        +isAtivo() boolean
    }

    class Projetil {
        -float x, y
        -float velocidade
        -int dano
        -Inimigo alvo
        -boolean ativo
        -Color cor
        +update()
        +draw(Graphics2D g2)
        +desativar()
        +isAtivo() boolean
    }

    %% Hierarquia de Torres
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
        +Torre(int col, int row, int tamanho, List~Inimigo~ inimigos, List~Projetil~ projeteis)
        #encontrarAlvo()
        +update()
        +draw(Graphics2D g2)*
        #atirar()*
        +getCusto() int
    }

    class TorreCanhao {
        +static final int CUSTO
        +TorreCanhao(int col, int row, int tamanho, List~Inimigo~ inimigos, List~Projetil~ projeteis)
        +draw(Graphics2D g2)
        #atirar()
    }

    class TorreLaser {
        +static final int CUSTO
        +TorreLaser(int col, int row, int tamanho, List~Inimigo~ inimigos, List~Projetil~ projeteis)
        +draw(Graphics2D g2)
        #atirar()
    }

    class Main {
        +main(String[] args)
    }

    %% Relações entre classes
    GamePanel --> TileManager
    GamePanel --> Inimigo
    GamePanel --> Torre
    GamePanel --> Projetil
    
    Torre <|-- TorreCanhao
    Torre <|-- TorreLaser
    
    Torre --> Inimigo
    Torre --> Projetil
    
    Projetil --> Inimigo
    
    Inimigo --> Point
    TileManager --> Point
