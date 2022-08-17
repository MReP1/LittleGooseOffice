package little.goose.account.common

interface ItemSelectCallback<T> {
    fun onItemClick(item: T)
    fun onItemLongClick(item: T)
}

interface ItemClickCallback<T> {
    fun onItemClick(item: T)
}