package py.com.progweb.parcial.rest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.com.progweb.parcial.ejb.ClienteDAO;
import py.com.progweb.parcial.ejb.ProductoDAO;
import py.com.progweb.parcial.ejb.VentaDAO;
import py.com.progweb.parcial.input.DetalleVentaInput;
import py.com.progweb.parcial.input.VentaInput;
import py.com.progweb.parcial.model.Cliente;
import py.com.progweb.parcial.model.DetalleVenta;
import py.com.progweb.parcial.model.Producto;
import py.com.progweb.parcial.model.Venta;
import py.com.progweb.parcial.dto.VentaDTO;
import py.com.progweb.parcial.dto.DetalleVentaDTO;

@Path("/ventas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VentaREST {

    @EJB
    private VentaDAO ventaDAO;

    @EJB
    private ClienteDAO clienteDAO;

    @EJB
    private ProductoDAO productoDAO;

    @PersistenceContext
    private EntityManager em;

    @GET
    public Response listar(
            @QueryParam("fechaDesde") String fechaDesdeStr,
            @QueryParam("fechaHasta") String fechaHastaStr,
            @QueryParam("idCliente") Integer idCliente) {

        try {
            LocalDateTime fechaDesde = fechaDesdeStr != null ? LocalDateTime.parse(fechaDesdeStr) : null;
            LocalDateTime fechaHasta = fechaHastaStr != null ? LocalDateTime.parse(fechaHastaStr) : null;

            List<Venta> ventas = ventaDAO.listarFiltrado(fechaDesde, fechaHasta, idCliente);

            List<VentaDTO> ventasDTO = ventas.stream()
                    .map(v -> new VentaDTO(
                            v.getFecha(),
                            v.getIdVenta(),
                            v.getTotal(),
                            v.getCliente().getNombre() + " " + v.getCliente().getApellido()))
                    .collect(Collectors.toList());

            return Response.ok(ventasDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al obtener ventas: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") Integer id) {
        Venta venta = ventaDAO.obtenerPorId(id);
        if (venta == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Venta no encontrada con id: " + id)
                    .build();
        }
        return Response.ok(venta).build();
    }

    @GET
    @Path("/cliente/{idCliente}")
    public Response listarPorCliente(@PathParam("idCliente") Integer idCliente) {
        List<Venta> ventas = ventaDAO.listarPorCliente(idCliente);
        return Response.ok(ventas).build();
    }

    @POST
    @Transactional
    public Response crearVenta(VentaInput ventaInput) {
        try {
            // Validar cliente
            Cliente cliente = clienteDAO.obtenerPorId(ventaInput.getIdCliente());
            if (cliente == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("El cliente especificado no existe")
                        .build();
            }

            // Validar detalles
            if (ventaInput.getDetalles() == null || ventaInput.getDetalles().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("La venta debe tener al menos un producto")
                        .build();
            }

            // Crear venta
            Venta venta = new Venta();
            venta.setCliente(cliente);
            venta.setFecha(LocalDateTime.now());
            venta.setTotal(BigDecimal.ZERO); // Será actualizado después

            // Registrar venta primero (sin detalles)
            ventaDAO.crear(venta);

            BigDecimal total = BigDecimal.ZERO;
            for (DetalleVentaInput detalleInput : ventaInput.getDetalles()) {
                // Validar producto
                Producto producto = productoDAO.obtenerPorId(detalleInput.getIdProducto());
                if (producto == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("El producto con ID " + detalleInput.getIdProducto() + " no existe")
                            .build();
                }

                // Crear detalle
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta);
                detalle.setProducto(producto);
                detalle.setCantidad(detalleInput.getCantidad());
                detalle.setPrecio(producto.getPrecioVenta());

                // Calcular subtotal y acumularlo al total
                BigDecimal subtotal = producto.getPrecioVenta().multiply(new BigDecimal(detalleInput.getCantidad()));
                total = total.add(subtotal);

                // Guardar el detalle
                em.persist(detalle);

                // Actualizar inventario
                producto.setCantidadExistente(producto.getCantidadExistente() - detalleInput.getCantidad());
                em.merge(producto);
            }

            // Actualizar el total de la venta
            venta.setTotal(total);
            em.merge(venta);

            return Response.status(Response.Status.CREATED)
                    .entity("Venta registrada exitosamente con ID: " + venta.getIdVenta())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al registrar la venta: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Venta venta) {
        try {
            venta.setIdVenta(id);
            ventaDAO.actualizar(venta);
            return Response.ok("Venta actualizada exitosamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al actualizar venta: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            ventaDAO.eliminar(id);
            return Response.ok("Venta eliminada exitosamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al eliminar venta: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/detalle")
    public Response obtenerDetalles(@QueryParam("idVenta") Integer idVenta) {
        try {
            if (idVenta == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Es necesario proporcionar el parámetro idVenta")
                        .build();
            }

            // Verificar que la venta existe
            Venta venta = ventaDAO.obtenerPorId(idVenta);
            if (venta == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Venta no encontrada con id: " + idVenta)
                        .build();
            }

            // Consultar los detalles asociados a esta venta
            Query q = em.createQuery(
                    "SELECT d FROM DetalleVenta d WHERE d.venta.idVenta = :idVenta");
            q.setParameter("idVenta", idVenta);
            List<DetalleVenta> detalles = q.getResultList();

            // Convertir a DTOs
            List<DetalleVentaDTO> detallesDTO = detalles.stream()
                    .map(d -> new DetalleVentaDTO(
                            d.getProducto().getIdProducto(),
                            d.getProducto().getNombre(),
                            d.getProducto().getCategoria().getIdCategoria(),
                            d.getProducto().getCategoria().getNombre(),
                            d.getCantidad(),
                            d.getPrecio(),
                            d.getPrecio().multiply(new BigDecimal(d.getCantidad()))))
                    .collect(Collectors.toList());

            return Response.ok(detallesDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al obtener detalles de venta: " + e.getMessage())
                    .build();
        }
    }
}
