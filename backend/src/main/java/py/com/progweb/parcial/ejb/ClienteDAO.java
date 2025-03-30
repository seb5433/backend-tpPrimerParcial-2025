package py.com.progweb.parcial.ejb;

import java.util.List;
import javax.ejb.Local;
import py.com.progweb.parcial.model.Cliente;

@Local
public interface ClienteDAO {
    void crear(Cliente cliente) throws Exception;
    Cliente obtenerPorId(Integer id);
    List<Cliente> listarTodos();
    void actualizar(Cliente cliente) throws Exception;
    void eliminar(Integer id) throws Exception;
    Cliente buscarPorCedula(String cedula);
    List<Cliente> buscarPorApellido(String apellido);
}
