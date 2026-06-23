package br.edu.ufersa.LibreFox.Model.entities;

public class Endereco {
    private long id;
    private String numero;
    private String bairro;
    private String logradouro;
    private String cidade;
    private String uf;

    // ERRO CORRIGIDO 1: construtor não atribuía os parâmetros aos atributos
    public Endereco(String numero, String bairro, String logradouro, String cidade, String uf) {
        setNumero(numero);
        setBairro(bairro);
        setLogradouro(logradouro);
        setCidade(cidade);
        setUf(uf);
    }

    public long getId() {
        return id;
    }

    // ERRO CORRIGIDO 2: setId lançava exceção para id == 0, mas 0 é válido como ID gerado pelo banco
    public void setId(long id) {
        if (id >= 0) {
            this.id = id;
        } else {
            throw new RuntimeException("Id não deve ser negativo");
        }
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        if (numero != null && !numero.isBlank()) {
            this.numero = numero;
        } else {
            throw new RuntimeException("O campo número não pode ser vazio");
        }
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        if (bairro != null && !bairro.isBlank()) {
            this.bairro = bairro;
        } else {
            throw new RuntimeException("O campo bairro não pode ser vazio");
        }
    }

    public String getLogradouro() {
        return logradouro;
    }

    // ERRO CORRIGIDO 3: atributo era "Logradouro" (maiúsculo) — renomeado para "logradouro"
    public void setLogradouro(String logradouro) {
        if (logradouro != null && !logradouro.isBlank()) {
            this.logradouro = logradouro;
        } else {
            throw new RuntimeException("O campo logradouro não pode ser vazio");
        }
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        if (cidade != null && !cidade.isBlank()) {
            this.cidade = cidade;
        } else {
            throw new RuntimeException("O campo cidade não pode ser vazio");
        }
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        if (uf != null && !uf.isBlank()) {
            this.uf = uf;
        } else {
            throw new RuntimeException("O campo UF não pode ser vazio");
        }
    }
}
