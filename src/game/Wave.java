package game;

import java.util.ArrayList;
import java.util.List;

public class Wave {
    private List<Integer> listaInimigos;

    // Constantes para facilitar a leitura
    public static final int TIPO_BASICO = 0;
    public static final int TIPO_RAPIDO = 1;
    public static final int TIPO_TANK = 2;

    public Wave() {
        this.listaInimigos = new ArrayList<>();
    }

    public void adicionarInimigo(int tipo, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            listaInimigos.add(tipo);
        }
    }

    public List<Integer> getListaInimigos() {
        return listaInimigos;
    }
}