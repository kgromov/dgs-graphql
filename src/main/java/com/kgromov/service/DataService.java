package com.kgromov.service;

import com.kgromov.graphql.dgs.types.Category;
import com.kgromov.graphql.dgs.types.Recipe;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

@Service
public class DataService {
    private final Map<String, Category> categoriesRepository = new ConcurrentHashMap<>();
    private final Map<String, List<Recipe>> recipesRepository = new ConcurrentHashMap<>();

    public void populateData() {
        Map<String, List<String>> recipesPerCategory = Map.of(
                "Fast food", List.of("Pizza", "Sushi", "Hot-Dog", "Hamburger"),
                "Slow food", List.of("Borsch", "Pasta")
        );
        AtomicInteger categoryId = new AtomicInteger();
        AtomicInteger recipeId = new AtomicInteger();
        recipesPerCategory.forEach((categoryName, recipeNames) -> {
            Category category = Category.newBuilder()
                    .id(String.valueOf(categoryId.incrementAndGet()))
                    .name(categoryName)
                    .build();
            List<Recipe> recipes = recipeNames.stream()
                    .map(name -> Recipe.newBuilder()
                            .id(String.valueOf(recipeId.incrementAndGet()))
                            .description(name)
                            .category(category)
                            .build())
                    .collect(Collectors.toList());
            categoriesRepository.put(category.getId(), category);
            recipesRepository.put(category.getId(), recipes);
        });
    }

    public Stream<Category> getCategories() {
        return categoriesRepository.values().stream();
    }

    public Stream<Recipe> getRecipes() {
        return recipesRepository.values().stream().flatMap(Collection::stream);
    }

    public Optional<Category> getCategoryBy(Predicate<Category> condition) {
        return this.getCategories().filter(condition).findFirst();
    }

    public Optional<Recipe> getRecipeBy(Predicate<Recipe> condition) {
        return this.getRecipes().filter(condition).findFirst();
    }

    public Stream<Recipe> getRecipesByCategory(Category category) {
        return this.recipesRepository.getOrDefault(category.getId(), emptyList()).stream();
    }
}
