package little.goose.account.ui.account.analysis.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import little.goose.account.ui.account.analysis.AccountAnalysisFragment

class AnalysisFragmentPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            //year
            AccountAnalysisFragment.newInstance(position)
        } else {
            //month
            AccountAnalysisFragment.newInstance(position)
        }
    }
}