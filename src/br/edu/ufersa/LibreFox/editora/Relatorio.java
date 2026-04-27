package br.edu.ufersa.LibreFox.editora;

import java.util.ArrayList;

public class Relatorio {

    // Armazena a data inicial do período do relatório
    private Data dataInicial;

    // Armazena a data final do período do relatório
    private Data dataFinal;

    // Armazena a quantidade de obras encontradas no relatório
    private short numDeObras;

    // Armazena o avaliador responsável pelas avaliações
    private Avaliador avaliadoPor;

    // Lista que armazena as obras incluídas no relatório
    private ArrayList<Obra> obras;

    // Construtor da classe
    public Relatorio(Data dataInicial, Data dataFinal, Avaliador avaliadoPor) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.avaliadoPor = avaliadoPor;
        this.obras = new ArrayList<>();
        this.numDeObras = 0;
    }

    // Retorna a data inicial
    public Data getDataInicial() {
        return dataInicial;
    }

    // Altera a data inicial
    public void setDataInicial(Data dataInicial) {
        this.dataInicial = dataInicial;
    }

    // Retorna a data final
    public Data getDataFinal() {
        return dataFinal;
    }

    // Altera a data final
    public void setDataFinal(Data dataFinal) {
        this.dataFinal = dataFinal;
    }

    // Retorna a quantidade de obras
    public short getNumDeObras() {
        return numDeObras;
    }

    // Retorna o avaliador responsável
    public Avaliador getAvaliadoPor() {
        return avaliadoPor;
    }

    // Altera o avaliador responsável
    public void setAvaliadoPor(Avaliador avaliadoPor) {
        this.avaliadoPor = avaliadoPor;
    }

    // Adiciona uma obra à lista do relatório
    public void AdicionarObra(Obra obra) {
        if (obra != null) {
            obras.add(obra);
            numDeObras++;
        }
    }

    // Exibe as informações do relatório
    public void gerarRelatorio() {
        System.out.println("===== RELATÓRIO =====");

        System.out.print("Período: ");
        dataInicial.ExibirData();
        System.out.print(" até ");
        dataFinal.ExibirData();

        System.out.println("Número de Obras: " + numDeObras);

        if (avaliadoPor != null) {
            System.out.println("Avaliado por: " + avaliadoPor.getNome());
        }

        System.out.println("Obras no relatório:");

        // Percorre a lista de obras e exibe seus dados
        for (Obra obra : obras) {
            obra.Buscar();
            System.out.println("-------------------");
        }
    }
}