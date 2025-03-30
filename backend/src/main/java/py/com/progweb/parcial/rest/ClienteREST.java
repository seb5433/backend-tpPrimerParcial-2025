package py.com.progweb.parcial.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.com.progweb.parcial.ejb.ClienteDAO;
import py.com.progweb.parcial.model.Cliente;

@Path("/clientes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClienteREST {
    
    @EJB
    private ClienteDAO clienteDAO;
    
    @GET
    public Response listar() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        return Response.ok(clientes).build();
    }
    
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") Integer id) {
        Cliente cliente = clienteDAO.obtenerPorId(id);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado con id: " + id)
                    .build();
        }
        return Response.ok(cliente).build();
    }
    
    @GET
    @Path("/cedula/{cedula}")
    public Response buscarPorCedula(@PathParam("cedula") String cedula) {
        Cliente cliente = clienteDAO.buscarPorCedula(cedula);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado con cédula: " + cedula)
                    .build();
        }
        return Response.ok(cliente).build();
    }
    
    @GET
    @Path("/apellido/{apellido}")
    public Response buscarPorApellido(@PathParam("apellido") String apellido) {
        List<Cliente> clientes = clienteDAO.buscarPorApellido(apellido);
        return Response.ok(clientes).build();
    }
    
    @POST
    public Response crear(Cliente cliente) {
        try {
            clienteDAO.crear(cliente);
            return Response.status(Response.Status.CREATED)
                    .entity("Cliente creado exitosamente")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al crear cliente: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Cliente cliente) {
        try {
            cliente.setIdCliente(id);
            clienteDAO.actualizar(cliente);
            return Response.ok("Cliente actualizado exitosamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al actualizar cliente: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            clienteDAO.eliminar(id);
            return Response.ok("Cliente eliminado exitosamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al eliminar cliente: " + e.getMessage())
                    .build();
        }
    }
}
