package pe.edu.upc.shadowchat.securities;

import java.io.Serializable;

/**
 * Respuesta del POST /login.
 * Devuelve token + username + rol principal para que Angular
 * sepa qué layout renderizar sin hacer un segundo request.
 */
public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;

    private final String jwttoken;
    private final String username;
    private final String rol;   // CLIENTE | ASESOR | ADMINISTRADOR

    public JwtResponse(String jwttoken, String username, String rol) {
        this.jwttoken = jwttoken;
        this.username = username;
        this.rol      = rol;
    }

    public String getJwttoken() { return jwttoken; }
    public String getUsername() { return username; }
    public String getRol()      { return rol; }
}