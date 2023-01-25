package little.goose.account.utils

inline fun<reified T> Any?.toTypeOr(fallback: T): T = this as? T ?: fallback