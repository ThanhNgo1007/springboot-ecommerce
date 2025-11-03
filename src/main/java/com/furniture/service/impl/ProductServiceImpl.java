package com.furniture.service.impl;

import com.furniture.exceptions.ProductException;
import com.furniture.modal.Category;
import com.furniture.modal.Product;
import com.furniture.modal.Seller;
import com.furniture.repository.CategoryRepository;
import com.furniture.repository.ProductRepository;
import com.furniture.request.CreateProductRequest;
import com.furniture.service.ProductService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

//    @Override
//    public Product createProduct(CreateProductRequest req, Seller seller) {
//
//        Category category1 = categoryRepository.findByCategoryId(req.getCategory());
//
//        if (category1 == null) {
//            Category category = new Category();
//            category.setCategoryId(req.getCategory());
//            category.setLevel(1);
//            category1 = categoryRepository.save(category);
//        }
//
//        Category category2 = categoryRepository.findByCategoryId(req.getCategory2());
//
//        if (category2 == null) {
//            Category category = new Category();
//            category.setCategoryId(req.getCategory2());
//            category.setLevel(2);
//            category.getParentCategory().add(category1);
//            category2 = categoryRepository.save(category);
//        }
//
//        Category category3 = categoryRepository.findByCategoryId(req.getCategory3());
//
//        if (category3 == null) {
//            Category category = new Category();
//            category.setCategoryId(req.getCategory3());
//            category.setLevel(3);
//            category.getParentCategory().add(category2);
//            category3 = categoryRepository.save(category);
//        }
//
//        Product product = new Product();
//        product.setSeller(seller);
//        product.setCategory(category3);
//        product.setDescription(req.getDescription());
//        product.setCreatedAt(LocalDateTime.now());
//        product.setTitle(req.getTitle());
//        product.setColor(req.getColor());
//        product.setSellingPrice(req.getSellingPrice());
//        product.setImages(req.getImages());
//        product.setMsrpPrice(req.getMsrpPrice());
//
//        return productRepository.save(product);
//    }

    @Override
    public Product createProduct(CreateProductRequest req, Seller seller) {

        // --- Room level 1 ---
        Category roomCategory = categoryRepository.findByCategoryId(req.getRoom());
        if (roomCategory == null) {
            roomCategory = new Category();
            roomCategory.setCategoryId(req.getRoom());
            roomCategory.setLevel(1);
            roomCategory = categoryRepository.save(roomCategory);
        }

        // --- Menu Products level 1 ---
        Category category1 = categoryRepository.findByCategoryId(req.getCategory());
        if (category1 == null) {
            category1 = new Category();
            category1.setCategoryId(req.getCategory());
            category1.setLevel(1);
            category1 = categoryRepository.save(category1);
        }

        // --- Menu Products level 2 ---
        Category category2 = categoryRepository.findByCategoryId(req.getCategory2());
        if (category2 == null) {
            category2 = new Category();
            category2.setCategoryId(req.getCategory2());
            category2.setLevel(2);
            category2.getParentCategory().add(category1);
            category2 = categoryRepository.save(category2);
        } else {
            category2.getParentCategory().add(category1);
            categoryRepository.save(category2);
        }

        // --- Category level 3 (Dressers & drawers) ---
//        Category category3 = categoryRepository.findByCategoryId(req.getCategory3());
//        if (category3 == null) {
//            category3 = new Category();
//            category3.setCategoryId(req.getCategory3());
//            category3.setLevel(3);
//            category3.getParentCategory().add(category2);
//            category3.getParentCategory().add(roomCategory); // Gắn thêm parent từ room
//            category3 = categoryRepository.save(category3);
//        } else {
//            category3.getParentCategory().add(category2);
//            category3.getParentCategory().add(roomCategory);
//            categoryRepository.save(category3);
        //}
        // --- CATEGORY LEVEL 3 (Dressers & drawers) ---
        Category category3 = categoryRepository.findByCategoryId(req.getCategory3());
        if (category3 == null) {
            category3 = new Category();
            category3.setCategoryId(req.getCategory3());
            category3.setLevel(3);
        }

        // Gắn parent: vừa thuộc "Bedroom" (room) vừa thuộc "Storage & organization"
        category3.getParentCategory().add(category2);
        category3.getParentCategory().add(roomCategory);
        category3 = categoryRepository.save(category3);


        int discountPercentage = calculateDiscountPercentage(req.getMsrpPrice(), req.getSellingPrice());

        // --- Tạo Product ---
        Product product = new Product();
        product.setSeller(seller);
        product.setRoom(roomCategory);
        product.setCategories(Set.of(category3)); // chỉ category3
        product.setDescription(req.getDescription());
        product.setCreatedAt(LocalDateTime.now());
        product.setTitle(req.getTitle());
        product.setColor(req.getColor());
        product.setSellingPrice(req.getSellingPrice());
        product.setImages(req.getImages());
        product.setMsrpPrice(req.getMsrpPrice());
        product.setDiscountPercent(discountPercentage);

        return productRepository.save(product);
    }

    private int calculateDiscountPercentage(int msrpPrice, int sellingPrice) {
        if (msrpPrice <= 0) {
            throw new IllegalArgumentException("Actual price must be greater than 0");
        }
        double discount = msrpPrice - sellingPrice;
        double discountPercentage = (discount/msrpPrice) * 100.0;

        return (int)discountPercentage;
    }


    @Override
    public void deleteProduct(Long productId) throws ProductException {

        Product product = findProductById(productId);
        productRepository.delete(product);

    }

    @Override
    public Product updateProduct(Long productId, Product product) throws ProductException {
        findProductById(productId);
        product.setId(productId);

        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long productId) throws ProductException {
        return productRepository.findById(productId).orElseThrow(() ->
                new ProductException("product not found with id"+productId));
    }

    @Override
    public List<Product> searchProducts(String query) {
        return productRepository.searchProduct(query);
    }

    @Override
    public Page<Product> getAllProducts(String category, String brand, String colors, Integer minPrice, Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber) {

        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                Join<Product, Category> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("categoryId"), category));
            }
            if (colors != null && !colors.isEmpty()) {
                System.out.println("colors: " + colors);
                predicates.add(criteriaBuilder.like(root.get("color"), colors));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sellingPrice"), maxPrice));
            }
            if (minDiscount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountPercent"), minDiscount));
            }
            if (stock != null) {
                predicates.add(criteriaBuilder.like(root.get("stock"), stock));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable;

        if (sort != null && !sort.isEmpty()) {
            pageable = switch (sort) {
                case "price_low" -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.by("sellingPrice").ascending());
                case "price_high" -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.by("sellingPrice").descending());
                default -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.unsorted());
            };
        }
        else {
            pageable = PageRequest.of(pageNumber != null ? pageNumber : 0, 10, Sort.unsorted());
        }
        return productRepository.findAll(spec, pageable);
    }

    @Override
    public List<Product> getProductBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
}
