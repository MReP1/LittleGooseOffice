sealed class LocalDataResult<out T> {

    data object Loading : LocalDataResult<Nothing>()

    data class Data<T>(val data: T) : LocalDataResult<T>()

    data class Failure(val exc: Throwable) : LocalDataResult<Nothing>()

}