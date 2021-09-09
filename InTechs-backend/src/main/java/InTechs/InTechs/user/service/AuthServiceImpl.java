package InTechs.InTechs.user.service;

import InTechs.InTechs.exception.exceptions.InvalidTokenException;
import InTechs.InTechs.exception.exceptions.UserAlreadyException;
import InTechs.InTechs.exception.exceptions.UserNotFoundException;
import InTechs.InTechs.user.entity.RefreshToken;
import InTechs.InTechs.user.entity.User;
import InTechs.InTechs.user.payload.request.SignInRequest;
import InTechs.InTechs.user.payload.request.SignUpRequest;
import InTechs.InTechs.user.payload.response.TokenResponse;
import InTechs.InTechs.user.repository.RefreshTokenRepository;
import InTechs.InTechs.user.repository.UserRepository;
import InTechs.InTechs.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.jwt.exp.refresh}")
    private final Long refreshTokenTime;

    @Override
    public void SignUp(SignUpRequest signUpRequest) {
        userRepository.findByEmail(signUpRequest.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyException();
                });

        userRepository.save(
                User.builder()
                        .name(signUpRequest.getName())
                        .email(signUpRequest.getEmail())
                        .password(signUpRequest.getPassword())
                        .build()
        );
    }

    @Override
    public TokenResponse SignIn(SignInRequest signInRequest) {
        userRepository.findByEmail(signInRequest.getEmail())
                .filter(user -> passwordEncoder.matches(signInRequest.getPassword(), user.getPassword()))
                .orElseThrow(UserNotFoundException::new);


        RefreshToken refreshToken = refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(signInRequest.getEmail())
                        .refreshToken(jwtTokenProvider.generateRefreshToken(signInRequest.getEmail()))
                        .refreshTokenTime(refreshTokenTime)
                        .build()
        );

        return TokenResponse.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(signInRequest.getEmail()))
                .refreshToken(refreshToken.getRefreshToken())
                .build();

    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String token) {
        if(!jwtTokenProvider.isRefreshToken(token))
            throw new InvalidTokenException();

        return refreshTokenRepository.findByRefreshToken(token)
                .map(refreshToken -> {
                    String generatedAccessToken = jwtTokenProvider.generateAccessToken(refreshToken.getEmail());
                    return refreshToken.update(generatedAccessToken, refreshTokenTime);
                })
                .map(refreshTokenRepository::save)
                .map(refreshToken -> {
                    String generatedAccessToken = jwtTokenProvider.generateAccessToken(refreshToken.getEmail());
                    return new TokenResponse(generatedAccessToken, refreshToken.getRefreshToken());
                })
                .orElseThrow(InvalidTokenException::new);
    }

}
