package little.goose.resource

import account.shared.resource.generated.resources.Res
import account.shared.resource.generated.resources.cancel
import account.shared.resource.generated.resources.confirm
import account.shared.resource.generated.resources.confirm_delete
import account.shared.resource.generated.resources.delete
import account.shared.resource.generated.resources.delete_description
import account.shared.resource.generated.resources.deleted
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Suppress("ClassName")
@ExperimentalResourceApi
object GooseRes {

    object strings {
        val confirm_delete get() = Res.string.confirm_delete
        val cancel get() = Res.string.cancel
        val confirm get() = Res.string.confirm
        val delete get() = Res.string.delete
        val delete_description get() = Res.string.delete_description
        val deleted get() = Res.string.deleted
    }

}