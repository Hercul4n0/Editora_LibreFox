package main.java.br.edu.ufersa.LibreFox.editora.service;

import main.java.br.edu.ufersa.LibreFox.editora.entities.Autor;
import main.java.br.edu.ufersa.LibreFox.editora.entities.Obra;

import java.util.List;

public class AutorService {
    public void enviarObra (Autor autor, Obra obra){
        autor.getObrasEnviadas().add(obra);
    }

}
