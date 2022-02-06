package com.radlsuttonedmn.retireplan

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper


class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback {

    private val mActionType: String
    private var budgetAdapter: BudgetAdapter? = null
    private var lifeExpectanciesAdapter: LifeExpectanciesAdapter? = null
    private var portfolioAdapter: PortfolioAdapter? = null
    private var socialSecurityAdapter: SocialSecurityAdapter? = null

    // Constructor for the budget adapter
    constructor(adapter: BudgetAdapter) {
        budgetAdapter = adapter
        mActionType = "delete_budget_item"
    }

    // Constructor for the life expectancies adapter
    constructor(adapter: LifeExpectanciesAdapter) {
        lifeExpectanciesAdapter = adapter
        mActionType = "delete_expectancy"
    }

    // Constructor for the portfolio adapter
    constructor(adapter: PortfolioAdapter) {
        portfolioAdapter = adapter
        mActionType = "delete_balance"
    }

    // Constructor for the social security adapter
    constructor(adapter: SocialSecurityAdapter) {
        socialSecurityAdapter = adapter
        mActionType = "delete_earnings"
    }

    // Disable long press drag
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    // Enable swiping right and left
    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    // Override the parent class's getMovementFlags method
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    // Override the parent class's onMove method
    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    // Execute the calling adapter's onItemDismiss method when a swipe occurs
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (mActionType) {
            "delete_budget_item" -> budgetAdapter!!.onItemDismiss(viewHolder.adapterPosition)
            "delete_expectancy" -> lifeExpectanciesAdapter!!.onItemDismiss(viewHolder.adapterPosition)
            "delete_balance" -> portfolioAdapter!!.onItemDismiss(viewHolder.adapterPosition)
            "delete_earnings" -> socialSecurityAdapter!!.onItemDismiss(viewHolder.adapterPosition)
        }
    }
}
