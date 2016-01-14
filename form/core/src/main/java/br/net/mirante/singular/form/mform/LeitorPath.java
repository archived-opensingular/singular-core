package br.net.mirante.singular.form.mform;

class LeitorPath {

    private final String path;
    private final String trecho;
    private final int fim;
    private final boolean indiceLista;

    public LeitorPath(String path) {
        this(path, 0);
    }

    private LeitorPath(String path, int inicio) {
        if (path == null){
            throw new SingularFormException("O path do campo não pode ser nulo.");
        }
        this.path = path;
        if (inicio >= path.length()) {
            fim = inicio;
            trecho = null;
            indiceLista = false;
        } else {
            indiceLista = (path.charAt(inicio) == '[');
            if (indiceLista) {
                fim = path.indexOf(']', inicio + 1) + 1;
                if (fim == 0 || inicio + 2 == fim) {
                    throw new RuntimeException("Path '" + path + "' inválido na posição " + inicio);
                }
                for (int i = inicio + 1; i < fim - 1; i++) {
                    if (!Character.isDigit(path.charAt(i))) {
                        throw new RuntimeException("Path '" + path + "' inválido na posição " + i);
                    }
                }
                trecho = path.substring(inicio + 1, fim - 1);
            } else {
                if (path.charAt(inicio) == '.') {
                    if (inicio == 0) {
                        throw new RuntimeException("Path '" + path + "' inválido na posição " + inicio);
                    } else {
                        inicio++;
                    }
                } else if (inicio != 0) {
                    throw new RuntimeException("Path '" + path + "' inválido na posição " + inicio);
                }

                fim = localizarFim(path, inicio);
                if (inicio == fim) {
                    throw new RuntimeException("Path '" + path + "' inválido na posição " + inicio);
                }
                trecho = (inicio == 0 && fim == path.length()) ? path : path.substring(inicio, fim);
                if (!MFormUtil.isNomeSimplesValido(trecho)) {
                    throw new RuntimeException("Path '" + path + "' inválido na posição " + inicio + " : Não é um nome de campo válido");
                }
            }
        }
    }

    private static int localizarFim(String s, int pos) {
        for (; pos < s.length() && s.charAt(pos) != '.' && s.charAt(pos) != '['; pos++)
            ;
        return pos;
    }

    public String getTrecho() {
        if (trecho == null) {
            throw new RuntimeException("Leitura já está no fim");
        }
        return trecho;
    }

    public LeitorPath proximo() {
        if (trecho == null) {
            throw new RuntimeException("Leitura já está no fim");
        }
        return new LeitorPath(path, fim);
    }

    public boolean isEmpty() {
        return trecho == null;
    }

    public boolean isUltimo() {
        if (trecho == null) {
            throw new RuntimeException("Leitura já está no fim");
        }
        return fim == path.length();
    }

    public boolean isNomeSimplesValido() {
        return MFormUtil.isNomeSimplesValido(getTrecho());
    }

    public boolean isIndice() {
        if (trecho == null) {
            throw new RuntimeException("Leitura já está no fim");
        }
        return indiceLista;
    }

    public int getIndice() {
        return Integer.parseInt(trecho);
    }

    String getTextoErro(MInstancia instanciaContexto, String msg) {
        if (path.length() == trecho.length()) {
            return "Na instancia '" + instanciaContexto.getPathFull() + "' do tipo '" + instanciaContexto.getMTipo().getNome()
                    + "' para o path '" + path + "': " + msg;
        }
        return "Ao tentar resolver '" + trecho + "' na instancia '" + instanciaContexto.getPathFull() + "' do tipo '"
                + instanciaContexto.getMTipo().getNome() + "' referent ao path '" + path + "' ocorreu o erro: " + msg;
    }

    public String getTextoErro(MEscopo escopo, String msg) {
        if (path.length() == trecho.length()) {
            return "No tipo '" + escopo.getNome() + "' para o path '" + path + "': " + msg;
        }
        return "No tipo '" + escopo.getNome() + "' para o trecho '" + trecho + "' do path '" + path + "': " + msg;
    }
}