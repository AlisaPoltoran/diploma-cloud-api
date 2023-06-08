package ru.netology.diplomacloudapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplomacloudapi.dto.AuthenticationRequest;
import ru.netology.diplomacloudapi.dto.AuthenticationResponse;
import ru.netology.diplomacloudapi.service.AuthenticationService;
import ru.netology.diplomacloudapi.service.LogoutService;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LogoutService logoutService;

    /**
     * A method that authenticates user and returns jwt token if successful
     * @param request an object of an Authentication class that consists of a login and password
     * @return an object of an AuthenticationResponse class that consists of a JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
