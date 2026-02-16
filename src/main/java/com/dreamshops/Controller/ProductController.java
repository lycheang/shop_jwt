package com.dreamshops.Controller;

import com.dreamshops.Response.ApiResponse;
import com.dreamshops.Request.AddProductRequest;
import com.dreamshops.Request.UpdateProductRequest;
import com.dreamshops.dto.ProductDto;
import com.dreamshops.model.Product;
import com.dreamshops.service.product.IProductService;
import com.dreamshops.service.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private  final IProductService productService;
    private final ProductRepository productRepository;
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts() {
        try{
            List<Product> products= productService.getAllProducts();
            List<ProductDto> productDtos=productService.getConvertedAllProduct(products);
            return ResponseEntity.ok(new ApiResponse("Success", productDtos));
        }
        catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,null));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id){
        try{
            Product products= productService.getProductById(id);
            ProductDto productDto=productService.convertToDto(products);
            return ResponseEntity.ok(new ApiResponse("Success", productDto));
        }
        catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,null));
        }
    }
    @GetMapping("/product/{name}")
    public ResponseEntity<ApiResponse> getProductsByName(@PathVariable String name){
        try{
            List<Product> products=productService.getProductsByName(name);
            return ResponseEntity.ok(new ApiResponse("Success", productService.getProductsByName(name)));
        }
        catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,null));
        }
    }
    @GetMapping("/by-brand-and-name")
    public ResponseEntity<ApiResponse> getProductsByBrandAndName(@RequestParam String brand,@RequestParam String name){
        try{
            List<Product> products=productService.getProductsByBrandAndName(brand,name);
            List<ProductDto> productDtos=productService.getConvertedAllProduct(products);
            if(products.isEmpty()){
                return ResponseEntity.ok(new ApiResponse("No product found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Success", productDtos));
        }
        catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,null));
        }
    }
    @GetMapping("/category/{category}/all")
    public ResponseEntity<ApiResponse> getProductsByCategoryName(@PathVariable String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found in category: " + category, null));
            }
            List<ProductDto> productDtos = productService.getConvertedAllProduct(products);
            return ResponseEntity.ok(new ApiResponse("Success", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }
    @GetMapping("/brand/{brand}/all")
    public ResponseEntity<ApiResponse> getProductsByBrand(@PathVariable String brand) {
        try {
            List<Product> products = productService.getProductsByBrand(brand);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found for brand: " + brand, null));
            }
            List<ProductDto> productDtos = productService.getConvertedAllProduct(products);
            return ResponseEntity.ok(new ApiResponse("Success", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }
    @GetMapping("/by-category-and-brand")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(@RequestParam String category,@RequestParam String brand){
        try{
            List<Product> products=productService.getProductsByCategoryAndBrand(category,brand);
            List<ProductDto> productDtos=productService.getConvertedAllProduct(products);
            if(products.isEmpty()){
                return ResponseEntity.ok(new ApiResponse("No product found", productDtos));
            }
            return ResponseEntity.ok(new ApiResponse("Success", productDtos));
        }
        catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product){
        try {
            Product theproduct = productService.addProduct(product);
            ProductDto productDto=productService.convertToDto(theproduct);
            return ResponseEntity.ok(new ApiResponse("Product Add Success", productDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }

    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody UpdateProductRequest product, @PathVariable Long id){
        try {
            Product theproduct=productService.updateProduct(product,id);
            var productDto=productService.convertToDto(theproduct);
            return ResponseEntity.ok(new ApiResponse("Product Update Success", theproduct));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,null));
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id){
        try {
             productService.deleteProduct(id);
            return ResponseEntity.ok(new ApiResponse("Product Delete Success", null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,null));
        }
    }
    @GetMapping("/count/by-brand-and-name")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam String brand,@RequestParam String name){
        try{
            return ResponseEntity.ok(new ApiResponse("Success", productService.countProductsByBrandAndName(brand,name)));
        }
        catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,null));
        }
    }
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> toggleProductStatus(@PathVariable Long id, @RequestParam boolean active) {
        try {
            Product product = productService.getProductById(id);
            product.setActive(active);
            productRepository.save(product); // You might want to move this save to the Service
            return ResponseEntity.ok(new ApiResponse("Product status updated to " + active, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }


}
