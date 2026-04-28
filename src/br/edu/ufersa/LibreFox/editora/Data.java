package br.edu.ufersa.LibreFox.editora;

public class Data {

    // Armazena o dia da data
    private short dia;

    // Armazena o mês da data
    private short mes;

    // Armazena o ano da data
    private short ano;

    // Construtor da classe, responsável por inicializar a data
    public Data(short dia, short mes, short ano) {
        setDia(dia);
        setMes(mes);
        setAno(ano);
    }

    // Retorna o dia corresponsdente armazenado
    public short getDia() {
        return dia;
    }

    // Altera o dia armazenado se estiver entre 1 e 31
    public void setDia(short dia) {
        if (dia >= 1 && dia <= 31) {
            this.dia = dia;
        } else {
            System.out.println("Dia inválido.");
        }
    }

    // Retorna o mês armazenado
    public short getMes() {
        return mes;
    }

    // Altera o mês armazenado se estiver entre 1 e 12
    public void setMes(short mes) {
        if (mes >= 1 && mes <= 12) {
            this.mes = mes;
        } else {
            System.out.println("Mês inválido.");
        }
    }

    // Retorna o ano armazenado
    public short getAno() {
        return ano;
    }

    // Altera o ano armazenado se for positivo
    public void setAno(short ano) {
        if (ano > 0) {
            this.ano = ano;
        } else {
            System.out.println("Ano inválido.");
        }
    }
}
