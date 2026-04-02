package com.app.ecom_application.services;

import com.app.ecom_application.dtos.AddressDTO;
import com.app.ecom_application.dtos.UserRequest;
import com.app.ecom_application.dtos.UserResponse;
import com.app.ecom_application.models.Address;
import com.app.ecom_application.models.User;
import com.app.ecom_application.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    //private List<User> userList = new ArrayList();
    // private Long nextId = 1l;

    public List<UserResponse> fetchAllUsers(){
        return  userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public void addUser (UserRequest userRequest){
        User user = new User();
        updateUserFromRequest(user,userRequest);
        userRepository.save(user);
    }

    public boolean updateUser(Long id, UserRequest updatedUserRequest) {
        return userRepository.findById(id)
                .map(u -> {
                   updateUserFromRequest(u,updatedUserRequest);
                    userRepository.save(u);
                    return true; // update happened
                })
                .orElse(false); // no user found
    }




    public Optional<UserResponse> fetchUserById(Long id) {
       return userRepository.findById(id)
               .map(this::mapToUserResponse);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if(userRequest.getAddress() != null){
            Address address = new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity());
            address.setState(userRequest.getAddress().getState());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
    }
    private UserResponse mapToUserResponse(User user){
        UserResponse response = new UserResponse();
        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getUserRole());
        if(user.getAddress() != null){
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());

        }
        return response;
    }
}
