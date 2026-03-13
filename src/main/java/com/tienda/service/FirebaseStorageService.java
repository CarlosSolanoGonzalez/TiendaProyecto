package com.tienda.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirebaseStorageService {

    private final ObjectProvider<FirebaseApp> firebaseAppProvider;

    @Value("${firebase.storage.path}")
    private String storagePath;

    @Value("${firebase.bucket.name}")
    private String bucketName;

    public String cargaImagen(MultipartFile archivo, String carpeta) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        FirebaseApp firebaseApp = firebaseAppProvider.getIfAvailable();
        if (firebaseApp == null) {
            throw new IllegalStateException("Firebase no esta configurado. Verifique firebase.json.path y firebase.json.file.");
        }

        String nombreOriginal = StringUtils.cleanPath(archivo.getOriginalFilename());
        String extension = "";
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto >= 0) {
            extension = nombreOriginal.substring(punto);
            nombreOriginal = nombreOriginal.substring(0, punto);
        }

        String nombreUnico = normalizarNombre(nombreOriginal) + "-" + UUID.randomUUID() + extension;
        String ruta = construirRuta(carpeta) + nombreUnico;
        try {
            Bucket bucket = StorageClient.getInstance(firebaseApp).bucket();
            Blob blob = bucket.create(ruta, archivo.getBytes(), archivo.getContentType());
            return construirUrl(blob.getName());
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible subir la imagen a Firebase Storage", ex);
        }
    }

    private String construirRuta(String carpeta) {
        StringBuilder ruta = new StringBuilder();
        if (StringUtils.hasText(storagePath)) {
            ruta.append(storagePath.replace("\\", "/"));
            if (ruta.charAt(ruta.length() - 1) != '/') {
                ruta.append('/');
            }
        }
        if (StringUtils.hasText(carpeta)) {
            ruta.append(carpeta.replace("\\", "/"));
            if (ruta.charAt(ruta.length() - 1) != '/') {
                ruta.append('/');
            }
        }
        return ruta.toString();
    }

    private String construirUrl(String nombreBlob) {
        return "https://firebasestorage.googleapis.com/v0/b/" + bucketName
                + "/o/" + URLEncoder.encode(nombreBlob, StandardCharsets.UTF_8)
                + "?alt=media";
    }

    private String normalizarNombre(String nombreBase) {
        String limpio = StringUtils.hasText(nombreBase) ? nombreBase : "producto";
        return limpio.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
