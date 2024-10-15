package com.linkopus.ms.services;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.models.schemas.User;
import com.linkopus.ms.daos.UserRepository;
import com.linkopus.ms.middlewares.errorHandlers.ApiError.ErrorBody;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAllUsers() {
		LOGGER.info("Fetching all users");
		List<User> users = userRepository.findAll();
		LOGGER.info("Retrieved {} users", users.size());
		return users;
	}

	public User getUserById(String id) throws ApiError {
		LOGGER.info("Fetching user with ID: {}", id);
		User user = userRepository.findById(id).orElseThrow(() -> {
			ErrorBody errorBody = new ErrorBody(HttpStatus.NOT_FOUND, ErrorTypes.USER_NOT_FOUND,
					"User not found with ID: " + id);
			return new ApiError(errorBody, LOGGER);
		});

		return user;
	}

	public User createUser(User user) {
		LOGGER.info("Creating user: {}", user.getEmail());
		User createdUser = userRepository.save(user);
		LOGGER.info("User created with ID: {}", createdUser.getId());
		return createdUser;
	}

	public User updateUser(String id, User userDetails) throws ApiError {
		LOGGER.info("Updating user with ID: {}", id);

		User user = userRepository.findById(id).orElseThrow(() -> {
			ErrorBody errorBody = new ErrorBody(HttpStatus.NOT_FOUND, ErrorTypes.USER_NOT_FOUND,
					"User not found with ID: " + id);
			return new ApiError(errorBody, LOGGER);
		});

		user.setName(userDetails.getName());
		user.setEmail(userDetails.getEmail());
		User updatedUser = userRepository.save(user);
		LOGGER.info("User with ID {} updated", id);
		return updatedUser;
	}

	public void deleteUser(String id) throws ApiError {
		LOGGER.info("Deleting user with ID: {}", id);

		if (!userRepository.existsById(id)) {
			ErrorBody errorBody = new ErrorBody(HttpStatus.NOT_FOUND, ErrorTypes.USER_NOT_FOUND,
					"User not found with ID: " + id);
			throw new ApiError(errorBody, LOGGER);
		}

		userRepository.deleteById(id);
		LOGGER.info("User with ID {} deleted", id);
	}
}
