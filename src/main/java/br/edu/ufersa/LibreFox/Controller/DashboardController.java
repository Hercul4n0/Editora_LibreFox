package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.entities.Sessao;

public interface DashboardController {
    void setSessao(Sessao sessao);
    void carregarDados();
}