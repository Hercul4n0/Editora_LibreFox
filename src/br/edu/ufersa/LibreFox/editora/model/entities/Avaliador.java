package br.edu.ufersa.LibreFox.editora.model.entities;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Avaliador extends Usuario{
    private List<Obra> ObrasparaAvaliar = new ArrayList<>();
    
    
    //Construtor

    public Avaliador (String nome, String cpf, String endereco){
        super(nome, cpf, endereco);
    }

    //Métodos

    private void AddParaObrasParaAvaliar (Obra obra){
        if(!ObrasparaAvaliar.contains(obra)) {
            ObrasparaAvaliar.add(obra);}

    }

    private void ExcluirObrasParaAvaliar (Obra obra){
        ObrasparaAvaliar.remove(obra);
    }

    public void definirAvaliador(Obra obra, Avaliador avaliador, Editora editora, Gerente gerente){
        if (editora.getGerenteCpf().equals(gerente.getCpf())) {
            obra.setAvaliador(avaliador);
            avaliador.AddParaObrasParaAvaliar(obra);
        }
    }

    public List<Obra> getObrasParaAvaliar(){
        return Collections.unmodifiableList(ObrasparaAvaliar);
    }

}
