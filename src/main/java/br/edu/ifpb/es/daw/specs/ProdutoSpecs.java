package br.edu.ifpb.es.daw.specs;

import br.edu.ifpb.es.daw.entities.Produto;
import org.springframework.data.jpa.domain.Specification;

public class ProdutoSpecs {
    public static Specification<Produto> nomeLike(String nome) {
        return (root, q, cb) -> nome == null ? null :
                cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Produto> categoriaId(Long categoriaId) {
        return (root, q, cb) -> categoriaId == null ? null :
                cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<Produto> precoEntre(Double min, Double max) {
        return (root, q, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.le(root.get("preco"), max);
            if (max == null) return cb.ge(root.get("preco"), min);
            return cb.between(root.get("preco"), min, max);
        };
    }

    public static Specification<Produto> qtdMin(Integer minQtd) {
        return (root, q, cb) -> minQtd == null ? null :
                cb.ge(root.get("quantidade"), minQtd);
    }
}
