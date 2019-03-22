package kn.fs.mvc;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kn.fs.domain.User;
import kn.fs.mvc.dto.UserDTO;
import kn.fs.service.UserService;
import kn.fs.utils.SecurityUtils;

@RestController
@RequestMapping("/api/v1/userManagement")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/shareFile/{userId}/{fileId}")
	public void share(@PathVariable(name = "userId") Long userId, @PathVariable(name = "fileId") Long fileId) {
		userService.share(userId, fileId, true);
	}
	
	@PostMapping("/removeShareFile/{userId}/{fileId}")
	public void removeShare(@PathVariable Long userId, @PathVariable Long fileId) {
		userService.share(userId, fileId, false);
	}

	@GetMapping("/all")
	public List<UserDTO> getAll(Principal principal) {
		List<User> users = userService.loadAllExceptCurrent(SecurityUtils.getLoggedUserId(principal));
		if(users == null || users.isEmpty()) {
			return Collections.emptyList();
		}
		
		return users.stream().map(user -> new UserDTO(user))
				.collect(Collectors.toList());
	}
}