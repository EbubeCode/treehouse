package com.garden.treehouse.services;

import com.garden.treehouse.model.Product;
import com.garden.treehouse.repos.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService{

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> findAll() {
		//		List<Product> activeProducts = new ArrayList<>();
//
//		for (Product product: products) {
//			if(product.isActive()) {
//				activeProducts.add(product);
//			}
//		}
		
		return productRepository.findAllByActive(true).orElse(List.of());
	}
	
	public Product findById(Long id) {
		return productRepository.findById(id).orElse(null);
	}
	
	public List<Product> findByCategory(String category){
		List<Product> products = productRepository.findByCategoryAndActive(category, true).orElse(List.of());
		
//		List<Product> activeProducts = new ArrayList<>();
//
//		for (Product product: products) {
//			if(product.isActive()) {
//				activeProducts.add(product);
//			}
//		}
		
		return products;
	}
	
	public List<Product> blurrySearch(String title) {
		List<Product> products = productRepository.findProductsByNameContainingAndActive(title, true).orElse(List.of());
//        List<Product> activeProducts = new ArrayList<>();
//
//		for (Product product: products) {
//			if(product.isActive()) {
//				activeProducts.add(product);
//			}
//		}
		
		return products;
	}

	public String save(Product product) {
		productRepository.save(product);
		return "Produce saved successfully";
	}

	public void deleteById(long id) {
		productRepository.deleteById(id);
	}
}
