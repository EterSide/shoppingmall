package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.entitiy.ProductImage;
import com.example.shoppingmall.service.CategoryService;
import com.example.shoppingmall.service.ProductImageService;
import com.example.shoppingmall.service.ProductRecommendationListener;
import com.example.shoppingmall.service.ProductService;
import com.example.shoppingmall.service.ProductViewEventService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {


    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductViewEventService productViewEventService;
    private final ProductRecommendationListener recommendationListener;


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

    @GetMapping("/list/{id}")
    public String products(Model model, @PathVariable Long id) {

        Category category = new Category();
        Optional<Category> op = categoryService.findById(id);
        if (op.isPresent()) {
            category = op.get();
        }
        List<Product> products = productService.findByCategory(category);

        model.addAttribute("products", products);

        return "product_list";

    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model) {
        List<Product> searchResults = productService.searchByName(keyword);
        model.addAttribute("products", searchResults);
        return "product_list";
    }

    @GetMapping("/{id}")
    public String getProduct(HttpSession session,Model model, @PathVariable Long id) {

        Product product = productService.findById(id);
        List<ProductImage> images = productImageService.findByProductId(product.getId());
        model.addAttribute("images", images);



        String category = product.getCategory().getName();
        model.addAttribute("product", product);
        model.addAttribute("category", category);

        product.setViewCount(product.getViewCount() + 1);

        productService.update(product);
        Member member = (Member) session.getAttribute("member");
        if (member != null){
            productViewEventService.sendProductViewEvent(member.getId(), product);

        }


        return "product_detail";

    }

    @GetMapping("/list/all")
    public String allProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "product_all_list";
    }

    @GetMapping("/admin/{id}")
    public String adminProducts(Model model, @PathVariable Long id, HttpSession session) {

        Product product = productService.findById(id);
        List<ProductImage> images = productImageService.findByProductId(product.getId());
        model.addAttribute("images", images);

        String category = product.getCategory().getName();
        model.addAttribute("product", product);
        model.addAttribute("category", category);

        product.setViewCount(product.getViewCount() + 1);

        productService.update(product);

        return "product_admin";
    }

    @DeleteMapping("/image/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        try {

            Optional<ProductImage> byId = productImageService.findById(id);
            ProductImage image = new ProductImage();
            if (byId.isPresent()) {
                image = byId.get();
            }

            // 실제 파일 삭제
            String filePath = uploadDir + image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
            Files.deleteIfExists(Paths.get(filePath));
            
            // DB에서 이미지 정보 삭제
            productImageService.deleteById(id);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, 
                              Product product,
                              @RequestParam("files") MultipartFile[] files) throws IOException {
        Product existingProduct = productService.findById(id);
        // 기존 제품 정보 업데이트
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        
        // 새로운 이미지 추가
        int displayOrder = productImageService.getMaxDisplayOrder(id) + 1;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // 기존의 파일 업로드 로직 사용
                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
                String fileNameWithoutExtension = StringUtils.stripFilenameExtension(originalFileName);
                String fileExtension = StringUtils.getFilenameExtension(originalFileName);
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                String newFileName = fileNameWithoutExtension + "_" + timestamp + "." + fileExtension;
                Path filePath = Paths.get(uploadDir + newFileName);
                Files.copy(file.getInputStream(), filePath);
                String fileUrl = "/uploads/" + newFileName;

                ProductImage productImage = new ProductImage();
                productImage.setProduct(existingProduct);
                productImage.setDisplayOrder(displayOrder++);
                productImage.setImageUrl(fileUrl);
                productImageService.save(productImage);
            }
        }
        
        productService.update(existingProduct);
        return "redirect:/product/admin/" + id;
    }

}
