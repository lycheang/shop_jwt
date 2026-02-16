package com.dreamshops.service.image;

import com.dreamshops.dto.ImageDto;
import com.dreamshops.model.Image;
import com.dreamshops.model.Product;
import com.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService{
    private final ImageRepository imageRepository;
    private final IProductService productService;
    private final FileStorageService fileStorageService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(()->new RuntimeException("Image with id " + id + " not found"));
    }

    @Override
    public void deleteImageById(Long id) {
        Image image = getImageById(id);

        // 2. Delete the physical file first
        if (image.getFilename() != null) {
            fileStorageService.deleteFile(image.getFilename());
        }

        // 3. Then delete the database record
        imageRepository.delete(image);
    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> imageDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // 4. Use the helper to save the file to the folder
                // This returns the filename (e.g., "iphone.png")
                String savedFileName = fileStorageService.storeFile(file);

                // 5. Save the METADATA (name, type, path) to the database
                Image image = new Image();
                image.setFilename(savedFileName); // Store the filename, NOT the bytes
                image.setFiletype(file.getContentType());
                image.setProduct(product);
                // image.setImage(null); // Ensure your Entity doesn't require the Blob anymore

                Image savedImage = imageRepository.save(image);

                // 6. Generate the download URL
                String downloadUrl = "/api/v1/images/download/" + savedImage.getId();
                savedImage.setDownloadUrl(downloadUrl);
                Image finalImage = imageRepository.save(savedImage);

                // 7. Convert to DTO
                ImageDto imageDto = new ImageDto();
                imageDto.setImageId(finalImage.getId());
                imageDto.setImageName(finalImage.getFilename());
                imageDto.setDownloadUrl(finalImage.getDownloadUrl());

                imageDtos.add(imageDto);

            } catch (Exception e) {
                throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
            }
        }
        return imageDtos;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            // 8. Overwrite the file on disk
            String savedFileName = fileStorageService.storeFile(file);

            // 9. Update DB metadata
            image.setFilename(savedFileName);
            image.setFiletype(file.getContentType());
            imageRepository.save(image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update image", e);
        }
    }
}
