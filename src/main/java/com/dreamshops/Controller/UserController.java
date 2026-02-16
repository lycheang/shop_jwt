package com.dreamshops.Controller;

import com.dreamshops.Response.ApiResponse;
import com.dreamshops.Request.CreateUserRequest;
import com.dreamshops.Request.UpdateUserRequest;
import com.dreamshops.dto.UserDto;
import com.dreamshops.model.User;
import com.dreamshops.service.User.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")

public class UserController {
    private final IUserService userService;

    @RequestMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId){
        try {
            User user = userService.getUserById(userId);
            UserDto userDto=userService.convertUserTDto(user);
            return ResponseEntity.ok(new ApiResponse("Success", userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,e.getMessage()));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request){
        try {
            User user=userService.createUser(request);
            UserDto userDto=userService.convertUserTDto(user);
            return ResponseEntity.ok(new ApiResponse("User Create Success", userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,e.getMessage()));
        }
    }
    @PutMapping("/{userId}/update")
    public ResponseEntity<ApiResponse> updateUser( @RequestBody UpdateUserRequest request,@PathVariable Long userId){
        try {
            User user =userService.updateUser(request,userId);
            UserDto userDto=userService.convertUserTDto(user);
            return ResponseEntity.ok(new ApiResponse("User Update Success", userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,e.getMessage()));
        }
    }
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId){
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse("User Delete Success", null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,e.getMessage()));
        }

    }
    @GetMapping("/getAllUser")
    public ResponseEntity<ApiResponse> getAllUsers(){
        try {
            return ResponseEntity.ok(new ApiResponse("Success", userService.getAllUsers()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,e.getMessage()));
        }
    }
}
