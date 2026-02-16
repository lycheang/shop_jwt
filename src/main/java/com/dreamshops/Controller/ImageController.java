package com.dreamshops.Controller;

import com.dreamshops.Response.ApiResponse;
import com.dreamshops.dto.ImageDto;
import com.dreamshops.model.Image;
import com.dreamshops.service.image.FileStorageService;
import com.dreamshops.service.image.IImageService;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/images")

@RequiredArgsConstructor
public class ImageController {
    private final IImageService imageService;
    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> saveImages(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("productId") Long productId) {

        // Data Flow: Controller routes request -> Service validates and processes -> Controller returns DTO
        List<ImageDto> imageDtos = imageService.saveImages(files, productId);
        return ResponseEntity.ok(new ApiResponse("Upload success", imageDtos));
    }
    @GetMapping("/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) {
        // 1. Get image metadata from DB
        Image image = imageService.getImageById(imageId);

        // 2. Load file from Disk using the filename stored in DB
        Resource resource = fileStorageService.loadFileAsResource(image.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFiletype()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() + "\"")
                .body(resource);
    }
    @PutMapping("/update/{imageId}")
    public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId,@RequestBody MultipartFile file){
        try {
            Image image=imageService.getImageById(imageId);
            if(image!=null){
                imageService.updateImage(file,imageId);
                return  ResponseEntity.ok(new ApiResponse("Image updated successfully",null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Image not found", null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Image not found", null));
    }
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId){
        try {
            Image image=imageService.getImageById(imageId);
            if(image!=null){
                imageService.deleteImageById(imageId);
                return  ResponseEntity.ok(new ApiResponse("Image deleted successfully",null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Image not found", null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Image not found", null));
    }
}
