package com.agrupae.application.service.user;

import java.util.UUID;

import com.agrupae.application.exception.user.UserNotFoundException;
import com.agrupae.application.port.in.user.UpdateProfileUseCase;
import com.agrupae.application.port.in.user.UserProfileView;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateProfileService implements UpdateProfileUseCase {
    private final UserRepository userRepository;

    @Override
    public UserProfileView handle(UUID userId, String name, String email) {
        User user = this.userRepository.findById(userId);

        if (user == null) {
            throw new UserNotFoundException();
        }

        user.updateProfile(name, email);
        User updatedUser = userRepository.save(user);

        return new UserProfileView(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt());
    }

}
