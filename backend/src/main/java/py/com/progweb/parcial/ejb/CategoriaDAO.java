package py.com.progweb.parcial.ejb;

import java.util.List;
import javax.ejb.Local;
import py.com.progweb.parcial.model.Categoria;

@Local
public interface CategoriaDAO {
    void crear(Categoria categoria);
    Categoria obtenerPorId(Integer id);
    List<Categoria> listarTodos();
    void actualizar(Categoria categoria);
    void eliminar(Integer id);
}
