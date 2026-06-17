package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.TokenRecuperacion;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.repositories.TokenRecuperacionRepository;
import pe.edu.upc.shadowchat.repositories.UsuarioRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IAuthRecuperacionService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthRecuperacionServiceImplement implements IAuthRecuperacionService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TokenRecuperacionRepository tokenRepository;
    @Autowired private JavaMailSender mailSender;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void solicitarRecuperacion(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        // No revelamos si el correo existe o no, por seguridad
        if (usuario == null) return;

        String token = UUID.randomUUID().toString();

        TokenRecuperacion tr = new TokenRecuperacion();
        tr.setToken(token);
        tr.setUsuario(usuario);
        tr.setFechaExpiracion(LocalDateTime.now().plusHours(1));
        tr.setUsado(false);
        tokenRepository.save(tr);

        String link = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(usuario.getCorreo());
        mensaje.setSubject("Recupera tu contraseña - ShadowByte");
        mensaje.setText(
                "Hola " + usuario.getNombres() + ",\n\n" +
                        "Recibimos una solicitud para recuperar tu contraseña en ShadowByte.\n\n" +
                        "Haz click en el siguiente enlace para crear una nueva contraseña:\n" +
                        link + "\n\n" +
                        "Este enlace expira en 1 hora. Si no solicitaste esto, ignora este correo.\n\n" +
                        "— Equipo ShadowByte"
        );
        mailSender.send(mensaje);
    }

    @Override
    public void resetearPassword(String token, String nuevaPassword) {
        TokenRecuperacion tr = tokenRepository.findByTokenAndUsadoFalse(token)
                .orElseThrow(() -> new RuntimeException("Enlace inválido o ya utilizado"));

        if (tr.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace ha expirado. Solicita uno nuevo.");
        }

        Usuario usuario = tr.getUsuario();
        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        tr.setUsado(true);
        tokenRepository.save(tr);
    }
}