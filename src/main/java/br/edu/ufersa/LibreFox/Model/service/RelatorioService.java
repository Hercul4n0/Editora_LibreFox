package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.RelatorioDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Relatorio;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class RelatorioService {

    private final RelatorioDAO relatorioDAO;

    public RelatorioService(Connection connection) {
        this.relatorioDAO = new RelatorioDAO(connection);
    }

    public Relatorio gerarPorPeriodo(LocalDate dataInicial, LocalDate dataFinal,
                                     Sessao sessao) throws SQLException {
        exigirGerente(sessao);
        validarPeriodo(dataInicial, dataFinal);
        return relatorioDAO.gerarPorPeriodo(dataInicial, dataFinal);
    }

    public Relatorio gerarPorPeriodoEAvaliador(LocalDate dataInicial, LocalDate dataFinal,
                                               Avaliador avaliador, Sessao sessao) throws SQLException {
        exigirGerente(sessao);
        validarPeriodo(dataInicial, dataFinal);
        if (avaliador == null) {
            throw new IllegalArgumentException("Avaliador não pode ser nulo.");
        }
        return relatorioDAO.gerarPorPeriodoEAvaliador(dataInicial, dataFinal, avaliador);
    }

    //Validação

    private void exigirGerente(Sessao sessao) {
        if (sessao == null || !sessao.podeGerenciar()) {
            throw new SecurityException("Apenas o gerente pode gerar relatórios.");
        }
    }

    private void validarPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException("Data inicial e final são obrigatórias.");
        }
        if (dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial.");
        }
    }
}
