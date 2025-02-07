package vanii.bookingapp.service.user;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vanii.bookingapp.dto.user.RegistrationRequestDto;
import vanii.bookingapp.dto.user.RegistrationResponseDto;
import vanii.bookingapp.exception.UserExistsException;
import vanii.bookingapp.mapper.UserMapper;
import vanii.bookingapp.model.Role;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.role.RoleRepository;
import vanii.bookingapp.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Pattern ADMIN_PATTERN = Pattern.compile("admin([1-9][0-9]?)@.*");
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    @Override
    public RegistrationResponseDto register(RegistrationRequestDto requestDto)
            throws UserExistsException {
        verifyValidEmail(requestDto.email());
        User user = mapper.toModel(requestDto);
        user.setPassword(encoder.encode(requestDto.password()));
        Set<Role> roles = new HashSet<>();
        for (Role role : roleRepository.findAll()) {
            if (role.getRole().equals(Role.RoleName.CUSTOMER)) {
                roles.add(role);
            }
            if (role.getRole().equals(Role.RoleName.MANAGER)
                    && ADMIN_PATTERN.matcher(requestDto.email()).find()) {
                roles.add(role);
            }
        }
        user.setRoles(roles);
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private void verifyValidEmail(String email) throws UserExistsException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserExistsException("User with this email already exists. Email: " + email);
        }
    }
}
