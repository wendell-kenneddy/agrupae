package com.agrupae.application.service.user;

import java.util.UUID;

import com.agrupae.application.exception.user.UserNotFoundException;
import com.agrupae.application.port.in.user.GetUserProfileUseCase;
import com.agrupae.application.port.in.user.UserProfileView;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.user.User;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class GetUserProfileService implements GetUserProfileUseCase {
    private final UserRepository userRepository;

    @Override
    public UserProfileView handle(@NonNull UUID userId) {
        User user = this.userRepository.findById(userId);

        if (user == null) throw new UserNotFoundException();

        UserProfileView view = new UserProfileView(
            userId,
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt(),
            user.getUpdatedAt());

        return view;
    }
}
