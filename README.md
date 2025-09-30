## Tubaroes vs Orcas

🏗️ Estrutura e Arquitetura do Código

A arquitetura do jogo é baseada no padrão de Game Loop, separando a lógica de jogo (update) da renderização (paintComponent). O jogo é desenvolvido em Java, utilizando a biblioteca Swing/AWT para gráficos.

## 🚀 Como Executar o Projeto

Para rodar o jogo localmente, siga os passos abaixo no seu terminal:

### Pré-requisitos

Certifique-se de ter o **JDK (Java Development Kit)** instalado na sua máquina (versão mínima recomendada: **JDK 17**).

### 1. Clonar o Repositório

Abra o seu terminal (Bash/CMD/PowerShell) e execute os seguintes comandos para baixar o código e entrar no diretório:


git clone https://github.com/DanielMP18/TubaroesVsOrcas.git
cd TubaroesVsOrcas

2. Compilação do Código

Use o compilador javac para criar a pasta bin e colocar todos os arquivos de bytecode (.class) nela:
Bash

# Compila todos os arquivos .java dentro de src/ e subpastas
javac -d bin src/**/*.java

3. Execução do Jogo

Finalmente, use o comando java para iniciar o Game Loop, informando que a classe principal (main.Main) está disponível no classpath (-cp) da pasta bin:
Bash

# Inicia o Game Loop
java -cp bin main.Main

```bash
---

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
