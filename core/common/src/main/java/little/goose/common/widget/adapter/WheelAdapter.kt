package little.goose.common.widget.adapter


/**
 * Created by wanglu on 3/28/18.
 */
interface WheelAdapter {
    fun getValue(position: Int): String
    fun getPosition(value : String): Int
    /**
     * get the text with potential maximum print length for support "WRAP_CONTENT" attribute
     * if not sure, return empty("") string, in that case "WRAP_CONTENT" will behavior like "MATCH_PARENT"
     * 用来测量长度以支持WRAP_CONTENT属性, 若为"", WRAP_CONTENT就会像MATCH_PARENT
     */
    fun getTextWithMaximumLength(): String
    fun getMaxIndex() : Int
    fun getMinIndex() : Int
    fun getSize(): Int
}