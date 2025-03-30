package py.com.progweb.parcial.ejb;

import java.util.List;
import javax.ejb.Local;
import py.com.progweb.parcial.model.Producto;

@Local
public interface ProductoDAO {
    void crear(Producto producto) throws Exception;
    Producto obtenerPorId(Integer id);
    List<Producto> listarTodos();
    void actualizar(Producto producto) throws Exception;
    void eliminar(Integer id) throws Exception;
    List<Producto> listarPorCategoria(Integer idCategoria);
}
