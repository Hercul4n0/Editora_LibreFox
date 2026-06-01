package main.java.br.edu.ufersa.LibreFox.editora.entities;

import java.util.ArrayList;

public class Relatorio {

    // Armazena a data inicial do período do relatório
    private Data dataInicial;

    // Armazena a data final do período do relatório
    private Data dataFinal;

    // Armazena a quantidade de obras encontradas no relatório
    private short numDeObras;

    // Armazena o avaliador responsável pelas por avaliar as obras
    private Avaliador avaliadoPor;

    // Lista que armazena as obras incluídas no relatório
    private ArrayList<Obra> obras;

    // Construtor da classe
    public Relatorio(Data dataInicial, Data dataFinal, Avaliador avaliadoPor) {
        setDataInicial(dataInicial);
        setDataFinal(dataFinal);
        setAvaliadoPor(avaliadoPor);

        this.obras = new ArrayList<>();
        this.numDeObras = 0;
    }

    // Retorna a data inicial
    public Data getDataInicial() {
        return dataInicial;
    }

    // Altera a data inicial se não for nula
    public void setDataInicial(Data dataInicial) {
        if (dataInicial != null) {
            this.dataInicial = dataInicial;
        } else {
            System.out.println("Data inicial inválida.");
        }
    }

    // Retorna a data final
    public Data getDataFinal() {
        return dataFinal;
    }

    // Altera a data final se não for nula
    public void setDataFinal(Data dataFinal) {
        if (dataFinal != null) {
            this.dataFinal = dataFinal;
        } else {
            System.out.println("Data final inválida.");
        }
    }

    // Retorna a quantidade de obras
    public short getNumDeObras() {
        return numDeObras;
    }

    // Retorna o avaliador responsável
    public Avaliador getAvaliadoPor() {
        return avaliadoPor;
    }

    public ArrayList<Obra> getObras(){return this.obras;}
    // Altera o avaliador responsável se não for nulo
    public void setAvaliadoPor(Avaliador avaliadoPor) {
        if (avaliadoPor != null) {
            this.avaliadoPor = avaliadoPor;
        } else {
            System.out.println("Avaliador inválido.");
        }
    }

}