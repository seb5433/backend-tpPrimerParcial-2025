package py.com.progweb.parcial.utils;

import java.util.Properties;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;
import java.math.BigDecimal;

import py.com.progweb.parcial.model.Cliente;
import py.com.progweb.parcial.model.DetalleVenta;
import py.com.progweb.parcial.model.Venta;

public class EmailService {

    //TODO: Poner esto en un archivo de configuración
    private static final String FROM_EMAIL = "abcdef@gmail.com";
    private static final String FROM_PASSWORD = "asdfasdfasdfasdf";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public static void enviarDetalleVenta(Venta venta, List<DetalleVenta> detalles) throws MessagingException {
        Cliente cliente = venta.getCliente();
        
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new MessagingException("El cliente no posee un email registrado");
        }
        
        // Configurar propiedades
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        // Crear sesión
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });
        
        // Crear mensaje
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(cliente.getEmail()));
        message.setSubject("Detalle de su compra - Ecommerce");
        
        // Crear contenido HTML
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><body>");
        htmlContent.append("<h2>Detalle de su compra</h2>");
        htmlContent.append("<p><strong>Cliente:</strong> ").append(cliente.getNombre()).append(" ").append(cliente.getApellido()).append("</p>");
        htmlContent.append("<p><strong>Fecha:</strong> ").append(venta.getFecha()).append("</p>");
        htmlContent.append("<p><strong>Número de venta:</strong> ").append(venta.getIdVenta()).append("</p>");
        
        // Tabla de productos
        htmlContent.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        htmlContent.append("<tr style='background-color: #f2f2f2;'>");
        htmlContent.append("<th>Producto</th><th>Precio Unitario</th><th>Cantidad</th><th>Subtotal</th>");
        htmlContent.append("</tr>");
        
        for (DetalleVenta detalle : detalles) {
            htmlContent.append("<tr>");
            htmlContent.append("<td>").append(detalle.getProducto().getNombre()).append("</td>");
            htmlContent.append("<td>").append(detalle.getPrecio()).append("</td>");
            htmlContent.append("<td>").append(detalle.getCantidad()).append("</td>");
            
            // Calcular subtotal
            BigDecimal subtotal = detalle.getPrecio().multiply(new BigDecimal(detalle.getCantidad()));
            htmlContent.append("<td>").append(subtotal).append("</td>");
            htmlContent.append("</tr>");
        }
        
        htmlContent.append("</table>");
        htmlContent.append("<h3>Total: ").append(venta.getTotal()).append("</h3>");
        htmlContent.append("<p>Gracias por su compra!</p>");
        htmlContent.append("</body></html>");
        
        message.setContent(htmlContent.toString(), "text/html; charset=utf-8");
        
        // Enviar el mensaje
        Transport.send(message);
    }
}
