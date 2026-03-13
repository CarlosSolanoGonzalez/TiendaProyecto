package com.tienda.config;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class StorageConfig {

    @Bean
    FirebaseApp firebaseApp(@Value("${firebase.bucket.name}") String bucketName,
                            @Value("${firebase.json.path}") String jsonPath,
                            @Value("${firebase.json.file}") String jsonFile) {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        String recurso = jsonPath + "/" + jsonFile;
        ClassPathResource resource = new ClassPathResource(recurso);
        if (!resource.exists()) {
            throw new IllegalStateException("No se encontro el archivo de Firebase en src/main/resources/" + recurso);
        }

        try (var inputStream = resource.getInputStream()) {
            FirebaseOptions.Builder builder = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream));
            if (StringUtils.hasText(bucketName)) {
                builder.setStorageBucket(bucketName);
            }
            return FirebaseApp.initializeApp(builder.build());
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible iniciar Firebase", ex);
        }
    }
}
