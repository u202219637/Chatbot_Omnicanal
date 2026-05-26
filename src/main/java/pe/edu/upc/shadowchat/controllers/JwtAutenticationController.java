package pe.edu.upc.shadowchat.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.shadowchat.securities.JwtRequest;
import pe.edu.upc.shadowchat.securities.JwtResponse;
import pe.edu.upc.shadowchat.securities.JwtTokenUtil;
import pe.edu.upc.shadowchat.serviceImplements.JwtUserDetailsService;

@RestController
@CrossOrigin
public class JwtAutenticationController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private JwtUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest req) throws Exception {
        authenticate(req.getUsername(), req.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        // Extrae el rol principal del token recién generado
        // para devolverlo a Angular junto con el token
        String rol = jwtTokenUtil.getRolFromToken(token);

        return ResponseEntity.ok(new JwtResponse(token, userDetails.getUsername(), rol));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}