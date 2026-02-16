package com.dreamshops.service.User;

import com.dreamshops.Request.CreateUserRequest;
import com.dreamshops.Request.UpdateUserRequest;
import com.dreamshops.data.RoleRepository;
import com.dreamshops.dto.UserDto;
import com.dreamshops.model.Role;
import com.dreamshops.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User with id " + userId + " not found"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request).filter(user->
                !userRepository.existsByEmail(request.getEmail()))
                .map(req->{
                    User user = new User();
                    user.setEmail(req.getEmail());
                    user.setPassword(passwordEncoder.encode(req.getPassword()));
                    user.setFirstName(req.getFirstName());
                    user.setLastName(req.getLastName());
                    user.setEmail(req.getEmail());
                    Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(()->new RuntimeException("Role not found"));
                    user.setRoles(Set.of(userRole));
                    return userRepository.save(user);
                }).orElseThrow(()->new RuntimeException("Already Exist User"));
    }

    @Override
    public User updateUser(UpdateUserRequest request, Long userId) {
        return userRepository.findById(userId).map(existingUser->{
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(()->new RuntimeException("User not found"));

    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete,
                ()->new RuntimeException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users=userRepository.findAll();
        return userRepository.findAll();
    }
    @Override
    public UserDto convertUserTDto(User user){
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);


    }

}
