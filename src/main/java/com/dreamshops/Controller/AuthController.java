package com.dreamshops.Controller;

import com.dreamshops.Request.ForgotPasswordRequest;
import com.dreamshops.Request.LoginRequest;
import com.dreamshops.Request.ResetPasswordRequest;
import com.dreamshops.Response.ApiResponse;
import com.dreamshops.Response.JwtResponse;
import com.dreamshops.dto.TokenRefreshRequest;
import com.dreamshops.model.RefreshToken;
import com.dreamshops.security.jwt.JwtUtils;
import com.dreamshops.security.user.ShopUserDetails;
import com.dreamshops.service.User.PasswordResetService;
import com.dreamshops.service.User.RefreshTokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    @SecurityRequirements()
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt=jwtUtils.generateTokenForUser(authentication);
            ShopUserDetails userDetails=(ShopUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse=new JwtResponse(userDetails.getId(),jwt,refreshTokenService.createRefreshToken(userDetails.getId()).getToken());
            return ResponseEntity.ok(new ApiResponse("Login Success", jwtResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Invalid Email or Password", e.getMessage()));
        }

    }
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    return ResponseEntity.ok(new ApiResponse("Token refreshed successfully", new JwtResponse(user.getId(), token, requestRefreshToken)));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
    // 1. Request a Reset Token
    @PostMapping("/forgot-password")
    @SecurityRequirements() // Public endpoint
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            String token = passwordResetService.createPasswordResetTokenForUser(request.getEmail());
            // In a real app, you would send this token via Email.
            // For now, we return it in the response so you can test it.
            return ResponseEntity.ok(new ApiResponse("Reset Token generated. (In production, check your email)", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    // 2. Use the Token to Change Password
    @PostMapping("/reset-password")
    @SecurityRequirements() // Public endpoint
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse("Password reset successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

}
