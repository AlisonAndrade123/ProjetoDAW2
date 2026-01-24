package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.dao.impl.ProdutoDAOImpl;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired; // <-- Novo Import
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private EntityManagerFactory emf;

    @GetMapping
    public List<Produto> listar() throws PersistenciaDawException {
        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        return dao.getAll();
    }
}