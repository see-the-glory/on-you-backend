package stg.onyou.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Category;
import stg.onyou.model.entity.Club;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.CategoryResponse;
import stg.onyou.model.network.response.ClubApiResponse;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryApiService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Header<List<CategoryResponse>> selectAllCategories(){

        List<Category> categoryList = categoryRepository.findAll();

        List<CategoryResponse> resultList = categoryList.stream()
                .map(category -> modelMapper
                .map(category, CategoryResponse.class))
                .collect(Collectors.toList());

        if(resultList.isEmpty()){
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        return Header.OK(resultList);
    }

}
