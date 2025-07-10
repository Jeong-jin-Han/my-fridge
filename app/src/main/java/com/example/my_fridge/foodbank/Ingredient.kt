package com.example.my_fridge.foodbank

data class Ingredient(
    val foodId: Int,
    val name: String,
    val category: String,
    val storage: String,
    val image: String
)