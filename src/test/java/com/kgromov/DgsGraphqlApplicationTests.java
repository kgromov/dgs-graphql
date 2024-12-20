package com.kgromov;

import com.kgromov.graphql.dgs.client.RecipeGraphQLQuery;
import com.kgromov.graphql.dgs.client.RecipeProjectionRoot;
import com.kgromov.graphql.dgs.types.Recipe;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest
@AutoConfigureGraphQlTester
class DgsGraphqlApplicationTests {

	@Autowired
	private GraphQlTester graphQlTester;

	@Test
	void checkRecipeByIdtAccount() {
		var queryRequest = new GraphQLQueryRequest(
				RecipeGraphQLQuery.newRequest().build(),
				new RecipeProjectionRoot().id().notes().category()
		);
		var recipeById = this.graphQlTester
				.document(queryRequest.serialize())
				.execute()
				.path("data").path("recipe")
				.entity(Recipe.class)
				.get();
	}
}
