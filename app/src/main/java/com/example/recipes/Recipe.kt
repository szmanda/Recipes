package com.example.recipes

import android.content.ContentValues

data class Recipe(
    val id: Int = 0,
    val title: String = "Plceholder",
    val ingredients: String = "ingredients",
    val directions: String = "directions",
) {
    public fun toContentValues(): ContentValues {
        val values = ContentValues()
        values.put(RecipesDb.C_ID, id)
        values.put(RecipesDb.C_TITLE, title)
        values.put(RecipesDb.C_INGREDIENTS, ingredients)
        values.put(RecipesDb.C_DIRECTIONS, directions)
        return values
    }
}

