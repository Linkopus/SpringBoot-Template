package com.linkopus.ms.daos;

import com.linkopus.ms.models.schemas.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	void cleanUp() {
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("Test saving and retrieving a user")
	void testSaveAndFindUser() {
		User user = new User();
		user.setId("1");
		user.setName("John Doe");
		user.setEmail("johndoe@example.com");

		userRepository.save(user);
		Optional<User> foundUser = userRepository.findById("1");

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getName()).isEqualTo("John Doe");
		assertThat(foundUser.get().getEmail()).isEqualTo("johndoe@example.com");
	}

	@Test
	@DisplayName("Test deleting a user")
	void testDeleteUser() {
		User user = new User();
		user.setId("2");
		user.setName("Jane Smith");
		user.setEmail("janesmith@example.com");
		userRepository.save(user);

		userRepository.deleteById("2");
		Optional<User> foundUser = userRepository.findById("2");

		assertThat(foundUser).isNotPresent();
	}

	@Test
	@DisplayName("Test updating a user")
	void testUpdateUser() {
		User user = new User();
		user.setId("3");
		user.setName("Bob Brown");
		user.setEmail("bobbrown@example.com");
		userRepository.save(user);

		user.setEmail("bob.brown@newdomain.com");
		userRepository.save(user);
		Optional<User> updatedUser = userRepository.findById("3");

		assertThat(updatedUser).isPresent();
		assertThat(updatedUser.get().getEmail()).isEqualTo("bob.brown@newdomain.com");
	}

	@Test
	@DisplayName("Test retrieving all users")
	void testFindAllUsers() {
		User user1 = new User();
		user1.setId("4");
		user1.setName("Alice Blue");
		user1.setEmail("aliceblue@example.com");

		User user2 = new User();
		user2.setId("5");
		user2.setName("Charlie Green");
		user2.setEmail("charliegreen@example.com");

		userRepository.save(user1);
		userRepository.save(user2);

		Iterable<User> users = userRepository.findAll();

		assertThat(users).hasSize(2);
	}
}
