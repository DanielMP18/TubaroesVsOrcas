package entity;

public enum Elemento {
    // Define os elementos
    AGUA,
    FOGO,
    ALGA,
    NEUTRO; // Para torres/inimigos que não participam do sistema

    // Constantes para os modificadores de dano
    private static final double MOD_FORTE = 2.0; // Dano dobrado
    private static final double MOD_FRACO = 0.5; // Metade do dano
    private static final double MOD_NORMAL = 1.0; // Dano normal

    /**
     * Calcula o modificador de dano.
     * --- ESTA É A VERSÃO CORRETA E À PROVA DE ERROS ---
     */
    public static double getModificador(Elemento atacante, Elemento defensor) {
        
        // --- ESTA É A VERIFICAÇÃO "IF" QUE PREVINE O TRAVAMENTO ---
        // Se o atacante OU o defensor for nulo, ou for Neutro,
        // o dano é sempre normal (1.0).
        if (atacante == null || defensor == null || atacante == NEUTRO || defensor == NEUTRO) {
            return MOD_NORMAL;
        }
        
        // Se forem do mesmo tipo, dano normal
        if (atacante == defensor) {
            return MOD_NORMAL;
        }

        // Lógica "Forte" (dano extra)
        if (atacante == AGUA && defensor == FOGO) {
            return MOD_FORTE;
        }
        if (atacante == FOGO && defensor == ALGA) {
            return MOD_FORTE;
        }
        if (atacante == ALGA && defensor == AGUA) {
            return MOD_FORTE;
        }

        // Lógica "Fraca" (dano reduzido)
        if (atacante == AGUA && defensor == ALGA) {
            return MOD_FRACO;
        }
        if (atacante == FOGO && defensor == AGUA) {
            return MOD_FRACO;
        }
        if (atacante == ALGA && defensor == FOGO) {
            return MOD_FRACO;
        }

        // Se não for nenhuma das regras, é normal
        return MOD_NORMAL; 
    }
}