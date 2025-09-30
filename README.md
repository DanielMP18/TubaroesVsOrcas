## Tubaroes vs Orcas

🏗️ Estrutura e Arquitetura do Código

A arquitetura do jogo é baseada no padrão de Game Loop, separando a lógica de jogo (update) da renderização (paintComponent).
Essa separação é a melhor prática para jogos, garantindo que a lógica rode em uma taxa consistente de FPS e o desenho possa rodar na maior velocidade possível.

🚀 Como Executar o Projeto

Para rodar o jogo localmente, siga os passos abaixo:

Pré-requisitos

Certifique-se de ter o JDK (Java Development Kit) instalado na sua máquina (versão mínima: (Ex: JDK 17)).

Instalação e Execução

    Clone o Repositório:
    Bash
git clone https://github.com/DanielMP18/TubaroesVsOrcas.git
cd TubaroesVsOrcas

Compile o Código:

    (Se estiver usando uma IDE como IntelliJ/Eclipse, use a função de build.)

    (Se estiver usando a linha de comando, use o compilador Java:)
    Bash

    javac -d bin src/**/*.java


## Diagrama de Classes UML

```mermaid
classDiagram
    class Main {
        +static void main(String[] args)
    }

    class GamePanel {
        -Thread gameThread
        -TileManager tileManager
        -List~Inimigo~ inimigos
        -List~Torre~ torres
        -int vidaBase
        -int estadoDoJogo
        +void startGameThread()
        +void run()
        +void update()
        +void paintComponent(Graphics g)
    }

    class TileManager {
        -int[][] mapGrid
        -List~Point~ caminho
        +List~Point~ getCaminho()
        +void draw(Graphics2D g2)
    }

    class Inimigo {
        -float x
        -float y
        -int vida
        -float velocidade
        -List~Point~ caminho
        -int pontoAlvoIndex
        +void update()
        +void draw(Graphics2D g2)
        +boolean chegouNaBase()
        +void receberDano(int dano)
    }

    class Torre {
        -int x
        -int y
        -int alcance
        -int dano
        -long cadenciaDeTiro
        -Inimigo alvoAtual
        +void update(List~Inimigo~ inimigos)
        -void encontrarAlvo(List~Inimigo~ inimigos)
        -void atirar()
        +void draw(Graphics2D g2)
    }

    Main ..> GamePanel : cria
    GamePanel "1" o-- "1" TileManager : contém
    GamePanel "1" o-- "*" Inimigo : contém
    GamePanel "1" o-- "*" Torre : contém
    GamePanel ..> Inimigo : gerencia
    GamePanel ..> Torre : gerencia
    Torre ..> Inimigo : rastreia (alvo)
