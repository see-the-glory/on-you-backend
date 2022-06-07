package stg.onyou.config.jwt;

public interface JwtProperties {
    String SECRET = "stgOnyou";  // secret값
    Long EXPIRATION_TIME = 1000000000000000000L; // 무한
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}