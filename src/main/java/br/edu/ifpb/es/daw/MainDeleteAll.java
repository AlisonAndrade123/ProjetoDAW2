package br.edu.ifpb.es.daw;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class MainDeleteAll {
    public static void main(String[] args) throws DawException {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            try (EntityManager em = emf.createEntityManager()) {
                EntityTransaction transaction = em.getTransaction();
                transaction.begin();

                System.out.println("--- Limpando o banco de dados ---");


                System.out.println("Deletando ligações Usuários <-> Papéis...");
                em.createNativeQuery("DELETE FROM usuarios_papeis").executeUpdate();

                System.out.println("Deletando Itens de Pedido...");
                em.createQuery("DELETE FROM ItemPedido").executeUpdate();

                System.out.println("Deletando Pedidos...");
                em.createQuery("DELETE FROM Pedido").executeUpdate();

                System.out.println("Deletando Endereços...");
                em.createQuery("DELETE FROM Endereco").executeUpdate();

                System.out.println("Deletando Produtos...");
                em.createQuery("DELETE FROM Produto").executeUpdate();

                System.out.println("Deletando Categorias...");
                em.createQuery("DELETE FROM Categoria").executeUpdate();

                System.out.println("Deletando Usuários...");
                em.createQuery("DELETE FROM Usuario").executeUpdate();

                System.out.println("Deletando Papéis...");
                em.createQuery("DELETE FROM Papel").executeUpdate();

                transaction.commit();
                System.out.println("--- Banco de dados limpo com sucesso! ---");
            } catch (Exception e) {
                System.err.println("ERRO ao limpar o banco: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}