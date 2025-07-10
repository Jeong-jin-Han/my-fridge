package com.example.my_fridge.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.my_fridge.R
import com.example.my_fridge.notification.NotificationFragment
import com.example.my_fridge.sampledata.GalleryData.getGalleryDataList
import com.example.my_fridge.sampledata.GalleryGroupData
import com.example.my_fridge.sampledata.GalleryGroupDto
import com.example.my_fridge.sampledata.NotificationData
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.my_fridge.foodmap.FoodMapAdapter
import com.example.my_fridge.myfoodmembercheckeddata.MyFoodCheckedMemberData
import com.example.my_fridge.myfoodmergedata.MyFoodMergeData
import com.example.my_fridge.myfoodpage.MyFoodpageFragment
import com.example.my_fridge.sampledata.GalleryDto

import com.example.my_fridge.myfoodmemberpage.MyFoodMemberpageFragment
import android.util.Log
import com.example.my_fridge.foodmap.FoodMapFragment
import com.example.my_fridge.myfoodmemberdata.MyFoodMemberData
import com.example.my_fridge.myfoodmemberdata.MyFoodMemberDto
import com.example.my_fridge.sampledata.GalleryData

@Suppress("DEPRECATION")

class GalleryFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 101
    private val CAMERA_PERMISSION_CODE = 103
    private val REQUEST_IMAGE_PICK = 102
    private var galleryAdapter: GalleryAdapter? = null
    private var imageFilePath: String? = null

    private var selectedCategory: String = "한식"

    private lateinit var cartAdapter: FoodMapAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 전달받은 category 가져오기 (예: "일식&중식")
        arguments?.getString("category")?.let {
            selectedCategory = it
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val recyclerView1: RecyclerView = view.findViewById(R.id.recycler_view1)
        val galleryDataList1: List<GalleryGroupDto> = GalleryGroupData.getGalleryGroupDataList()
        val topBarTextView = view.findViewById<TextView>(R.id.top_bar_text_ver2)
        topBarTextView.text = "추천 요리"

        val cameraButton = view.findViewById<Button>(R.id.fixed_button)

        val searchButton = view.findViewById<ImageView>(R.id.top_bar_search_ver2)
        searchButton.visibility = View.GONE


        val cartButton = view.findViewById<ImageView>(R.id.top_bar_cart_ver2)
        val cartPopup = view.findViewById<CardView>(R.id.cartPopupCard)

        cartButton.visibility = View.VISIBLE
        cartButton.setOnClickListener {
            cartPopup.visibility = if (cartPopup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // ✅ 여기서 RecyclerView + Adapter 설정
        val cartrecyclerView = view.findViewById<RecyclerView>(R.id.cartRecyclerView)
//        val cartItems = listOf("계란", "우유", "양파") // 예시 데이터

//        val foodmemberDataList = MyFoodMemberList
        val foodmemberDataList = MyFoodCheckedMemberData.getCheckedFoodMembers()

        cartAdapter = FoodMapAdapter(
            foodmemberDataList,
            context = requireContext(),
            onFridgeClick = {
                    id ->
//                // ✅ MyFoodpageFragment로 전환
//                requireActivity().supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(
//                        R.anim.phone_slide_in_right,
//                        R.anim.phone_slide_out_left,
//                        R.anim.phone_slide_in_left,
//                        R.anim.phone_slide_out_right
//                    )
//                    .replace(R.id.content_frame_ver2, MyFoodpageFragment())
//                    .addToBackStack(null)
//                    .commit()
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()

                val newFragment = MyFoodMemberpageFragment().apply {
                    arguments = Bundle().apply {
                        putInt("memberId", id)
                    }
                }

                transaction.setCustomAnimations(
                    R.anim.phone_slide_in_right,
                    R.anim.phone_slide_out_left,
                    R.anim.phone_slide_in_left,
                    R.anim.phone_slide_out_right
                )

                transaction
                    .hide(this@GalleryFragment) // 현재 FoodMapFragment 숨김
                    .add(R.id.content_frame_ver2, newFragment) // 새 Fragment 추가
                    .addToBackStack(null)
                    .commit()
            },
            onLocationClick = {
                    id ->
                val MyFoodMemberList: List<MyFoodMemberDto> = MyFoodMemberData.getAllMyFoodMembers()
                val member = MyFoodMemberList.find { it.memberId == id }

                if(member != null) {
                    val fragment = FoodMapFragment().apply {
                        arguments = Bundle().apply {
                            putDouble("lat", member.lat)
                            putDouble("lng", member.lng)
                            putInt("memberId", member.memberId)
                        }
                    }

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame_ver2, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            },
            onCheckClick = { memberId ->
                val updatedList = MyFoodCheckedMemberData.getCheckedFoodMembers()
                Log.d("CartUpdate", "Checked members: ${updatedList.map { it.name }}")
                cartAdapter.updateData(updatedList)
                cartAdapter.notifyDataSetChanged() // Adapter에 변경된 사항을 알리고 UI 업데이트

                // groupId를 어떻게 ㅎ
//                val groupId = getSelectedGroupId() // 사용자가 선택한 그룹 ID를 가져오는 함수
                galleryAdapter?.updateData(0)
            }
        )

        cartrecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        cartrecyclerView.adapter = cartAdapter


        val layoutManager = StaggeredGridLayoutManager(
            2, StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.layoutManager = layoutManager
        val notificationNumber = view.findViewById<TextView>(R.id.notificationNumber_ver2)
        notificationNumber.text = NotificationData.getCheckdNotificationDataList(requireContext()).toString()
        val notificationButton = view.findViewById<ImageView>(R.id.top_bar_bell_ver2)
        notificationButton.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.phone_slide_in_right, // 새로운 Fragment가 오른쪽에서 들어오는 애니메이션
                    R.anim.phone_slide_out_left, // 기존 Fragment가 왼쪽으로 밀리는 애니메이션
                    R.anim.phone_slide_in_left,  // 뒤로가기 시 기존 Fragment가 왼쪽에서 들어오는 애니메이션
                    R.anim.phone_slide_out_right
                )
                .replace(R.id.content_frame_ver2, NotificationFragment())
                .addToBackStack(null)
                .commit()
        }

        val mypageButton = view.findViewById<ImageView>(R.id.top_bar_person_ver2)
        mypageButton.setOnClickListener {

            val fragment = MyFoodpageFragment().apply {
                arguments = Bundle().apply {
                    putString("source", "gallery")
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.phone_slide_in_right, // 새로운 Fragment가 오른쪽에서 들어오는 애니메이션
                    R.anim.phone_slide_out_left, // 기존 Fragment가 왼쪽으로 밀리는 애니메이션
                    R.anim.phone_slide_in_left,  // 뒤로가기 시 기존 Fragment가 왼쪽에서 들어오는 애니메이션
                    R.anim.phone_slide_out_right
                )
                .replace(R.id.content_frame_ver2, fragment)
                .addToBackStack(null)
                .commit()
        }

        // 2. 해당 카테고리로 필터링
        val filteredGalleryList = GalleryData.getGalleryDataList()
            .filter { it.date == selectedCategory }



//        dataList = getGalleryDataList().filter { it.date == "한식" }.toMutableList()
        galleryAdapter = GalleryAdapter(context = requireContext(),
            dataList = filteredGalleryList.toMutableList()) { id, sharedView ->
//
//            val mergedFridge = MyFoodMergeData.getMergedList().map { it.foodId }.toSet()
//            val sortedList = getGalleryDataList()
//                .filter { it.date == "한식" }
//                .sortedWith(compareByDescending<GalleryDto> { dto ->
////                    dto.ingredients.count { it in fridge }
//                    dto.ingredients.count { it in mergedFridge }
//
//                }.thenBy { dto ->
////                    dto.ingredients.count { it !in fridge }
//                    dto.ingredients.count { it !in mergedFridge }
//                }
//                )
//                .map{it.id}

            // 전체 정렬 (추천 순서)
            val mergedFridge = MyFoodMergeData.getMergedList().map { it.foodId }.toSet()
            val sortedList = getGalleryDataList()
                .sortedWith(
                    compareByDescending<GalleryDto> { dto ->
                        dto.ingredients.count { it in mergedFridge }
                    }.thenBy { dto ->
                        dto.ingredients.count { it !in mergedFridge }
                    }
                )
                .map { it.id }

            val fragment = GalleryDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("id", id)
                    putIntegerArrayList("sortedList", ArrayList(sortedList))  // 👉 리스트 같이 넘기기 (GalleryDto는 Parcelable 필요)
                    putString("category", selectedCategory) // ✅ 여기서 다시 전달
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .addSharedElement(sharedView, "shared_image_transition")
                .replace(R.id.content_frame_ver2, fragment, "galleryFragment")
                .addToBackStack("galleryFragment")
                .commit()
        }


//        recyclerView.adapter = galleryAdapter
//        galleryAdapter?.updateData(1)

        val groupData = GalleryGroupData.getGalleryGroupDataList()

// 현재 selectedCategory 값을 기반으로 groupId 찾기
        val groupId = groupData.find { it.title == selectedCategory }?.memberId ?: 1  // 기본값은 1 ("한식")

// RecyclerView 세팅 후 업데이트
        recyclerView.adapter = galleryAdapter
        galleryAdapter?.updateData(groupId)


        cameraButton.setOnClickListener {
            showImageUploadOptions()
        }

        recyclerView1.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView1.adapter = GalleryGroupAdapter(galleryDataList1) { id ->
            // 🟡 카테고리 이름 가져오기
            val groupData = GalleryGroupData.getGalleryGroupDataList()
            val categoryTitle = groupData.find { it.memberId == id }?.title ?: "한식"

            // ✅ 선택된 카테고리 반영
            selectedCategory = categoryTitle
            galleryAdapter?.updateData(id)
        }
    }

    private fun showImageUploadOptions() {
        val options = arrayOf("사진 촬영", "갤러리에서 업로드")
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("이미지 업로드")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> checkCameraPermission() // 사진 촬영
                1 -> openGallery()          // 갤러리에서 업로드
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        photoFile?.let {
            val photoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", it)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    imageFilePath?.let {
                        openGalleryUploadFragment(it, "Captured Photo", "Camera Upload")
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    data?.data?.let { uri ->
                        openGalleryUploadFragment(uri.toString(), "Captured Photo", "Camera Upload")
                    }
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir).apply {
            imageFilePath = absolutePath
        }
    }

    private fun openGalleryUploadFragment(imagePath: String, title: String, abstract: String) {
        val fragment = GalleryUploadFragment(galleryAdapter).apply {
            arguments = Bundle().apply {
                putString("imagePath", imagePath)
                putString("title", title)
                putString("abstract", abstract)
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame_ver2, fragment)
            .addToBackStack(null)
            .commit()
    }
}

