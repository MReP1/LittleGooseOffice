package little.goose.account.ui.transaction.icon

import little.goose.account.R
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.models.TransactionIcon

object TransactionIconHelper {

    private val iconMap = mapOf(
        1 to TransactionIcon(1, EXPENSE, "饮食", R.drawable.icon_eat),
        2 to TransactionIcon(2, EXPENSE, "购物", R.drawable.icon_shopping),
        3 to TransactionIcon(3, EXPENSE, "交通", R.drawable.icon_car),
        4 to TransactionIcon(4, EXPENSE, "学习", R.drawable.icon_book),
        5 to TransactionIcon(5, EXPENSE, "房租", R.drawable.icon_house),
        6 to TransactionIcon(6, EXPENSE, "娱乐", R.drawable.icon_game),
        7 to TransactionIcon(7, EXPENSE, "服饰", R.drawable.icon_clothe),
        8 to TransactionIcon(8, EXPENSE, "化妆", R.drawable.icon_make_up),
        9 to TransactionIcon(9, EXPENSE, "礼物", R.drawable.icon_gift),
        10 to TransactionIcon(10, EXPENSE, "数码", R.drawable.icon_camera),
        11 to TransactionIcon(11, INCOME, "工资", R.drawable.icon_credit_card),
        12 to TransactionIcon(12, INCOME, "红包", R.drawable.icon_red_packet),
        13 to TransactionIcon(13, INCOME, "其他", R.drawable.icon_money),
        14 to TransactionIcon(14, EXPENSE, "医疗", R.drawable.icon_medical),
        15 to TransactionIcon(15, EXPENSE, "话费", R.drawable.icon_phone_charge),
        16 to TransactionIcon(16, EXPENSE, "网费", R.drawable.icon_router),
        17 to TransactionIcon(17, EXPENSE, "氪金", R.drawable.icon_give_reword),
        18 to TransactionIcon(18, INCOME, "理财", R.drawable.icon_stock),
        19 to TransactionIcon(19, EXPENSE, "理财", R.drawable.icon_stock),
        20 to TransactionIcon(20, EXPENSE, "零食", R.drawable.icon_fastfood),
        21 to TransactionIcon(21, EXPENSE, "日常", R.drawable.icon_blender),
        22 to TransactionIcon(22, EXPENSE, "理发", R.drawable.icon_cut),
        23 to TransactionIcon(23, EXPENSE, "红包", R.drawable.icon_red_packet),
    )

    val expenseIconList = iconMap.values.filter { it.type == EXPENSE }
    val incomeIconList = iconMap.values.filter { it.type == INCOME }

    fun getIconPath(iconId: Int): Int = iconMap[iconId]?.path ?: R.drawable.icon_money

}