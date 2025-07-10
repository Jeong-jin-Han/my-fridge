package com.example.my_fridge.myfoodmergedata

import com.example.my_fridge.myfooddata.MyFoodData
import com.example.my_fridge.myfooddata.MyFoodDto
import com.example.my_fridge.myfoodmembercheckeddata.MyFoodCheckedMemberData

object MyFoodMergeData {
    // 병합 결과 저장용 (필요하면 캐싱)
    private var mergedList: List<MyFoodDto> = emptyList()

    fun updateMergedList() {
        // 나의 데이터 포함
        val myList = MyFoodData.getMyFoodDataAllItems()
        // 친구들 체크된 데이터
        val friendList = MyFoodCheckedMemberData.getMergedFoodList()

        // 병합 (중복 제거)
        mergedList = (myList + friendList).distinctBy { it.foodId }

        android.util.Log.d("MyFoodMerge", "병합 완료: 내 ${myList.size}개 + 친구 ${friendList.size}개 → 총 ${mergedList.size}개")
    }

//    fun getMergedList(): List<MyFoodDto> = mergedList
    fun getMergedList(): List<MyFoodDto> {
        updateMergedList()
        return mergedList
    }
}