package com.linkopus.ms.controllers;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.middlewares.errorHandlers.ApiError.ErrorBody;
import com.linkopus.ms.models.schemas.User;
import com.linkopus.ms.services.UserService;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import com.linkopus.ms.utils.ressources.enums.SuccessTypes;
import com.linkopus.ms.utils.ressources.models.SuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private final ApiError NOT_FOUND_ERROR = new ApiError(
			new ErrorBody(HttpStatus.NOT_FOUND, ErrorTypes.USER_NOT_FOUND, "User not found"),
			LoggerFactory.getLogger(UserControllerTest.class));

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getAllUsers_shouldReturnListOfUsers() {
		List<User> expectedUsers = Arrays.asList(new User(), new User());
		when(userService.getAllUsers()).thenReturn(expectedUsers);

		List<User> actualUsers = userController.getAllUsers();

		assertEquals(expectedUsers, actualUsers);
		verify(userService, times(1)).getAllUsers();
	}

	@Test
	void getUserById_shouldReturnUser() throws ApiError {
		String userId = "123";
		User expectedUser = new User();
		when(userService.getUserById(userId)).thenReturn(expectedUser);

		ResponseEntity<User> response = userController.getUserById(userId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedUser, response.getBody());
		verify(userService, times(1)).getUserById(userId);
	}

	@Test
	void createUser_shouldReturnSuccessResponse() {
		User user = new User();

		ResponseEntity<SuccessResponse> response = userController.createUser(user);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(SuccessTypes.USER_ADDED_SUCCESSFULLY, Objects.requireNonNull(response.getBody()).message());
		verify(userService, times(1)).createUser(user);
	}

	@Test
	void updateUser_shouldReturnSuccessResponse() throws ApiError {
		String userId = "123";
		User userDetails = new User();
		User updatedUser = new User();
		when(userService.updateUser(userId, userDetails)).thenReturn(updatedUser);

		ResponseEntity<SuccessResponse> response = userController.updateUser(userId, userDetails);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(SuccessTypes.USER_UPDATED_SUCCESSFULLY, Objects.requireNonNull(response.getBody()).message());
		verify(userService, times(1)).updateUser(userId, userDetails);
	}

	@Test
	void deleteUser_shouldReturnSuccessResponse() throws ApiError {
		String userId = "123";

		ResponseEntity<SuccessResponse> response = userController.deleteUser(userId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(SuccessTypes.USER_DELETED_SUCCESSFULLY, Objects.requireNonNull(response.getBody()).message());
		verify(userService, times(1)).deleteUser(userId);
	}

	@Test
	void getUserById_shouldThrowApiError() throws ApiError {
		String userId = "123";
		when(userService.getUserById(userId)).thenThrow(NOT_FOUND_ERROR);

		assertThrows(ApiError.class, () -> userController.getUserById(userId));
		verify(userService, times(1)).getUserById(userId);
	}

	@Test
	void updateUser_shouldThrowApiError() throws ApiError {
		String userId = "123";
		User userDetails = new User();
		when(userService.updateUser(userId, userDetails)).thenThrow(NOT_FOUND_ERROR);

		assertThrows(ApiError.class, () -> userController.updateUser(userId, userDetails));
		verify(userService, times(1)).updateUser(userId, userDetails);
	}

	@Test
	void deleteUser_shouldThrowApiError() throws ApiError {
		String userId = "123";
		doThrow(NOT_FOUND_ERROR).when(userService).deleteUser(userId);

		assertThrows(ApiError.class, () -> userController.deleteUser(userId));
		verify(userService, times(1)).deleteUser(userId);
	}
}
