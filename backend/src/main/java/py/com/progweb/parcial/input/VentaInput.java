package py.com.progweb.parcial.input;

import java.util.List;

public class VentaInput {
    private Integer idCliente;
    private List<DetalleVentaInput> detalles;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public List<DetalleVentaInput> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaInput> detalles) {
        this.detalles = detalles;
    }
}
