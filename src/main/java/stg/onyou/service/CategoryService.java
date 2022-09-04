package stg.onyou.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Category;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.CategoryResponse;
import stg.onyou.repository.CategoryRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * 전체 카테고리 select
     */
    public Header<List<CategoryResponse>> selectAllCategories(){

        List<Category> categoryList = categoryRepository.findAll();

        List<CategoryResponse> categoryResponseList = categoryList.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());

        if(categoryResponseList.isEmpty()){
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        return Header.OK(categoryResponseList);
    }

}
