package com.garden.treehouse.controller;

import com.garden.treehouse.model.Product;
import com.garden.treehouse.repos.ProductRepository;
import com.garden.treehouse.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    public AdminController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping("/admin/add")
    public String addProduct(Model model, Principal principal) {
        if (!principal.getName().equals("admin"))
            return "badRequestPage";

        Product product = new Product();
        model.addAttribute("product", product);
        return "admin/addProduct";
    }

    @PostMapping("/admin/add")
    public String addProductPost(@ModelAttribute("product") Product product) {


        MultipartFile productImage = product.getProductImage();

        try {
            byte[] bytes = productImage.getBytes();
            String imageName;

            if (productImage.getOriginalFilename() == null)
                imageName = product.getName() + product.getId() + ".png";
            else
                imageName = StringUtils.cleanPath(productImage.getOriginalFilename());


            String imageUrl = "/product-image/" + imageName;
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream("src/main/resources/static" + imageUrl));
            stream.write(bytes);
            stream.close();
            product.setImageUrl(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        productService.save(product);
        return "redirect:/admin";
    }

    @GetMapping("/admin/productInfo")
    public String productInfo(@RequestParam("id") Long id, Model model, Principal principal) {
        if (!principal.getName().equals("admin"))
            return "badRequestPage";

        Product product = productService.findById(id);
        model.addAttribute("product", product);

        return "admin/productInfo";
    }


    @GetMapping("/admin/updateProduct")
    public String updateProduct(@RequestParam("id") Long id, Model model, Principal principal) {
        if (!principal.getName().equals("admin"))
            return "badRequestPage";

        Product product = productService.findById(id);
        model.addAttribute("product", product);

        return "admin/updateProduct";
    }


    @PostMapping("/admin/updateProduct")
    public String updateProductPost(@RequestParam("id") Long id,
                                    @ModelAttribute("product") Product product, Principal principal) {
        if (!principal.getName().equals("admin"))
            return "badRequestPage";

        MultipartFile productImage = product.getProductImage();
        var currentProduct = productService.findById(id);

        if (!productImage.isEmpty()) {
            try {
                byte[] bytes = productImage.getBytes();
                String imageName;

                if (productImage.getOriginalFilename() == null)
                    imageName = product.getName() + product.getId() + ".png";
                else
                    imageName = StringUtils.cleanPath(productImage.getOriginalFilename());


                String imageUrl = "/product-image/" + imageName;
                if (currentProduct.getImageUrl() != null)
                    Files.delete(Paths.get("src/main/resources/static" + currentProduct.getImageUrl()));

                currentProduct.setImageUrl(imageUrl);

                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream("src/main/resources/static" + imageUrl));
                stream.write(bytes);
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        convertProduct(currentProduct, product);
        productService.save(currentProduct);

        return "redirect:/admin/productInfo?id=" + currentProduct.getId();
    }

    private void convertProduct(Product oldProduct, Product newProduct) {
        oldProduct.setName(newProduct.getName());
        oldProduct.setInStockNumber(newProduct.getInStockNumber());
        oldProduct.setPrice(newProduct.getPrice());
        oldProduct.setShippingWeight(newProduct.getShippingWeight());
        oldProduct.setDescription(newProduct.getDescription());
        oldProduct.setCategory(newProduct.getCategory());
    }

    @GetMapping("/admin")
    public String productList(@RequestParam(name = "category", required = false) String category, Model model, Principal principal) {
        System.out.println("Got here");
        if (!principal.getName().equals("admin"))
            return "badRequestPage";

        List<Product> productList;
        if (category != null)
            productList = productService.findByCategory(category);
        else {
            productList = new ArrayList<>();
            productRepository.findAll().forEach(productList::add);
        }

        model.addAttribute("productList", productList);
        return "admin/productList";

    }

    @PostMapping("/admin/remove")
    public String remove(
            @RequestParam("id") Long id, Principal principal) {
        if (!principal.getName().equals("admin"))
            return "badRequestPage";

        var product = productService.findById(id);

        if (product != null && product.getImageUrl() != null) {
            try {
                Files.delete(Paths.get("src/main/resources/static" + product.getImageUrl()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productService.deleteById(id);
        return "redirect:/admin";
    }
}
