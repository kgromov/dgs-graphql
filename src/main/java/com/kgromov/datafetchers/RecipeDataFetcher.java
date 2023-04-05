package com.kgromov.datafetchers;

import com.kgromov.graphql.dgs.types.Category;
import com.kgromov.graphql.dgs.types.Recipe;
import com.kgromov.service.DataService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class RecipeDataFetcher {
    private final DataService dataService;

    @DgsQuery(field = "recipes")
    public List<Recipe> getRecipes(DataFetchingEnvironment dfe) {
        Category category = dfe.getSource();
        return dataService.getRecipes().toList();
    }

    @DgsQuery
    public Recipe recipe(@InputArgument String id, DataFetchingEnvironment dfe) {
        Category category = dfe.getSource();
        return dataService.getRecipeBy(recipe -> recipe.getId().equals(id)).orElse(null);
    }
}
