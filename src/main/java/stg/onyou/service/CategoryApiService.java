package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Club;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.CategoryResponse;
import stg.onyou.model.network.response.ClubApiResponse;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryApiService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Header<List<CategoryResponse>> selectAllCategories(){

        List<CategoryResponse> categories = new ArrayList<CategoryResponse>();

        categoryRepository.findAll()
                .forEach(category->{
                    categories.add(selectCategory(category));
                });

        if(categories.isEmpty()){
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return Header.OK(categories);
    }

}
