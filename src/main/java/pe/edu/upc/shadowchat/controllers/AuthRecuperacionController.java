package pe.edu.upc.shadowchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.serviceInterfaces.IAuthRecuperacionService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthRecuperacionController {

    @Autowired private IAuthRecuperacionService authRecuperacionService;

    @PostMapping("/recuperar")
    public ResponseEntity<Map<String, String>> recuperar(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        try {
            authRecuperacionService.solicitarRecuperacion(correo);
        } catch (Exception ignored) {}
        return ResponseEntity.ok(Map.of("mensaje",
                "Si el correo existe en nuestro sistema, recibirás un enlace de recuperación."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String nuevaPassword = body.get("password");
        try {
            authRecuperacionService.resetearPassword(token, nuevaPassword);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}