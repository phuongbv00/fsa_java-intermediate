package lecture.spring.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductCreateReq {
    private Integer id;
    @Size(min = 1, max = 50)
    @NotNull
    private String name;
    @Min(1000)
    @NotNull
    private Integer price;

    public ProductCreateReq() {
    }

    public ProductCreateReq(Integer id, String name, Integer price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
