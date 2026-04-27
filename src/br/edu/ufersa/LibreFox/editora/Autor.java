package br.edu.ufersa.LibreFox.editora;

public class Autor {
    private String nome;
    private String endereco;
    private String cpf;

    public Autor(String nome, String endereco, String cpf) {
        this.nome = nome;
        this.endereco = endereco;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void Alterar(String nome, String endereco, String cpf) {
        this.nome = nome;
        this.endereco = endereco;
        this.cpf = cpf;
    }

    public void Excluir() {
        this.nome = null;
        this.endereco = null;
        this.cpf = null;
    }

    public void Buscar() { // Exibe os dados do autor que se buscou
        System.out.println("Nome: " + nome);
        System.out.println("Endereço: " + endereco);
        System.out.println("CPF: " + cpf);
    }

    public void BuscarObra(Obra obra) {
        if (obra != null && obra.getAutor() == this) {
            obra.Buscar();
        } else {
            System.out.println("Você não tem acesso a esta obra.");
        }
    }
}