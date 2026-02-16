package com.dreamshops.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private Long id;
    private String token;
    private String type = "Bearer";
    private String refreshToken;

    public JwtResponse(Long id, String token, String refreshToken) {
        this.id = id;
        this.token = token;
        this.refreshToken = refreshToken;
    }

}
