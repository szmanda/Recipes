package com.example.recipes

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream

object RecipesDb {
    const val TABLE_NAME = "recipes"
    const val C_ID = "id"
    const val C_TITLE = "title"
    const val C_INGREDIENTS = "ingredients"
    const val C_DIRECTIONS = "directions"
}

private const val SQL_CREATE_RECIPES =
    "CREATE TABLE ${RecipesDb.TABLE_NAME} (" +
            "${RecipesDb.C_ID} INTEGER PRIMARY KEY," +
            "${RecipesDb.C_TITLE} TEXT," +
            "${RecipesDb.C_INGREDIENTS} TEXT," +
            "${RecipesDb.C_DIRECTIONS} TEXT)"
private const val SQL_DELETE_RECIPES = "DROP TABLE IF EXISTS ${RecipesDb.TABLE_NAME}"

class RecipesDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    fun readCsv(inputStream: InputStream): List<Recipe> {
        println("Reading CSV")
        val reader = inputStream.bufferedReader()
        val header = reader.readLine()
        val list: List<Recipe> = reader.lineSequence().filter{ it.isNotBlank() }.map {
            val (id, title, ingredients, directions, remaining_fields) = it.split(";", ignoreCase = false, limit = 5)
            Recipe(
                id.trim().toInt(),
                title.trim().removeSurrounding("\""),
                ingredients.trim().removeSurrounding("\""),
                directions.trim().removeSurrounding("\""),
            )
        }.toList()
        return list
    }

    fun import(context: Context) {
        println("Importing recipes from csv")
        val inputStream = context.assets.open("recipes999.csv")
        val db = writableDatabase
        val recipes = readCsv(inputStream)
        recipes.forEach {
            db.insertWithOnConflict(
                RecipesDb.TABLE_NAME,
                null,
                it.toContentValues(),
                CONFLICT_REPLACE
            )
        }
        inputStream.close()
        println("Imported recipes")
    }
    
    fun getRecipes() : List<Recipe>{
        val list = ArrayList<Recipe>();
        val db = readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM " + RecipesDb.TABLE_NAME, null)
        println("Cursor: $cursor")
        
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(
                        Recipe(
                            cursor.getInt(cursor.getColumnIndex(RecipesDb.C_ID)),
                            cursor.getString(cursor.getColumnIndex(RecipesDb.C_TITLE)),
                            cursor.getString(cursor.getColumnIndex(RecipesDb.C_INGREDIENTS)),
                            cursor.getString(cursor.getColumnIndex(RecipesDb.C_DIRECTIONS)),
                        )
                    )
                    // println("Added recipe ${cursor.getString(cursor.getColumnIndex(RecipesDb.C_TITLE))}")
                } while (cursor.moveToNext())
            }
        }
        return list.toList()
    }

    fun getRecipe(id: Int) : Recipe {
        val db = readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM " + RecipesDb.TABLE_NAME + " WHERE " + RecipesDb.C_ID + " = " + id, null)
        println("Cursor: $cursor")
        cursor?.moveToFirst()
        if (cursor != null) {
            return Recipe(
                cursor.getInt(cursor.getColumnIndex(RecipesDb.C_ID)),
                cursor.getString(cursor.getColumnIndex(RecipesDb.C_TITLE)),
                cursor.getString(cursor.getColumnIndex(RecipesDb.C_INGREDIENTS)),
                cursor.getString(cursor.getColumnIndex(RecipesDb.C_DIRECTIONS)),
            )
        }
        return Recipe();
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_RECIPES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_RECIPES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "Recipes.db"
    }
}