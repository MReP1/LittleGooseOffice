package little.goose.resource

import account.shared.resource.generated.resources.Res
import account.shared.resource.generated.resources.cancel
import account.shared.resource.generated.resources.confirm
import account.shared.resource.generated.resources.confirm_delete
import account.shared.resource.generated.resources.delete
import account.shared.resource.generated.resources.delete_description
import account.shared.resource.generated.resources.deleted
import account.shared.resource.generated.resources.ic_little_goose
import account.shared.resource.generated.resources.ic_little_goose_back
import account.shared.resource.generated.resources.ic_little_goose_fore
import account.shared.resource.generated.resources.icon_format_h1
import account.shared.resource.generated.resources.icon_format_h2
import account.shared.resource.generated.resources.icon_format_h3
import account.shared.resource.generated.resources.icon_format_h4
import account.shared.resource.generated.resources.icon_format_h5
import account.shared.resource.generated.resources.icon_format_h6
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Suppress("ClassName")
@ExperimentalResourceApi
object GooseRes {

    object string {
        val confirm_delete get() = Res.string.confirm_delete
        val cancel get() = Res.string.cancel
        val confirm get() = Res.string.confirm
        val delete get() = Res.string.delete
        val delete_description get() = Res.string.delete_description
        val deleted get() = Res.string.deleted
    }

    object drawable {
        val icon_format_h1 get() = Res.drawable.icon_format_h1
        val icon_format_h2 get() = Res.drawable.icon_format_h2
        val icon_format_h3 get() = Res.drawable.icon_format_h3
        val icon_format_h4 get() = Res.drawable.icon_format_h4
        val icon_format_h5 get() = Res.drawable.icon_format_h5
        val icon_format_h6 get() = Res.drawable.icon_format_h6

        val ic_little_goose get() = Res.drawable.ic_little_goose
        val ic_little_goose_back get() = Res.drawable.ic_little_goose_back
        val ic_little_goose_fore get() = Res.drawable.ic_little_goose_fore
    }

}