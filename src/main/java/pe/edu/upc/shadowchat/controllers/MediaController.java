package pe.edu.upc.shadowchat.controllers;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/media")
@SecurityRequirement(name = "bearerAuth")
public class MediaController {

    @Value("${azure.storage.connection-string:}")
    private String connectionString;

    @Value("${azure.storage.container:shadowchat-media}")
    private String containerName;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file) {
        try {
            BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            BlobContainerClient containerClient = serviceClient
                    .getBlobContainerClient(containerName);
            containerClient.createIfNotExists();

            String extension = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf("."));
            }
            String blobName = "asesor/" + UUID.randomUUID() + extension;

            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            String url = blobClient.getBlobUrl();
            return ResponseEntity.ok(Map.of("url", url));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error subiendo imagen: " + e.getMessage()));
        }
    }
}