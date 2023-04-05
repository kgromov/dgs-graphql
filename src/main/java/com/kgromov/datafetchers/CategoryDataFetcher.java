package com.kgromov.datafetchers;

import com.kgromov.graphql.dgs.types.Category;
import com.kgromov.graphql.dgs.types.Recipe;
import com.kgromov.service.DataService;
import com.netflix.graphql.dgs.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class CategoryDataFetcher {
    private final DataService dataService;

    @DgsQuery
    public Category category(@InputArgument String id) {
        return dataService.getCategoryBy(category -> category.getId().equals(id)).orElse(null);
    }

    @DgsQuery
    public Category categoryByName(@InputArgument String name) {
        return dataService.getCategoryBy(category -> category.getName().equals(name)).orElse(null);
    }

    @DgsQuery
    public List<Category> categories() {
        return dataService.getCategories().toList();
    }

    @DgsData(parentType = "Category")
    public List<Recipe> recipes(DgsDataFetchingEnvironment dfe) {
        Category category = dfe.getSource();
        return dataService.getRecipesByCategory(category).toList();
    }
}
