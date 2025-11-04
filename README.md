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
    direction LR

    class Main {
        +main(String[] args)
    }

    class GamePanel {
        -tileManager: TileManager
        -inimigos: List~Inimigo~
        -torres: List~Torre~
        -projeteis: List~Projetil~
        -vidaBase: int
        -dinheiro: int
        -estadoDoJogo: int
        +GamePanel()
        +startGameThread()
        +run()
        +update()
        +paintComponent(Graphics g)
        +construirTorre(int, int)
        +spawnInimigo()
    }

    class TileManager {
        -mapGrid: int[][]
        -caminho: List~Point~
        +TileManager(int, int, int)
        +getCaminho(): List~Point~
        +isTileValidoParaConstrucao(int, int)
        +draw(Graphics2D g2)
    }

    class Inimigo {
        -x: float
        -y: float
        -vida: int
        -velocidade: float
        -ativo: boolean
        -caminho: List~Point~
        +Inimigo(float, float, List~Point~)
        +update()
        +levarDano(int)
        +draw(Graphics2D g2)
        +isAtivo(): boolean
        +chegouNaBase(): boolean
    }

    class Projetil {
        -x: float
        -y: float
        -dano: int
        -alvo: Inimigo
        -ativo: boolean
        +Projetil(float, float, float, int, Inimigo, Color)
        +update()
        +draw(Graphics2D g2)
        +isAtivo(): boolean
    }

    class Torre {
        <<abstract>>
        #x: int
        #y: int
        #custo: int
        #alcance: int
        #alvo: Inimigo
        #inimigos: List~Inimigo~
        #projeteis: List~Projetil~
        +Torre(int, int, int, List~Inimigo~, List~Projetil~)
        +update()
        #encontrarAlvo()
        {abstract} +atirar()
        {abstract} +draw(Graphics2D g2)
    }

    class TorreCanhao {
        +TorreCanhao(int, int, int, List~Inimigo~, List~Projetil~)
        +atirar()
        +draw(Graphics2D g2)
    }

    class TorreLaser {
        +TorreLaser(int, int, int, List~Inimigo~, List~Projetil~)
        +atirar()
        +draw(Graphics2D g2)
    }

    ' --- Relacionamentos ---

    Main ..> GamePanel : cria

    GamePanel "1" o-- "1" TileManager : gerencia
    GamePanel "1" o-- "*" Inimigo : gerencia
    GamePanel "1" o-- "*" Torre : gerencia
    GamePanel "1" o-- "*" Projetil : gerencia

    Torre <|-- TorreCanhao : "é um"
    Torre <|-- TorreLaser : "é um"

    Torre ..> Inimigo : "1" mira "1"
    Projetil ..> Inimigo : "1" persegue "1"
    
    ' Subclasses de Torre criam projéteis
    TorreCanhao ..> Projetil : cria
    TorreLaser ..> Projetil : cria
    
    ' GamePanel usa o caminho do TileManager para criar Inimigos
    GamePanel ..> TileManager : (usa getCaminho())
