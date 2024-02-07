package little.goose.shared.common

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getCurrentTimeMillis(): Long {
    return NSDate().timeIntervalSince1970.toLong() * 1000
}