package org;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.model.Usuario;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unid-jpa");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Usuario usuario = new Usuario();
        usuario.setNome("Kauan Luiz");
        usuario.setEmail("kauanluiz@gmail.com");
        usuario.setSenha("0314");
        em.persist(usuario);
        em.getTransaction().commit();
        em.close();
    }
}
