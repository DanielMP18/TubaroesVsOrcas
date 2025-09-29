package main;

import game.GamePanel;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Tubarões Vs Orcas");

        // Cria o painel do jogo
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        // Ajusta o tamanho da janela para o tamanho preferido do painel
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Inicia o loop do jogo
        gamePanel.startGameThread();
    }
}
