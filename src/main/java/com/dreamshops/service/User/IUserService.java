package com.dreamshops.service.User;

import com.dreamshops.Request.CreateUserRequest;
import com.dreamshops.Request.UpdateUserRequest;
import com.dreamshops.dto.UserDto;
import com.dreamshops.model.User;

import java.util.List;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UpdateUserRequest request, Long userId);
    void deleteUser(Long userId);
    List<User> getAllUsers();

    UserDto convertUserTDto(User user);

    User getAuthenticatedUser();
}
