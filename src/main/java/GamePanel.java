package main.java;
import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    //configuracoes da tela
    final int tamanhoOriginaldaTela = 16;
    final int escala = 3;
    final int tamanhoDoTitulo = tamanhoOriginaldaTela*escala;
    final int tamanhoMaximoColunas = 24;
    final int tamanhoMaximoLinhas = 16;
    final int larguraDaTela = tamanhoDoTitulo*tamanhoMaximoColunas;
    final int comprimentoDaTela = tamanhoDoTitulo*tamanhoMaximoLinhas;

    Thread gameThread;

    public GamePanel(){
        this.setPreferredSize(new Dimension(larguraDaTela, comprimentoDaTela));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); //melhora a renderizacao


    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while(gameThread != null){
            System.out.print("o gameLoop ta funcionando");
        }
    }
}
