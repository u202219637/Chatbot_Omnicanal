package pe.edu.upc.shadowchat.serviceInterfaces;

public interface IAuthRecuperacionService {
    void solicitarRecuperacion(String correo);
    void resetearPassword(String token, String nuevaPassword);
}