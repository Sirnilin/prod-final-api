package com.example.prodolymp.service;

import com.example.prodolymp.models.ProductModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.repositories.ProductRepositories;
import com.example.prodolymp.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepositories productRepositories;
    private final UserRepositories userRepositories;

    public ProductModel addProduct(String title, String category, String description, Integer price){
        ProductModel product = new ProductModel();

        product.setDescription(description);
        product.setTitle(title);
        product.setPrice(price);
        product.setCategory(category);
        product.setIsBought(false);

        productRepositories.save(product);

        return product;
    }

    public List<ProductModel> getAllProduct(UserModel user){
        List<ProductModel> productModels = productRepositories.findAll();
        List<ProductModel> result = new ArrayList<>();
        Set<Long> userProductId = user.getProductIds();

        for(ProductModel product : productModels){
            if(userProductId.contains(product.getId())){
                product.setIsBought(true);
            }

            result.add(product);
        }

        return result;
    }

    public ProductModel buyProduct(Long id, UserModel user){
        if(productRepositories.findById(id).isEmpty()){
            return null;
        }

        ProductModel product = productRepositories.findById(id).get();

        if(product.getPrice() > user.getPoints()){
            return null;
        }

        user.setPoints(user.getPoints() - product.getPrice());
        user.getProductIds().add(product.getId());
        product.setIsBought(true);
        userRepositories.save(user);

        return product;
    }

    public List<ProductModel> getAllUserProduct(UserModel user){
        List<ProductModel> result = new ArrayList<>();
        List<ProductModel> productModels = productRepositories.findAll();

        for(ProductModel product : productModels){
            if(user.getProductIds().contains(product.getId())){
                result.add(product);
            }
        }

        return result;
    }

    public ProductModel addImage(String image, Long id){
        if(productRepositories.findById(id).isEmpty()){
            return null;
        }

        ProductModel product = productRepositories.findById(id).get();

        product.setImage(image);

        productRepositories.save(product);
        return product;
    }

}
