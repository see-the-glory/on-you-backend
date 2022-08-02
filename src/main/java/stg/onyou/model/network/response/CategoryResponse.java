package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private Integer order;

    @QueryProjection
    public CategoryResponse(Long id, String name, String description, String thumbnail, Integer order){
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.order = order;
    }


}
