package com.kgromov.service;

import com.kgromov.graphql.dgs.types.Category;
import com.kgromov.graphql.dgs.types.Recipe;
import com.kgromov.graphql.dgs.types.RecipeDto;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public Recipe createRecipe(String description) {
        List<Category> categories = this.getCategories().collect(Collectors.toList());
        Collections.shuffle(categories);
        Category category = categories.get(0);
        Recipe recipe = Recipe.newBuilder()
                .description(description)
                .category(category)
                .build();
        this.attachToCategory(recipe);
        return recipe;
    }

    public Recipe createRecipe(RecipeDto recipeDto) {
        Category category = this.getCategoryBy(c -> c.getId().equals(recipeDto.getCategoryId())).orElseThrow();
        Recipe recipe = Recipe.newBuilder()
                .id(String.valueOf(recipesRepository.size() + 1))
                .description(recipeDto.getDescription())
                .cookTime(recipeDto.getCookTime())
                .prepTime(recipeDto.getPrepTime())
                .url(recipeDto.getUrl())
                .difficulty(recipeDto.getDifficulty())
                .notes(recipeDto.getNotes())
                .servings(recipeDto.getServings())
                .source(recipeDto.getSource())
                .category(category)
                .build();
        this.attachToCategory(recipe);
        return recipe;
    }

    private void attachToCategory(Recipe recipe) {
        String categoryId = recipe.getCategory().getId();
        String recipeId = String.valueOf(recipesRepository.getOrDefault(categoryId, emptyList()).size() + 1);
        recipe.setId(recipeId);
        recipesRepository.computeIfAbsent(categoryId,
                recipes -> new ArrayList<>()).add(recipe);
    }

    public Category createCategory(String name) {
        Category category = new Category.Builder()
                .id(String.valueOf(this.categoriesRepository.size() + 1))
                .name(name)
                .build();
        categoriesRepository.put(category.getId(), category);
        return category;
    }
}
