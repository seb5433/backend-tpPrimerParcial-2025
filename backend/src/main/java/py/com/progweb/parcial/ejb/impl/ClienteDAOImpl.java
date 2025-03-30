package py.com.progweb.parcial.ejb.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import py.com.progweb.parcial.ejb.ClienteDAO;
import py.com.progweb.parcial.model.Cliente;

@Stateless
public class ClienteDAOImpl implements ClienteDAO {

    @PersistenceContext(unitName = "EcommerceBD")
    private EntityManager em;

    @Override
    public void crear(Cliente cliente) throws Exception {
        // Validar que la cédula no exista
        Cliente existente = buscarPorCedula(cliente.getCedula());
        if (existente != null) {
            throw new Exception("Ya existe un cliente con la cédula: " + cliente.getCedula());
        }

        // Validar que el nombre no sea nulo o vacío
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del cliente es obligatorio");
        }

        // Validar que el apellido no sea nulo o vacío
        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            throw new Exception("El apellido del cliente es obligatorio");
        }

        // Validar formato de email (básico)
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty() &&
                !cliente.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new Exception("El formato del email es inválido");
        }

        em.persist(cliente);
    }

    @Override
    public Cliente obtenerPorId(Integer id) {
        return em.find(Cliente.class, id);
    }

    @Override
    public List<Cliente> listarTodos() {
        Query q = em.createQuery("SELECT c FROM Cliente c ORDER BY c.apellido, c.nombre");
        return (List<Cliente>) q.getResultList();
    }

    @Override
    public Cliente buscarPorCedula(String cedula) {
        try {
            Query q = em.createQuery("SELECT c FROM Cliente c WHERE c.cedula = :cedula");
            q.setParameter("cedula", cedula);
            return (Cliente) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Cliente> buscarPorApellido(String apellido) {
        Query q = em.createQuery(
                "SELECT c FROM Cliente c WHERE UPPER(c.apellido) LIKE :apellido ORDER BY c.apellido, c.nombre");
        q.setParameter("apellido", "%" + apellido.toUpperCase() + "%");
        return (List<Cliente>) q.getResultList();
    }

    @Override
    public void actualizar(Cliente cliente) throws Exception {
        Cliente existente = obtenerPorId(cliente.getIdCliente());
        if (existente == null) {
            throw new Exception("El cliente no existe");
        }

        // Validar que la cédula no colisione con otro cliente
        if (!existente.getCedula().equals(cliente.getCedula())) {
            Cliente clienteConCedula = buscarPorCedula(cliente.getCedula());
            if (clienteConCedula != null) {
                throw new Exception("Ya existe otro cliente con la cédula: " + cliente.getCedula());
            }
        }

        // Validar que el nombre no sea nulo o vacío
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del cliente es obligatorio");
        }

        // Validar que el apellido no sea nulo o vacío
        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            throw new Exception("El apellido del cliente es obligatorio");
        }

        // Validar formato de email (básico)
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty() &&
                !cliente.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new Exception("El formato del email es inválido");
        }

        em.merge(cliente);
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        Cliente cliente = obtenerPorId(id);
        if (cliente == null) {
            throw new Exception("El cliente no existe");
        }

        // Verificar si el cliente tiene ventas asociadas
        Query q = em.createQuery("SELECT COUNT(v) FROM Venta v WHERE v.cliente.idCliente = :idCliente");
        q.setParameter("idCliente", id);
        Long count = (Long) q.getSingleResult();

        if (count > 0) {
            throw new Exception("No se puede eliminar el cliente porque tiene ventas asociadas");
        }

        em.remove(cliente);
    }
}
