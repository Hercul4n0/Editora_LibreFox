package br.edu.ufersa.LibreFox.Controllers;

import br.edu.ufersa.LibreFox.Model.entities.Sessao;

public interface DashboardController {
    void setSessao(Sessao sessao);
    void carregarDados();
}