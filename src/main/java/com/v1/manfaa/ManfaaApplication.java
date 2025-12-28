package com.v1.manfaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@SpringBootApplication
public class ManfaaApplication {

	public static void main(String[] args) {
//        SecretKey key = Keys.secretKeyFor(HS256);
//        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
//
//        System.out.println("===========================================");
//        System.out.println("JWT SECRET KEY (Copy to application.properties):");
//        System.out.println("===========================================");
//        System.out.println("jwt.secret.current=" + base64Key);
//        System.out.println("===========================================");
//        System.out.println("\nKey length: " + key.getEncoded().length + " bytes");
        SpringApplication.run(ManfaaApplication.class, args);
	}

}
