package game;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {

    private List<Wave> waves;
    private int ondaAtualIndex = 0; // Começa na 0 (que é a Wave 1)
    private int inimigoAtualIndex = 0; // Qual inimigo da lista estamos spawnando

    private long ultimoSpawnTime = 0;
    private long spawnCooldown = 1_000_000_000L; // 1 segundo padrão (pode variar)

    public WaveManager() {
        waves = new ArrayList<>();
        criarOndas();
    }

    private void criarOndas() {
        Wave w;

        // --- ONDA 1: Introdutória ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_BASICO, 10);
        waves.add(w);

        // --- ONDA 2: Introduz Rápidos ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_BASICO, 8);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 5);
        waves.add(w);

        // --- ONDA 3: Introduz Tank ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_BASICO, 5);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 5);
        w.adicionarInimigo(Wave.TIPO_TANK, 3);
        waves.add(w);

        // --- ONDA 4: Enxame Rápido ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 20);
        waves.add(w);

        // --- ONDA 5: Misto Equilibrado ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_BASICO, 10);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 10);
        w.adicionarInimigo(Wave.TIPO_TANK, 5);
        waves.add(w);

        // --- ONDA 6: Muralha de Tanks ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_TANK, 10);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 5);
        waves.add(w);

        // --- ONDA 7: Horda Grande ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_TANK, 25);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 5);
        waves.add(w);

        // --- ONDA 8: Elite ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_TANK, 30);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 15);
        waves.add(w);

        // --- ONDA 9: O Caos ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_TANK, 15);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 20);
        w.adicionarInimigo(Wave.TIPO_BASICO, 10);
        waves.add(w);

        // --- ONDA 10: FINAL BOSS (Conceitual - muitos inimigos fortes) ---
        w = new Wave();
        w.adicionarInimigo(Wave.TIPO_TANK, 20);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 15);
        w.adicionarInimigo(Wave.TIPO_TANK, 20);
        w.adicionarInimigo(Wave.TIPO_RAPIDO, 15);
        waves.add(w);
    }

    // Retorna o ID do próximo inimigo ou -1 se a onda acabou de spawnar tudo
    public int getProximoInimigoParaSpawnar() {
        long agora = System.nanoTime();

        // Verifica o cooldown do spawn
        if (agora - ultimoSpawnTime < spawnCooldown) {
            return -1;
        }

        if (ondaAtualIndex >= waves.size()) return -1; // Jogo acabou

        Wave ondaAtual = waves.get(ondaAtualIndex);
        List<Integer> lista = ondaAtual.getListaInimigos();

        if (inimigoAtualIndex < lista.size()) {
            int tipo = lista.get(inimigoAtualIndex);
            inimigoAtualIndex++;
            ultimoSpawnTime = agora;

            // Ajusta cooldown dinamicamente se quiser (ex: rápidos spawnam mais rápido)
            if (tipo == Wave.TIPO_RAPIDO) spawnCooldown = 500_000_000L; // 0.5s
            else spawnCooldown = 1_000_000_000L; // 1.0s

            return tipo;
        }

        return -1; // Não há mais inimigos nesta onda
    }

    public boolean isOndaCompletaDeSpawnar() {
        if (ondaAtualIndex >= waves.size()) return true;
        return inimigoAtualIndex >= waves.get(ondaAtualIndex).getListaInimigos().size();
    }

    public void proximaOnda() {
        if (ondaAtualIndex < waves.size()) {
            ondaAtualIndex++;
            inimigoAtualIndex = 0;
        }
    }

    public int getOndaAtualNumero() {
        return ondaAtualIndex + 1;
    }

    public int getTotalOndas() {
        return waves.size();
    }

    public boolean isUltimaOndaFinalizada() {
        return ondaAtualIndex >= waves.size();
    }
}