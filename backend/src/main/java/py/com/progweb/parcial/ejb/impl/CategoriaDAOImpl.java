package py.com.progweb.parcial.ejb.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import py.com.progweb.parcial.ejb.CategoriaDAO;
import py.com.progweb.parcial.model.Categoria;

@Stateless
public class CategoriaDAOImpl implements CategoriaDAO {

    @PersistenceContext(unitName = "EcommerceBD")
    private EntityManager em;

    @Override
    public void crear(Categoria categoria) {
        em.persist(categoria);
    }

    @Override
    public Categoria obtenerPorId(Integer id) {
        return em.find(Categoria.class, id);
    }

    @Override
    public List<Categoria> listarTodos() {
        Query q = em.createQuery("select c from Categoria c");
        return (List<Categoria>) q.getResultList();
    }

    @Override
    public void actualizar(Categoria categoria) {
        em.merge(categoria);
    }

    @Override
    public void eliminar(Integer id) {
        Categoria categoria = obtenerPorId(id);
        if (categoria != null) {
            em.remove(categoria);
        }
    }
}