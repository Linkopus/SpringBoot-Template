package com.linkopus.ms.controllers;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.models.schemas.User;
import com.linkopus.ms.services.UserService;
import com.linkopus.ms.utils.ressources.enums.SuccessTypes;
import com.linkopus.ms.utils.ressources.models.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable String id) throws ApiError {
		User user = userService.getUserById(id);
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@PostMapping
	public ResponseEntity<SuccessResponse> createUser(@RequestBody User user) {
		userService.createUser(user);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new SuccessResponse(SuccessTypes.USER_ADDED_SUCCESSFULLY));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SuccessResponse> updateUser(@PathVariable String id, @RequestBody User userDetails)
			throws ApiError {
		User updatedUser = userService.updateUser(id, userDetails);
		return ResponseEntity.ok(new SuccessResponse(SuccessTypes.USER_UPDATED_SUCCESSFULLY));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse> deleteUser(@PathVariable String id) throws ApiError {
		userService.deleteUser(id);
		return ResponseEntity.ok(new SuccessResponse(SuccessTypes.USER_DELETED_SUCCESSFULLY));
	}
}
