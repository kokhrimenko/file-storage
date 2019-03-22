package kn.fs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/profile").setViewName("profile");
		registry.addViewController("/").setViewName("profile");
		registry.addViewController("/upload").setViewName("upload");
		registry.addViewController("/error/404").setViewName("error");
		registry.addViewController("/login").setViewName("login");
	}
}
