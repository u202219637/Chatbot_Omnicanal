package pe.edu.upc.shadowchat.dtos.dashboard;

import java.time.LocalDateTime;

public class ComentarioGlobalDTO {
    private Integer calificacion;
    private String motivo;
    private String comentario;
    private LocalDateTime fecha;
    private String clienteNombre;

    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer v) { this.calificacion = v; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String v) { this.motivo = v; }
    public String getComentario() { return comentario; }
    public void setComentario(String v) { this.comentario = v; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime v) { this.fecha = v; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String v) { this.clienteNombre = v; }
}