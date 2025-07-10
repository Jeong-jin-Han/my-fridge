package com.example.my_fridge.myfoodnotificationdata

import com.example.my_fridge.foodnotification.FoodNotificationType

data class FoodNotificationDto(
    val id : Int,
    val type :FoodNotificationType,
    val message : String,
    val imgPath: Int,
)
