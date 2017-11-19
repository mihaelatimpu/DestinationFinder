package com.mimi.destinationfinder.settings

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.base.BaseFragment
import com.mimi.destinationfinder.dto.Radius
import com.mimi.destinationfinder.dto.ResultsCount
import com.mimi.destinationfinder.dto.Settings
import com.mimi.destinationfinder.dto.TransportMode
import com.mimi.destinationfinder.utils.Context
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class SettingsFragment : BaseFragment(), SettingsContract.View {
    override var isActive: Boolean = false
        get() = isAdded
    override val contextName = Context.Settings
    override val presenter: SettingsContract.Presenter by inject()
    lateinit var adapter: PlaceTypeAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onStart() {
        super.onStart()
        presenter.view = this
        presenter.start()
    }

    override fun init() {
        placeTypesList.layoutManager = GridLayoutManager(context,
                2, GridLayoutManager.VERTICAL, false)
        val items = context.resources.getStringArray(R.array.place_types).toList()
        adapter = PlaceTypeAdapter(allItems = items, context = context)
        placeTypesList.adapter = adapter
        saveButton.setOnClickListener { presenter.saveSettings() }
    }

    override fun exit() {
        activity.finish()
    }

    override fun refreshSettings(settings: Settings) {
        radius_spinner.setSelection(settings.radius.type)
        transportModeSpinner.setSelection(settings.transportMode.type)
        maxResultsSpinner.setSelection(settings.maxResultPerCount.type)
        adapter.refreshSelectedItems(settings.placesOnInterests)
    }

    override fun collectSettings() =
            Settings(radius = Radius(radius_spinner.selectedItemPosition),
                    transportMode = TransportMode(transportModeSpinner.selectedItemPosition),
                    maxResultPerCount = ResultsCount(maxResultsSpinner.selectedItemPosition),
                    placesOnInterests = adapter.selectedItems)

}