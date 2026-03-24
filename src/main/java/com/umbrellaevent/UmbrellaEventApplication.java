package com.umbrellaevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author Bilal Mirje
 * @author Amar Patil
 */

@SpringBootApplication
@RestController
@CrossOrigin("*")
public class UmbrellaEventApplication {

	public static void main(String[] args) {


		SpringApplication.run(UmbrellaEventApplication.class, args);
	}

	@GetMapping("/test")
	public String test(){
		return "Test endpoint for Umbrella working fine";
	}
}
