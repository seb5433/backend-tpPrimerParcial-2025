package py.com.progweb.parcial.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.com.progweb.parcial.ejb.CategoriaDAO;
import py.com.progweb.parcial.model.Categoria;

@Path("/categorias")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CategoriaREST {
    
    @EJB
    private CategoriaDAO categoriaDAO;
    
    @GET
    public Response listar() {
        List<Categoria> categorias = categoriaDAO.listarTodos();
        return Response.ok(categorias).build();
    }
    
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") Integer id) {
        Categoria categoria = categoriaDAO.obtenerPorId(id);
        if (categoria == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(categoria).build();
    }
    
    @POST
    public Response crear(Categoria categoria) {
        categoriaDAO.crear(categoria);
        return Response.status(Response.Status.CREATED).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Categoria categoria) {
        Categoria existente = categoriaDAO.obtenerPorId(id);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        categoria.setIdCategoria(id);
        categoriaDAO.actualizar(categoria);
        return Response.ok().build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        Categoria existente = categoriaDAO.obtenerPorId(id);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        categoriaDAO.eliminar(id);
        return Response.ok().build();
    }
}
