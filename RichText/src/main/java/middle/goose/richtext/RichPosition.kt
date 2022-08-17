package middle.goose.richtext

data class RichPosition (
    val start: Int = 0,
    val end: Int = 0
){
    fun isValid(): Boolean {
        return start < end
    }
}