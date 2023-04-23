package com.example.recipes

import androidx.loader.content.Loader
import com.example.recipes.Recipe
import java.io.InputStream

class RecipeLoader {
    fun readCsv(inputStream: InputStream): List<Recipe> {
        val reader = inputStream.bufferedReader()
        val header = reader.readLine()
        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (id, title, ingredients, directions) = it.split(";", ignoreCase = false, limit = 3)
                Recipe(
                    id.trim().toInt(),
                    title.trim().removeSurrounding("\""),
                    ingredients.trim().removeSurrounding("\""),
                    directions.trim().removeSurrounding("\""),
                )
            }.toList()
    }


}