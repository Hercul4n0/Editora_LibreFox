package br.edu.ufersa.LibreFox.Model.entities;

import java.time.LocalDate;
import java.util.ArrayList;

public class Relatorio {

    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private Avaliador avaliadoPor;      // null = relatório geral de todos os avaliadores
    private ArrayList<Obra> obras;

    public Relatorio(LocalDate dataInicial, LocalDate dataFinal, Avaliador avaliadoPor) {
        setDataInicial(dataInicial);
        setDataFinal(dataFinal);
        this.avaliadoPor = avaliadoPor; // pode ser null (relatório geral)
        this.obras = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // GETTERS E SETTERS
    // -------------------------------------------------------------------------

    public LocalDate getDataInicial() { return dataInicial; }
    public void setDataInicial(LocalDate dataInicial) {
        if (dataInicial != null) this.dataInicial = dataInicial;
        else throw new IllegalArgumentException("Data inicial não pode ser nula.");
    }

    public LocalDate getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDate dataFinal) {
        if (dataFinal != null) this.dataFinal = dataFinal;
        else throw new IllegalArgumentException("Data final não pode ser nula.");
    }

    public Avaliador getAvaliadoPor() { return avaliadoPor; }
    public void setAvaliadoPor(Avaliador avaliadoPor) {
        this.avaliadoPor = avaliadoPor;
    }

    public ArrayList<Obra> getObras() { return obras; }

    // Derivado da lista — não precisa ser armazenado separadamente
    public int getNumDeObras() { return obras.size(); }
}
