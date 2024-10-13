package com.linkopus.ms.services;

import com.linkopus.ms.daos.UserRepository;
import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.models.schemas.User;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private User createUser(String id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}

	@Test
	void getAllUsers_shouldReturnListOfUsers() {
		List<User> expectedUsers = Arrays.asList(createUser("1", "John Doe", "john@example.com"),
				createUser("2", "Jane Doe", "jane@example.com"));
		when(userRepository.findAll()).thenReturn(expectedUsers);

		List<User> actualUsers = userService.getAllUsers();

		assertEquals(expectedUsers, actualUsers);
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void getUserById_shouldReturnUser_whenUserExists() throws ApiError {
		String userId = "1";
		User expectedUser = createUser(userId, "John Doe", "john@example.com");
		when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

		User actualUser = userService.getUserById(userId);

		assertEquals(expectedUser, actualUser);
		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	void getUserById_shouldThrowApiError_whenUserNotFound() {
		String userId = "nonexistent";
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		ApiError apiError = assertThrows(ApiError.class, () -> userService.getUserById(userId));
		assertEquals(HttpStatus.NOT_FOUND, apiError.getErrorBody().getStatus());
		assertEquals(ErrorTypes.USER_NOT_FOUND, apiError.getErrorBody().getName());
		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	void createUser_shouldReturnCreatedUser() {
		User userToCreate = createUser(null, "John Doe", "john@example.com");
		User createdUser = createUser("1", "John Doe", "john@example.com");
		when(userRepository.save(userToCreate)).thenReturn(createdUser);

		User actualUser = userService.createUser(userToCreate);

		assertEquals(createdUser, actualUser);
		verify(userRepository, times(1)).save(userToCreate);
	}

	@Test
	void updateUser_shouldReturnUpdatedUser_whenUserExists() throws ApiError {
		String userId = "1";
		User existingUser = createUser(userId, "John Doe", "john@example.com");
		User userDetails = createUser(null, "John Updated", "john.updated@example.com");
		User updatedUser = createUser(userId, "John Updated", "john.updated@example.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenReturn(updatedUser);

		User actualUser = userService.updateUser(userId, userDetails);

		assertEquals(updatedUser, actualUser);
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void updateUser_shouldThrowApiError_whenUserNotFound() {
		String userId = "nonexistent";
		User userDetails = createUser(null, "John Updated", "john.updated@example.com");
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		ApiError apiError = assertThrows(ApiError.class, () -> userService.updateUser(userId, userDetails));
		assertEquals(HttpStatus.NOT_FOUND, apiError.getErrorBody().getStatus());
		assertEquals(ErrorTypes.USER_NOT_FOUND, apiError.getErrorBody().getName());
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void deleteUser_shouldDeleteUser_whenUserExists() throws ApiError {
		String userId = "1";
		when(userRepository.existsById(userId)).thenReturn(true);

		userService.deleteUser(userId);

		verify(userRepository, times(1)).existsById(userId);
		verify(userRepository, times(1)).deleteById(userId);
	}

	@Test
	void deleteUser_shouldThrowApiError_whenUserNotFound() {
		String userId = "nonexistent";
		when(userRepository.existsById(userId)).thenReturn(false);

		ApiError apiError = assertThrows(ApiError.class, () -> userService.deleteUser(userId));
		assertEquals(HttpStatus.NOT_FOUND, apiError.getErrorBody().getStatus());
		assertEquals(ErrorTypes.USER_NOT_FOUND, apiError.getErrorBody().getName());
		verify(userRepository, times(1)).existsById(userId);
		verify(userRepository, never()).deleteById(anyString());
	}
}
