package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.entitiy.ProductImage;
import com.example.shoppingmall.service.CategoryService;
import com.example.shoppingmall.service.ProductImageService;
import com.example.shoppingmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
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


    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/register")
    public String registerProduct(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "register_product";
    }

    @PostMapping("/register")
    public String registerProduct(Product product, @RequestParam("files") MultipartFile[] files) throws IOException {
        Product pro = productService.save(product);
        int i = 0;
        for (MultipartFile file : files) {
            i = i + 1;
            if (!file.isEmpty()) {

                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
                String fileNameWithoutExtension = StringUtils.stripFilenameExtension(originalFileName);
                String fileExtension = StringUtils.getFilenameExtension(originalFileName);
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                String newFileName = fileNameWithoutExtension + "_" + timestamp + "." + fileExtension;
                Path filePath = Paths.get(uploadDir + newFileName);
                Files.copy(file.getInputStream(), filePath);
                String fileUrl = "/uploads/" + newFileName;

                ProductImage productImage = new ProductImage();
                productImage.setProduct(pro);
                productImage.setDisplayOrder(i);
                productImage.setImageUrl(fileUrl);
                productImageService.save(productImage);

//                // 파일 저장 로직
//                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
//                fileName = timestamp + fileName;
//                fileName = fileName.replaceAll("[:]", "_");
//
//                Path path = Paths.get(uploadDir + fileName);
//
//                Files.copy(file.getInputStream(), path);
//                ProductImage productImage = new ProductImage();
//                productImage.setProduct(product);
//                productImage.setDisplayOrder(i);
//                productImage.setImageUrl(path.toString());
//                productImageService.save(productImage);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/list")
    public String products(Model model) {

        List<Product> products = productService.findAll();

        model.addAttribute("products", products);

        return "product_list";

    }

    @GetMapping("/{id}")
    public String getProduct(Model model, @PathVariable Long id) {

        Product product = productService.findById(id);
        List<ProductImage> images = productImageService.findByProductId(product.getId());
        model.addAttribute("images", images);

        String category = product.getCategory().getName();
        model.addAttribute("product", product);
        model.addAttribute("category", category);

        return "product_detail";

    }

}
