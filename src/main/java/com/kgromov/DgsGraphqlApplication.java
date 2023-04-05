package com.kgromov;

import com.kgromov.graphql.dgs.client.RecipeGraphQLQuery;
import com.kgromov.graphql.dgs.client.RecipeProjectionRoot;
import com.kgromov.graphql.dgs.types.Recipe;
import com.kgromov.service.DataService;
import com.netflix.graphql.dgs.client.*;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

            var query = new RecipeGraphQLQuery.Builder()
                    .id("1")
                    .build();
            RecipeProjectionRoot projection = new RecipeProjectionRoot()
                    .id()
                    .description()
                    .difficulty()
                    .parent()
                    .cookTime()
                    .prepTime();
            GraphQLQueryRequest request = new GraphQLQueryRequest(query, projection);
//            DefaultGraphQLClient client = new DefaultGraphQLClient(baseUrl);
            GraphQLResponse response = blockingClient.executeQuery(request.serialize());
            Recipe recipe = response.dataAsObject(Recipe.class);
            String responseJson = response.getJson();
        };
    }


}
