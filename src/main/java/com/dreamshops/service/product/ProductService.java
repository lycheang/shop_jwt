package com.dreamshops.service.product;

import com.dreamshops.Request.AddProductRequest;
import com.dreamshops.Request.UpdateProductRequest;
import com.dreamshops.dto.ImageDto;
import com.dreamshops.dto.ProductDto;
import com.dreamshops.model.Category;
import com.dreamshops.model.Image;
import com.dreamshops.model.Product;
import com.dreamshops.service.category.CategoryRepository;
import com.dreamshops.service.image.ImageRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Override

    public Product addProduct(AddProductRequest request) {
        //check if the category is found in the DB
        // if yes, set it as the new product category
        //if no,save it as a new category
        //the set as the new product category

        if(isProductExist(request.getName(),request.getBrand())){
            throw new RuntimeException("Product with name " + request.getName() + " and brand " + request.getBrand() + " already exist");
        }

        Category category= Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(()->{
                    Category newCategory=new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });

        request.setCategory(category);

        return productRepository.save(createProduct(request,category));


    }
    private boolean isProductExist(String name, String brand){
        return productRepository.existsByNameAndBrand(name,brand);
    }
    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product with id " + id + " not found"));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(product -> {
                    product.setActive(false); // <--- SOFT DELETE
                    productRepository.save(product);
                }, () -> new RuntimeException("Product not found"));
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Long productid) {
        return productRepository.findById(productid)
                .map(existingProduct->updateExistingProduct(existingProduct,request))
                .map(productRepository::save)
                .orElseThrow(() -> new RuntimeException("Product with id " + productid + " not found"));
    }
    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());
        Category category=categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setName(category.getName());
        return existingProduct;
    }
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrue();
    }
    @Override
    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category,brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand,name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand,name);
    }
    @Override
    public List<ProductDto> getConvertedAllProduct(List<Product> products){
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product){
        ProductDto productDto=modelMapper.map(product,ProductDto.class);
        if (product.getInventory() == 0) {
            productDto.setStockStatus("Out of Stock");
        } else if (product.getInventory() <= 10) {
            // Alert for Low Stock (1 to 10 items)
            productDto.setStockStatus("Low Stock! Only " + product.getInventory() + " left");
        } else {
            productDto.setStockStatus("In Stock");
        }
        List<Image> images= imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos=images.stream().map(image->modelMapper.map(image,ImageDto.class))
                .toList();
        productDto.setImages(imageDtos);
        return productDto;

    }
}
