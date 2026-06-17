package pe.edu.upc.shadowchat.dtos.dashboard;

import java.time.LocalDateTime;
import java.util.List;

public class SatisfaccionAsesorDTO {

    private Double promedioPropio;
    private Long totalCasosCalificados;
    private List<DistribucionItem> distribucion;
    private List<ComentarioItem> comentariosRecientes;

    public Double getPromedioPropio() { return promedioPropio; }
    public void setPromedioPropio(Double v) { this.promedioPropio = v; }

    public Long getTotalCasosCalificados() { return totalCasosCalificados; }
    public void setTotalCasosCalificados(Long v) { this.totalCasosCalificados = v; }

    public List<DistribucionItem> getDistribucion() { return distribucion; }
    public void setDistribucion(List<DistribucionItem> v) { this.distribucion = v; }

    public List<ComentarioItem> getComentariosRecientes() { return comentariosRecientes; }
    public void setComentariosRecientes(List<ComentarioItem> v) { this.comentariosRecientes = v; }

    public static class DistribucionItem {
        private Integer estrellas;
        private Long total;
        public Integer getEstrellas() { return estrellas; }
        public void setEstrellas(Integer v) { this.estrellas = v; }
        public Long getTotal() { return total; }
        public void setTotal(Long v) { this.total = v; }
    }

    public static class ComentarioItem {
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
}