package little.goose.common

interface ItemSelectCallback<T> {
    fun onItemClick(item: T)
    fun onItemLongClick(item: T)
}

