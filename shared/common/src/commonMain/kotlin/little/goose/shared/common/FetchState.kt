package little.goose.shared.common

sealed class FetchState<out T> {

    data object Loading : FetchState<Nothing>()

    data class Success<T>(val data: T) : FetchState<T>()

    data class Failure(val exception: Throwable) : FetchState<Nothing>()

    inline fun <N> foldState(
        failureMapper: (exc: Throwable) -> FetchState<N> = { Failure(it) },
        loadingMapper: () -> FetchState<N> = { Loading },
        successMapper: (T) -> FetchState<N>
    ): FetchState<N> {
        return when (this) {
            is Failure -> failureMapper(this.exception)
            Loading -> loadingMapper()
            is Success -> successMapper(this.data)
        }

    }

}