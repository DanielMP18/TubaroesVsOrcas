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
