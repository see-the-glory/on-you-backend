package stg.onyou.config.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.User;
import stg.onyou.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;

@Service
public class JwtTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String REDIS_PREFIX = "jwt:";
    private final UserRepository userRepository;

    public JwtTokenService(JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    public boolean isValidToken(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String userEmail = jwtTokenProvider.getUserPk(token);
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            return token.equals(user.getJwt());
        }
        return false;
    }
}
