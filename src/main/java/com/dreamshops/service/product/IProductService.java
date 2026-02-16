package com.dreamshops.service.product;

import com.dreamshops.Request.AddProductRequest;
import com.dreamshops.Request.UpdateProductRequest;
import com.dreamshops.dto.ProductDto;
import com.dreamshops.model.Product;
import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long id);
    void deleteProduct(Long id);
    Product updateProduct(UpdateProductRequest product, Long productid);
    List<Product> getAllProducts();

    List<Product> getAllProductsForAdmin();

    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category,String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String brand,String name);
    Long countProductsByBrandAndName(String brand,String name);

    List<ProductDto> getConvertedAllProduct(List<Product> products);

    ProductDto convertToDto(Product product);
}
