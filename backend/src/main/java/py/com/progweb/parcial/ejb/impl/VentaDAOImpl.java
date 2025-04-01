package py.com.progweb.parcial.ejb.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import py.com.progweb.parcial.ejb.ClienteDAO;
import py.com.progweb.parcial.ejb.VentaDAO;
import py.com.progweb.parcial.model.Cliente;
import py.com.progweb.parcial.model.Venta;

@Stateless
public class VentaDAOImpl implements VentaDAO {

    @PersistenceContext(unitName = "EcommerceBD")
    private EntityManager em;

    @Inject
    private ClienteDAO clienteDAO;

    @Override
    public void crear(Venta venta) throws Exception {
        // Validar que el cliente existe
        Cliente cliente = clienteDAO.obtenerPorId(venta.getCliente().getIdCliente());
        if (cliente == null) {
            throw new Exception("El cliente especificado no existe");
        }

        venta.setCliente(cliente);

        if (venta.getFecha() == null) {
            venta.setFecha(LocalDateTime.now());
        }

        venta.setTotal(BigDecimal.ZERO);

        em.persist(venta);
        em.flush();
    }

    @Override
    public Venta obtenerPorId(Integer id) {
        return em.find(Venta.class, id);
    }

    @Override
    public List<Venta> listarTodos() {
        Query q = em.createQuery("SELECT v FROM Venta v ORDER BY v.fecha DESC");
        return (List<Venta>) q.getResultList();
    }

    @Override
    public List<Venta> listarPorCliente(Integer idCliente) {
        Query q = em.createQuery("SELECT v FROM Venta v WHERE v.cliente.idCliente = :idCliente ORDER BY v.fecha DESC");
        q.setParameter("idCliente", idCliente);
        return (List<Venta>) q.getResultList();
    }

    public List<Venta> listarFiltrado(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Integer idCliente) {
        StringBuilder jpql = new StringBuilder("SELECT v FROM Venta v WHERE 1=1");

        if (fechaDesde != null) {
            jpql.append(" AND v.fecha >= :fechaDesde");
        }

        if (fechaHasta != null) {
            jpql.append(" AND v.fecha <= :fechaHasta");
        }

        if (idCliente != null) {
            jpql.append(" AND v.cliente.idCliente = :idCliente");
        }

        jpql.append(" ORDER BY v.fecha DESC");

        Query q = em.createQuery(jpql.toString());

        if (fechaDesde != null) {
            q.setParameter("fechaDesde", fechaDesde);
        }

        if (fechaHasta != null) {
            q.setParameter("fechaHasta", fechaHasta);
        }

        if (idCliente != null) {
            q.setParameter("idCliente", idCliente);
        }

        return (List<Venta>) q.getResultList();
    }

    @Override
    public void actualizar(Venta venta) throws Exception {
        // No permitimos actualizar ventas ya procesadas
        Venta existente = obtenerPorId(venta.getIdVenta());
        if (existente == null) {
            throw new Exception("La venta no existe");
        }

        throw new Exception("Las ventas no pueden ser modificadas una vez registradas");
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        throw new Exception("Las ventas no pueden ser eliminadas");
    }
}
