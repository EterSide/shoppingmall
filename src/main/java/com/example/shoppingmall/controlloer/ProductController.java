package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.entitiy.ProductImage;
import com.example.shoppingmall.repository.ProductImageRepository;
import com.example.shoppingmall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/register")
    public String registerProduct() {
        return "register_product";
    }

    @PostMapping("/register")
    public String registerProduct(Product product, @RequestParam("files") MultipartFile[] files) throws IOException {

        int i = 0;
        productRepository.save(product);

        for (MultipartFile file : files) {
            i = i + 1;
            if (!file.isEmpty()) {
                // 파일 저장 로직
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                fileName = timestamp + fileName;
                fileName = fileName.replaceAll("[:]", "_");

                Path path = Paths.get(uploadDir + fileName);

                Files.copy(file.getInputStream(), path);
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setDisplayOrder(i);
                productImage.setImageUrl(path.toString());
                productImageRepository.save(productImage);

            }

        }

        return "redirect:/";
    }

    @GetMapping("/products")
    public String products(Model model) {

        List<Product> products = productRepository.findAll();

        model.addAttribute("products", products);

        return "product";

    }

}
