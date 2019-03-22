package kn.fs.mvc;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kn.fs.domain.User;
import kn.fs.mvc.dto.UserRegistrationDTO;
import kn.fs.service.UserService;

@Controller
@RequestMapping("/register")
public class RegistrationController {

	@Autowired
    private UserService userService;
	
	@ModelAttribute("user")
    public UserRegistrationDTO userRegistrationDto() {
        return new UserRegistrationDTO();
    }
	
	@GetMapping
    public String showRegistrationForm(Model model) {
        return "register";
    }
	
	@PostMapping
	public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDTO userDto,
			BindingResult result) {
		if(!userDto.getPassword().equals(userDto.getConfirmPassword())) {
			result.rejectValue("password", null, "Password and confirm password are not equals. Please check!");
        }
        User existing = userService.loadUserByUsername(userDto.getUsername());
        if (existing != null){
            result.rejectValue("username", null, "There is already an account registered with that username.");
        }
        
        if (result.hasErrors()){
            return "register";
        }

        userService.createUser(userDto.toUser());
        return "redirect:/register?success";
    }

}
