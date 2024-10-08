package com.example.demo.services;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CosmosDbService {
    public static void addEntityToContext(String containerName, Map<String, Object> filterFields, Map<String, Object> context) {

        // MOCKED DATABASE
        // REMOVE THIS CODE AND DEPENDENCY INJECT CosmosDatabase IN REAL CASE ================================
        String cosmosUri = "https://qworks-workflow.documents.azure.com:443";
        String cosmosKey = "";
        String cosmosDatabase = "workflow_management";

        CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(cosmosUri)
                .key(cosmosKey)
                .buildClient();


        CosmosDatabase database = cosmosClient.getDatabase(cosmosDatabase);
        //==============================================

        CosmosContainer container = database.getContainer(containerName);

        // Build a dynamic SQL query based on filter fields
        List<String> conditions = new ArrayList<>();

        filterFields.forEach((key, value) -> {
            String condition = containerName + "." + key + " = '" + value + "'";
            conditions.add(condition);
        });

        String query = "SELECT * FROM " + containerName + " WHERE " + String.join(" AND ", conditions);

        // Create a parameterized query
        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

        CosmosPagedIterable<Object> results = container.queryItems(query, options, Object.class);
        Optional<Object> res = results.stream().findFirst();

        if (res.isEmpty()) {
            return;
        }

        context.put(containerName, res.get());

        // MOCKED DATABASE
        // REMOVE THIS CODE IN REAL CASE ================================
        cosmosClient.close();
        // =======================================================================
    }
}
