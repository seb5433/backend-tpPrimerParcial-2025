package py.com.progweb.parcial.ejb.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import py.com.progweb.parcial.ejb.CategoriaDAO;
import py.com.progweb.parcial.ejb.ProductoDAO;
import py.com.progweb.parcial.model.Categoria;
import py.com.progweb.parcial.model.Producto;

@Stateless
public class ProductoDAOImpl implements ProductoDAO {
    
    @PersistenceContext(unitName = "EcommerceBD")
    private EntityManager em;
    
    @Inject
    private CategoriaDAO categoriaDAO;
    
    @Override
    public void crear(Producto producto) throws Exception {
        // Validar que la categoría existe
        Categoria categoria = categoriaDAO.obtenerPorId(producto.getCategoria().getIdCategoria());
        if (categoria == null) {
            throw new Exception("La categoría especificada no existe");
        }
        
        // Validar que el nombre no sea nulo o vacío
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del producto es obligatorio");
        }
        
        // Validar que el precio sea positivo
        if (producto.getPrecioVenta() == null || producto.getPrecioVenta().doubleValue() <= 0) {
            throw new Exception("El precio de venta debe ser mayor que cero");
        }
        
        // Validar que la cantidad sea positiva
        if (producto.getCantidadExistente() == null || producto.getCantidadExistente() <= 0) {
            throw new Exception("La cantidad existente debe ser mayor que cero");
        }
        
        producto.setCategoria(categoria);
        em.persist(producto);
    }
    
    @Override
    public Producto obtenerPorId(Integer id) {
        return em.find(Producto.class, id);
    }
    
    @Override
    public List<Producto> listarTodos() {
        Query q = em.createQuery("SELECT p FROM Producto p");
        return (List<Producto>) q.getResultList();
    }
    
    @Override
    public List<Producto> listarPorCategoria(Integer idCategoria) {
        Query q = em.createQuery("SELECT p FROM Producto p WHERE p.categoria.idCategoria = :idCat");
        q.setParameter("idCat", idCategoria);
        return (List<Producto>) q.getResultList();
    }
    
    @Override
    public void actualizar(Producto producto) throws Exception {
        Producto existente = obtenerPorId(producto.getIdProducto());
        if (existente == null) {
            throw new Exception("El producto no existe");
        }
        
        // Validar que la categoría existe
        if (producto.getCategoria() != null) {
            Categoria categoria = categoriaDAO.obtenerPorId(producto.getCategoria().getIdCategoria());
            if (categoria == null) {
                throw new Exception("La categoría especificada no existe");
            }
            producto.setCategoria(categoria);
        }
        
        // Validar que el nombre no sea nulo o vacío
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del producto es obligatorio");
        }
        
        // Validar que el precio sea positivo
        if (producto.getPrecioVenta() == null || producto.getPrecioVenta().doubleValue() <= 0) {
            throw new Exception("El precio de venta debe ser mayor que cero");
        }
        
        // Validar que la cantidad sea positiva
        if (producto.getCantidadExistente() == null || producto.getCantidadExistente() <= 0) {
            throw new Exception("La cantidad existente debe ser mayor que cero");
        }
        
        em.merge(producto);
    }
    
    @Override
    public void eliminar(Integer id) throws Exception {
        Producto producto = obtenerPorId(id);
        if (producto == null) {
            throw new Exception("El producto no existe");
        }
        
        // Verificar si el producto está siendo usado en alguna venta
        Query q = em.createQuery("SELECT COUNT(dv) FROM DetalleVenta dv WHERE dv.producto.idProducto = :idProducto");
        q.setParameter("idProducto", id);
        Long count = (Long) q.getSingleResult();
        
        if (count > 0) {
            throw new Exception("No se puede eliminar el producto porque está siendo utilizado en ventas");
        }
        
        em.remove(producto);
    }
}
