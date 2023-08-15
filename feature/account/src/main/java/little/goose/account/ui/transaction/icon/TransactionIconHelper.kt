package little.goose.account.ui.transaction.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import little.goose.account.R
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.models.TransactionIcon
import little.goose.design.system.util.Icon

object TransactionIconHelper {

    val icons = mapOf(
        1 to Icon.Drawable(R.drawable.icon_eat),
        2 to Icon.Drawable(R.drawable.icon_shopping),
        3 to Icon.Drawable(R.drawable.icon_car),
        4 to Icon.Drawable(R.drawable.icon_book),
        5 to Icon.Drawable(R.drawable.icon_house),
        6 to Icon.Drawable(R.drawable.icon_game),
        7 to Icon.Drawable(R.drawable.icon_clothe),
        8 to Icon.Drawable(R.drawable.icon_make_up),
        9 to Icon.Drawable(R.drawable.icon_gift),
        10 to Icon.Drawable(R.drawable.icon_camera),
        11 to Icon.Drawable(R.drawable.icon_credit_card),
        12 to Icon.Drawable(R.drawable.icon_red_packet),
        13 to Icon.Drawable(R.drawable.icon_money),
        14 to Icon.Drawable(R.drawable.icon_medical),
        15 to Icon.Drawable(R.drawable.icon_phone_charge),
        16 to Icon.Drawable(R.drawable.icon_router),
        17 to Icon.Drawable(R.drawable.icon_give_reword),
        18 to Icon.Drawable(R.drawable.icon_stock),
        19 to Icon.Drawable(R.drawable.icon_stock),
        20 to Icon.Drawable(R.drawable.icon_fastfood),
        21 to Icon.Drawable(R.drawable.icon_blender),
        22 to Icon.Drawable(R.drawable.icon_cut),
        23 to Icon.Drawable(R.drawable.icon_red_packet),
        24 to Icon.Vector(Icons.Rounded.Pets)
    )

    private val iconMap = mapOf(
        1 to TransactionIcon(1, EXPENSE, "饮食", 1),
        2 to TransactionIcon(2, EXPENSE, "购物", 2),
        3 to TransactionIcon(3, EXPENSE, "交通", 3),
        4 to TransactionIcon(4, EXPENSE, "学习", 4),
        5 to TransactionIcon(5, EXPENSE, "房租", 5),
        6 to TransactionIcon(6, EXPENSE, "娱乐", 6),
        7 to TransactionIcon(7, EXPENSE, "服饰", 7),
        8 to TransactionIcon(8, EXPENSE, "化妆", 8),
        9 to TransactionIcon(9, EXPENSE, "礼物", 9),
        10 to TransactionIcon(10, EXPENSE, "数码", 10),
        11 to TransactionIcon(11, INCOME, "工资", 11),
        12 to TransactionIcon(12, INCOME, "红包", 12),
        13 to TransactionIcon(13, INCOME, "其他", 13),
        14 to TransactionIcon(14, EXPENSE, "医疗", 14),
        15 to TransactionIcon(15, EXPENSE, "话费", 15),
        16 to TransactionIcon(16, EXPENSE, "网费", 16),
        17 to TransactionIcon(17, EXPENSE, "氪金", 17),
        18 to TransactionIcon(18, INCOME, "理财", 18),
        19 to TransactionIcon(19, EXPENSE, "理财", 19),
        20 to TransactionIcon(20, EXPENSE, "零食", 20),
        21 to TransactionIcon(21, EXPENSE, "日常", 21),
        22 to TransactionIcon(22, EXPENSE, "理发", 22),
        23 to TransactionIcon(23, EXPENSE, "红包", 23),
        24 to TransactionIcon(24, EXPENSE, "宠物", 24),
    )

    val expenseIconList = iconMap.values.filter { it.type == EXPENSE }
    val incomeIconList = iconMap.values.filter { it.type == INCOME }

    fun getIcon(iconId: Int): Icon {
        val icon = iconMap[iconId]
        val iconResId = icon?.iconResId ?: 1
        return icons[iconResId] ?: Icon.Drawable(R.drawable.icon_money)
    }

}