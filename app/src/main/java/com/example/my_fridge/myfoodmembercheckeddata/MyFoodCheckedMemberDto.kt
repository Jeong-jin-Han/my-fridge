package com.example.my_fridge.myfoodmembercheckeddata

import com.example.my_fridge.myfoodmemberdata.MyFoodMemberDto

data class MyFoodCheckedMemberDto(
    val foodmember: MyFoodMemberDto,
    val checked: Boolean
)
