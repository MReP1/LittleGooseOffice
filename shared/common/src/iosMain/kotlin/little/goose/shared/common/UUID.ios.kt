package little.goose.shared.common

import platform.Foundation.NSUUID

actual fun generateUUIDString(): String {
    return NSUUID().UUIDString()
}