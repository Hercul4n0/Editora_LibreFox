package br.edu.ufersa.LibreFox.util;

import br.edu.ufersa.LibreFox.Model.entities.Perfil;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Diálogo para o usuário escolher, entre os perfis que sua conta possui,
 * com qual deseja entrar/trocar (usado no login e na troca de perfil dentro
 * dos dashboards).
 */
public final class SeletorPerfil {

    private SeletorPerfil() {}

    /** Mostra o diálogo e devolve o perfil escolhido, ou {@code null} se cancelado. */
    public static Perfil escolher(Set<Perfil> perfisDisponiveis) {
        List<Perfil> ordem = new ArrayList<>(perfisDisponiveis);

        Dialog<Perfil> dialog = new Dialog<>();
        dialog.setTitle("Escolher perfil");
        dialog.setHeaderText("Esta conta tem mais de um perfil. Como deseja entrar?");

        List<ButtonType> tipos = new ArrayList<>();
        for (Perfil perfil : ordem) {
            tipos.add(new ButtonType(rotulo(perfil), ButtonBar.ButtonData.OK_DONE));
        }
        dialog.getDialogPane().getButtonTypes().addAll(tipos);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            int idx = tipos.indexOf(button);
            return idx >= 0 ? ordem.get(idx) : null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static String rotulo(Perfil perfil) {
        return switch (perfil) {
            case AUTOR -> "Autor";
            case AVALIADOR -> "Avaliador";
            case GERENTE -> "Gerente";
        };
    }
}
