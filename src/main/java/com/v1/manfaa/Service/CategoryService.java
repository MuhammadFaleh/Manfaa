package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.CategoryDTOIn;
import com.v1.manfaa.DTO.Out.CategoryDTOOut;
import com.v1.manfaa.DTO.Out.UserDTOOut;
import com.v1.manfaa.Model.Category;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.CategoryRepository;
import com.v1.manfaa.Repository.ServiceRequestRepository;
import com.v1.manfaa.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository  categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryDTOOut> convertToDtoOut(List<Category> categories) {
        return categories.stream()
                .map(category -> new CategoryDTOOut(
                        category.getName(),
                        category.getDescription()
                ))
                .toList();
    }
    public List<CategoryDTOOut>getAllCategory(){
        return  convertToDtoOut(categoryRepository.findAll());
    }

    public void addCategory(Integer userId ,CategoryDTOIn categoryDTOIn){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }
        Category category = new Category(null, categoryDTOIn.getName(), categoryDTOIn.getDescription(), null, null);
        categoryRepository.save(category);


    }

    public void updateCategory(Integer userId ,Integer categoryId , CategoryDTOIn categoryDTOIn){
        Category old =   categoryRepository.findCategoryById(categoryId);
        User user = userRepository.findUserById(userId);

        if(user == null){
            throw new ApiException("User not found");
        }

      if(old == null){
          throw new ApiException("category not found ");
      }

      old.setName(categoryDTOIn.getName());
      old.setDescription(categoryDTOIn.getDescription());
      categoryRepository.save(old);

    }

    public void deleteCategory(Integer userId , Integer categoryId){
        User user = userRepository.findUserById(userId);
        Category old = categoryRepository.findCategoryById(categoryId);

        if(user == null){
            throw new ApiException("User not found");
        }
        if(old == null){
            throw new ApiException("category not found ");
        }
        categoryRepository.delete(old);


    }


}
