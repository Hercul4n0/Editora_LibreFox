package main.java.br.edu.ufersa.LibreFox.editora.service;

import main.java.br.edu.ufersa.LibreFox.editora.entities.*;

import java.util.ArrayList;


public class RelatorioService {

    //RelatorioService service = new RelatorioService();
    //private short numDeObras;
    public void AdicionarObra(Relatorio relatorio, Obra obra) {
        if (obra != null) {
            relatorio.getObras().add(obra);
            short numObras = relatorio.getNumDeObras();
            numObras++;
        } else {
            System.out.println("Obra inválida.");
        }
    }

    // Exibe as informações do relatório
    public void gerarRelatorio(Relatorio relatorio) {
        System.out.println("===== RELATÓRIO =====");

        System.out.print("Período: ");

        if (relatorio.getDataInicial() != null) {
            System.out.print(relatorio.getDataInicial().getDia() + "/" +
                    relatorio.getDataInicial().getMes() + "/" + relatorio.getDataInicial().getAno());
        }

        System.out.print(" até ");

        if (relatorio.getDataFinal() != null) {
            System.out.println(relatorio.getDataFinal().getDia() + "/" +
                    relatorio.getDataFinal().getMes() + "/" + relatorio.getDataFinal().getAno());
        }

        System.out.println("Número de Obras: " + relatorio.getNumDeObras());

        if (relatorio.getAvaliadoPor() != null) {
            System.out.println("Avaliado por: " + relatorio.getAvaliadoPor().getNome());
        }

        System.out.println("Obras no relatório:");

        // Percorre a lista de obras e exibe seus dados
        for (Obra obra : relatorio.getObras()) {
            System.out.println("-------------------");
        }
    }
}
