package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpMethod;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.UserResponse;
import stg.onyou.repository.UserRepository;
import stg.onyou.model.entity.User;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Header<UserResponse> selectUser(Long id){

        return userRepository.findById(id)
                .map(user -> Header.OK(selectUserResponse(user)))
                .orElseThrow(
                        ()-> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
    }

    private UserResponse selectUserResponse(User user){

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .organizationName(user.getOrganization().getName())
                .birthday(user.getBirthday())
                .sex(user.getSex())
                .email(user.getEmail())
                .created(user.getCreated())
                .build();

        return userResponse;

    }

}