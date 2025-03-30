package py.com.progweb.parcial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VentaDTO {
    @JsonIgnore
    private LocalDateTime fecha;
    private Integer idVenta;
    private String fechaVenta;
    private BigDecimal total;
    private String cliente;

    public VentaDTO(LocalDateTime fecha, Integer idVenta, BigDecimal total, String cliente) {
        this.fecha = fecha;
        this.idVenta = idVenta;
        this.total = total;
        this.cliente = cliente;

        if (fecha != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.fechaVenta = fecha.format(formatter);
        }
    }

    @JsonIgnore
    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
        if (fecha != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.fechaVenta = fecha.format(formatter);
        }
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}
