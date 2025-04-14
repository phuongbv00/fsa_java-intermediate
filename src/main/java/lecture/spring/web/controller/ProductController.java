package lecture.spring.web.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lecture.spring.web.dto.ProductCreateReq;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("product")
public class ProductController {
    // HandlerMethodValidationException if id is < 100
    @GetMapping("{id}")
    public String showDetail(@Min(100) @PathVariable Integer id, @Size(max = 5) @RequestParam String name, Model model) {
        model.addAttribute("productCreateReq", new ProductCreateReq(id, "Product " + id, id * 1000));
        return "product/create";
    }

    @GetMapping("create")
    public String showCreateForm(Model model) {
        model.addAttribute("productCreateReq", new ProductCreateReq(null, "", 0));
        return "product/create";
    }

    // Validation errors are captured by BindingResult
    @PostMapping("create")
    public String handleCreate(@Validated @ModelAttribute ProductCreateReq productCreateReq, BindingResult result) {
        if (result.hasErrors()) {
            return "product/create";
        }

        // Save product logic here...

        return "redirect:/product/create";
    }

    // MethodArgumentNotValidException
//    @PostMapping("create")
//    public String handleCreate(@Validated @ModelAttribute ProductCreateReq productCreateReq) {
//
//        // Save product logic here...
//
//        return "redirect:/product/create";
//    }

    // No trigger any validation exception
//    @PostMapping("create")
//    public String handleCreate(@ModelAttribute ProductCreateReq productCreateReq) {
//
//        // Save product logic here...
//
//        return "redirect:/product/create";
//    }
}
