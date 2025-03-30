package py.com.progweb.parcial.ejb;

import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Local;
import py.com.progweb.parcial.model.Venta;

@Local
public interface VentaDAO {
    void crear(Venta venta) throws Exception;
    Venta obtenerPorId(Integer id);
    List<Venta> listarTodos();
    void actualizar(Venta venta) throws Exception;
    void eliminar(Integer id) throws Exception;
    List<Venta> listarPorCliente(Integer idCliente);
    List<Venta> listarFiltrado(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Integer idCliente);
}
