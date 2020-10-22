package com.murano500k.coldwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.murano500k.coldwallet.ChildItem
import com.murano500k.coldwallet.ExpandableHeaderItem
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.StatsItem
import com.murano500k.coldwallet.net.utils.Status
import com.murano500k.coldwallet.viewmodel.StatsViewModel
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.android.synthetic.main.fragment_stats.*
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
@FragmentScoped
class StatsFragment : Fragment() {

    private val statsViewModel: StatsViewModel by viewModels()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val childList = listOf(
        ChildItem("First Album", false),
        ChildItem("Second Album", false),
        ChildItem("Third Album", false),
        ChildItem("Fourth Album", false)

    )

    private val parentList = listOf(
        ExpandableHeaderItem("Travis Scott"),
        ExpandableHeaderItem("Migos"),
        ExpandableHeaderItem("Post Malone"),
        ExpandableHeaderItem("Drake")

    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewStats.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
        setupObserver()
        statsViewModel.fetchStats()
    }

    private fun setupObserver() {
        statsViewModel.getStats().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { stats -> initList(stats) }
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initList(stats: List<StatsItem>) {
        for (parentItem in stats){
            val parentTitle = createParentTitle(parentItem.baseCurrency, parentItem.total)
            val group = ExpandableGroup(ExpandableHeaderItem(parentTitle))
            for (child  in parentItem.rows) {
                group.add(ChildItem(child.rowText, child.isCrypto))
            }
            groupAdapter.add(group)
        }
    }

    private fun createParentTitle(baseCurrency: String, total: BigDecimal): String {
        val scaledTotal  = total.setScale(4, RoundingMode.CEILING)

        return "All in $baseCurrency, total ${scaledTotal}"
    }

}