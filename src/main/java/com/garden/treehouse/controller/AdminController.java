package com.garden.treehouse.controller;

import com.garden.treehouse.model.Product;
import com.garden.treehouse.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@Controller
public class AdminController {

    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
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
                imageName = product.getName()+ product.getId() + ".png";
            else
                imageName = StringUtils.cleanPath(productImage.getOriginalFilename());


            String imageUrl = "/product-image/"+imageName;
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream("src/main/resources/static" + imageUrl));
            stream.write(bytes);
            stream.close();
            product.setImageUrl(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        productService.save(product);
        return "redirect:admin";
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
        model.addAttribute("book", product);

        return "admin/updateProduct";
    }


    @PostMapping("/admin/updateProduct")
    public String updateProductPost(@ModelAttribute("product") Product product, Principal principal) {
        if (!principal.getName().equals("admin"))
            return "badRequestPage";

        MultipartFile productImage = product.getProductImage();

        if(!productImage.isEmpty()) {
            try {
                byte[] bytes = productImage.getBytes();
                String imageName;

                if (productImage.getOriginalFilename() == null)
                    imageName = product.getName()+ product.getId() + ".png";
                else
                    imageName = StringUtils.cleanPath(productImage.getOriginalFilename());


                String imageUrl = "/product-image/"+imageName;
                Files.delete(Paths.get("src/main/resources/static" + product.getImageUrl()));

                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream("src/main/resources/static"+ imageUrl));
                stream.write(bytes);
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productService.save(product);

        return "redirect:admin/productInfo?id="+product.getId();
    }

    @GetMapping("/admin")
    public String productList(Model model, Principal principal) {
        System.out.println("Got here");
        if (!principal.getName().equals("admin"))
            return "badRequestPage";
        List<Product> productList = productService.findAll();
        model.addAttribute("productList", productList);
        return "admin/productList";

    }

    @PostMapping("/amin/remove")
    public String remove(
            @ModelAttribute("id") String id, Model model
    ) {
        productService.deleteById(Long.parseLong(id.substring(8)));
        List<Product> productList = productService.findAll();
        model.addAttribute("productList", productList);

        return "redirect:/admin";
    }
}
