package com.kgromov;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.TypeRef;
import com.kgromov.graphql.dgs.client.*;
import com.kgromov.graphql.dgs.types.Category;
import com.kgromov.graphql.dgs.types.Recipe;
import com.kgromov.service.DataService;
import com.netflix.graphql.dgs.client.*;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class DgsGraphqlApplication {
    @Value("${base.url}")
    private String baseUrl;

    public static void main(String[] args) {
        SpringApplication.run(DgsGraphqlApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(DataService dataService,
                                        GraphQLClient blockingClient) {
        return args -> {
            dataService.populateData();

            var recipeByIdQuery = new RecipeGraphQLQuery.Builder()
                    .id("1")
                    .build();
            RecipeProjectionRoot projection = new RecipeProjectionRoot()
                    .id()
                    .description()
                    .difficulty()
                    .parent()
                    .cookTime()
                    .prepTime();
            GraphQLQueryRequest request = new GraphQLQueryRequest(recipeByIdQuery, projection);
//            DefaultGraphQLClient client = new DefaultGraphQLClient(baseUrl);

            var categoryByNameQuery = new CategoryByNameGraphQLQuery.Builder()
                    .name("Fast Food")
                    .build();
            CategoryProjectionRoot categoryProjection = new CategoryProjectionRoot()
                    .id()
                    .name()
                    .recipes()
                    .parent();

            var categoriesQuery = new CategoriesGraphQLQuery.Builder().build();
            CategoriesProjectionRoot categoriesProjection = new CategoriesProjectionRoot().id().name();
            GraphQLQueryRequest csreq = new GraphQLQueryRequest(categoriesQuery, categoriesProjection);
            GraphQLResponse csres = blockingClient.executeQuery(csreq.serialize());
            List<Category> categories = csres.extractValueAsObject("data.categories", new TypeRef<List<Category>>() { });

            GraphQLResponse response = blockingClient.executeQuery(request.serialize());
//            Recipe recipe = response.dataAsObject(Recipe.class); // response with wrapper - $.data.recipe
            Recipe recipe = response.extractValueAsObject("data.recipe", Recipe.class); // response with wrapper - $.data.recipe

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode data = objectMapper.reader().readValue(response.getJson(), JsonNode.class);
            String recipeJson = data.path("data").path("recipe").toString();
            Recipe recipe2 = objectMapper.readerFor(Recipe.class).readValue(recipeJson);
        };
    }


}
