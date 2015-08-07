package br.net.mirante.singular.ui.mform;

class LeitorPath {

    private final String path;
    private final String trecho;
    private final int fim;

    public LeitorPath(String path) {
        this(path, 0);
    }

    public LeitorPath(String path, int inicio) {
        this.path = path;
        this.fim = localizarFim(path, inicio);
        trecho = (inicio >= path.length()) ? null : (inicio == 0 && fim == path.length()) ? path : path.substring(inicio, fim);
    }

    private static int localizarFim(String s, int pos) {
        for (; pos < s.length() && s.charAt(pos) != '.'; pos++)
            ;
        return pos;
    }

    public String getTrecho() {
        return trecho;
    }

    public LeitorPath proximo() {
        if (trecho == null) {
            throw new RuntimeException("Leitura já no fim");
        }
        return new LeitorPath(path, fim + 1);
    }

    public boolean isEmpty() {
        return trecho == null;
    }

    public boolean isUltimo() {
        return fim == path.length();
    }

    public boolean isNomeSimplesValido() {
        return MFormUtil.isNomeSimplesValido(trecho);
    }

    public boolean isIndice() {
        if (trecho.length() >= 3 && trecho.charAt(0) == '[' && trecho.charAt(trecho.length() - 1) == ']') {
            for (int i = trecho.length() - 2; i > 0; i--) {
                if (!Character.isDigit(trecho.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int getIndice() {
        return Integer.parseInt(trecho.substring(1, trecho.length() - 1));
    }

    String getTextoErro(MInstancia instanciaContexto, String msg) {
        if (path.length() == trecho.length()) {
            return "Em '" + instanciaContexto.getCaminhoCompleto() + "' do tipo '" + instanciaContexto.getMTipo().getNome()
                    + "' para o path '" + path + "': " + msg;
        }
        return "Em '" + instanciaContexto.getCaminhoCompleto() + "' do tipo '" + instanciaContexto.getMTipo().getNome()
                + "' para o trecho '" + trecho + "' do path '" + path + "': " + msg;
    }

    public String getTextoErro(MEscopo escopo, String msg) {
        if (path.length() == trecho.length()) {
            return "No tipo '" + escopo.getNome() + "' para o path '" + path + "': " + msg;
        }
        return "No tipo '" + escopo.getNome() + "' para o trecho '" + trecho + "' do path '" + path + "': " + msg;
    }
}