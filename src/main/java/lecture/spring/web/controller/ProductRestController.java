package lecture.spring.web.controller;

import jakarta.validation.constraints.Min;
import lecture.spring.web.dto.ProductCreateReq;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product")
public class ProductRestController {
    @GetMapping("{id}")
    @ResponseBody
    public ProductCreateReq showDetail(@Min(100) @PathVariable Integer id) {
        return new ProductCreateReq(id, "Product " + id, id * 1000);
    }

    @GetMapping("create")
    @ResponseBody
    public ProductCreateReq create(@Validated ProductCreateReq req) {
        return req;
    }
}
