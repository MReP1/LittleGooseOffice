package little.goose.account.utils

inline fun<reified T> Any?.toTypeOrNull(): T? = this as? T
inline fun<reified T> Any?.toTypeOr(fallback: T): T = this as? T ?: fallback
inline fun<reified T> Any?.toType(fallback: T): T = this as T