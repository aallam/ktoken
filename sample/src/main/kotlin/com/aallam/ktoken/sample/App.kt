package com.aallam.ktoken.sample

import com.aallam.ktoken.Tokenizer
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import org.jetbrains.kotlinx.dataframe.size

fun main() {
    val token = requireNotNull(System.getenv("OPENAI_API_KEY")) { "OPENAI_API_KEY environment variable must be set." }
    val embeddingModel = ModelId("text-embedding-ada-002")
    val maxTokens = 8000

    // load & inspect dataset
    var dataframe = DataFrame
        .readCSV("data/fine_food_reviews_1k.csv")
        .dropNA()
        .add("combined") { "Title: ${getValue<String>("Summary")}; Content: ${(getValue<String>("Text")).trim()}" }
    println(dataframe.head(2))

    // subsample to 1k most recent reviews and remove samples that are too long
    val topN = 1000
    dataframe = dataframe
        .sortBy("Time")
        .tail(topN * 2) // first cut to first 2k entries, assuming less than half will be filtered out
        .remove("Time")

    val tokenizer = runBlocking(Dispatchers.IO) { Tokenizer.of(embeddingModel.id) }
    dataframe = dataframe
            .add("n_tokens") { tokenizer.encode(getValue("combined")).size }
            .filter { it.getValue<Int>("n_tokens") <= maxTokens }
    println(dataframe.size())

    // get embeddings and save them for future reuse
    val openAI = OpenAI(token, logging = LoggingConfig(LogLevel.None))
    dataframe = dataframe.add("embedding") {
        val request = EmbeddingRequest(model = embeddingModel, input = listOf(getValue("combined")))
        val response = runBlocking(Dispatchers.IO) { openAI.embeddings(request) }
        response.embeddings[0].embedding
    }
    dataframe.writeCSV("data/fine_food_reviews_with_embeddings_1k.csv")
}
