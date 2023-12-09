package little.goose.shared.common

import java.util.UUID

actual fun generateUUIDString(): String {
    return UUID.randomUUID().toString()
}