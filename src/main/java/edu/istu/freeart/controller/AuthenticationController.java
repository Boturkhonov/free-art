package edu.istu.freeart.controller;

import com.google.common.collect.ImmutableSet;
import edu.istu.freeart.entity.Role;
import edu.istu.freeart.entity.User;
import edu.istu.freeart.impl.UserDetailsServiceImpl;
import edu.istu.freeart.model.AuthenticationRequest;
import edu.istu.freeart.repo.RoleRepository;
import edu.istu.freeart.repo.UserRepository;
import edu.istu.freeart.util.JwtUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static edu.istu.freeart.util.CustomConstants.DEFAULT_IMAGE_NAME;
import static edu.istu.freeart.util.CustomConstants.DEFAULT_POINTS_COUNT;

@RestController
@RequestMapping("api/")
public class AuthenticationController {

    @Value("${edu.istu.free-art.default-avatar-name}")
    private String defaultAvatarName;

    @Value("${edu.istu.free-art.default-points}")
    private String defaultPoints;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtUtil jwtTokenUtil;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(AuthenticationManager authenticationManager,
            UserDetailsServiceImpl userDetailsService,
            JwtUtil jwtTokenUtil,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthenticationRequest authenticationRequest) {
        authenticationRequest.setLogin(authenticationRequest.getLogin().toLowerCase());
        Optional<User> userOptional = userRepository.findByLogin(authenticationRequest.getLogin());
        if (!userOptional.isPresent()) {
            final User user = new User();
            user.setAvatarUrl(defaultAvatarName);
            user.setAbout("");
            user.setLogin(authenticationRequest.getLogin());
            user.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
            user.setPoints(DEFAULT_POINTS_COUNT);
            Role role = roleRepository.findByName("USER").orElseThrow(RuntimeException::new);
            user.setRoles(ImmutableSet.of(role));
            userRepository.save(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return login(authenticationRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getLogin(),
                    authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getLogin());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        JSONObject response = new JSONObject();
        response.put("token", jwt);
        User user = userRepository.findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));
        response.put("id", user.getId());
        response.put("role", user.getRoles().size() > 1 ? "ADMIN" : "USER");
        return ResponseEntity.ok(response.toMap());
    }

}
