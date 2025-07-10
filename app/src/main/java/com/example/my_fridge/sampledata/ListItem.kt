import com.example.my_fridge.sampledata.MemberDto

sealed class ListItem {
    data class Header(val title: String) : ListItem()
    data class Contact(val member: MemberDto, val qualification: String) : ListItem()
}