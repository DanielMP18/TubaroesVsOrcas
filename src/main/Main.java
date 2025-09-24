package main;
import main.java.GamePanel;

import javax.swing.JFrame;
public class Main {
    public static void main(String[] args) {
        //manipulacao de tela
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Tubarões Vs Orcas");

        //manipulacao do tamanho da tela
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        //loop de frames
        gamePanel.startGameThread();


    }
}