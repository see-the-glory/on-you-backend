package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.UserApiRequest;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.repository.UserRepository;
import stg.onyou.model.entity.User;
import java.util.Optional;

@Service
public class UserApiService {

    @Autowired
    private UserRepository userRepository;

    public Header<UserApiResponse> selectUser(int id){

        return userRepository.findById(id)
                .map(user -> response(user))
                .orElseGet(
                        ()->Header.ERROR("No data exist")
                );
    }

    private Header<UserApiResponse> response(User user){

        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .organizationName(user.getOrganization().getName())
                .birthdate(user.getBirthdate())
                .sex(user.getSex())
                .accountEmail(user.getAccountEmail())
                .created(user.getCreated())
                .build();

        return Header.OK(userApiResponse);

    }

}
