package pe.edu.upeu.PharmaBackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.PharmaBackend.entity.Usuario;
import pe.edu.upeu.PharmaBackend.repository.UsuarioRepository;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;
    private final long ttl;
    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, JwtEncoder jwtEncoder,
                          @Value("${app.jwt.ttl-seconds:3600}") long ttl) {
        this.usuarios=usuarios; this.encoder=encoder; this.jwtEncoder=jwtEncoder; this.ttl=ttl;
    }
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginResponse(String token, String tipo, long expiraEnSegundos, String username,
                                String nombreCompleto, List<String> roles, List<String> permisos) {}
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        Usuario user = usuarios.findByUsername(req.username())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
        if (!user.isActivo() || !encoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }
        Instant now=Instant.now();
        JwtClaimsSet claims=JwtClaimsSet.builder().issuer("pharmasoft").subject(user.getUsername())
            .issuedAt(now).expiresAt(now.plusSeconds(ttl)).claim("roles", List.of(user.getRol())).build();
        String token=jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
        return new LoginResponse(token,"Bearer",ttl,user.getUsername(),user.getNombreCompleto(),
            List.of(user.getRol()),List.of());
    }
}
