package com.dreamshops.service.category;

import com.dreamshops.model.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    Category addCategory(Category category);
    Category updateCategory(Category category, Long id);
    List<Category> getAllCategories();
    void deleteCategory(Long id);

}
