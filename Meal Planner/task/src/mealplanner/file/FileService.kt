package mealplanner.file

import java.io.File

object FileService {


    fun writeShoppingList(fileName: String, shoppingList: String){
        File(fileName).writeText(shoppingList)
    }
}