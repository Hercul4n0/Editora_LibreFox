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
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    // Retorna o dia armazenado
    public short getDia() {
        return dia;
    }

    // Altera o dia armazenado
    public void setDia(short dia) {
        this.dia = dia;
    }

    // Retorna o mês armazenado
    public short getMes() {
        return mes;
    }

    // Altera o mês armazenado
    public void setMes(short mes) {
        this.mes = mes;
    }

    // Retorna o ano armazenado
    public short getAno() {
        return ano;
    }

    // Altera o ano armazenado
    public void setAno(short ano) {
        this.ano = ano;
    }
}
