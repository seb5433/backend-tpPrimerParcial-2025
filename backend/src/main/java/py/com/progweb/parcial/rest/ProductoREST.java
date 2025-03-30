package py.com.progweb.parcial.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.com.progweb.parcial.ejb.ProductoDAO;
import py.com.progweb.parcial.model.Producto;

@Path("/productos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductoREST {

    @EJB
    private ProductoDAO productoDAO;

    @GET
    public Response listar() {
        List<Producto> productos = productoDAO.listarTodos();
        return Response.ok(productos).build();
    }

    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") Integer id) {
        Producto producto = productoDAO.obtenerPorId(id);
        if (producto == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Producto no encontrado con id: " + id)
                    .build();
        }
        return Response.ok(producto).build();
    }

    @GET
    @Path("/categoria/{idCategoria}")
    public Response listarPorCategoria(@PathParam("idCategoria") Integer idCategoria) {
        List<Producto> productos = productoDAO.listarPorCategoria(idCategoria);
        return Response.ok(productos).build();
    }

    @POST
    public Response crear(Producto producto) {
        try {
            productoDAO.crear(producto);
            return Response.status(Response.Status.CREATED).entity("Producto creado exitosamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al crear producto: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Producto producto) {
        try {
            producto.setIdProducto(id);
            productoDAO.actualizar(producto);
            return Response.ok("Producto actualizado exitosamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al actualizar producto: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            productoDAO.eliminar(id);
            return Response.ok("Producto eliminado exitosamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al eliminar producto: " + e.getMessage())
                    .build();
        }
    }
}
