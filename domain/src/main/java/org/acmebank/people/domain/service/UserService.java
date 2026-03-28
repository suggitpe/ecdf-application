package org.acmebank.people.domain.service;

import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.port.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String email, String fullName, Grade grade, UUID managerId, boolean isIta, boolean isPromotionCoordinator) {
        User user = new User(null, email, fullName, grade, managerId, isIta, isPromotionCoordinator);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByManagerId(UUID managerId) {
        return userRepository.findByManagerId(managerId);
    }
}
