package com.example.fetchitemdisplay.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fetchitemdisplay.api.FetchAPI
import com.example.fetchitemdisplay.models.DisplayItem
import com.example.fetchitemdisplay.models.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel: ViewModel() {
    private val fetchAPI = FetchAPI()

    private var items = ArrayList<Item>()
    val displayItems = MutableLiveData<ArrayList<DisplayItem>>()
    var withNullsAndEmptyStrings = MutableLiveData<Boolean>()

    init {
        withNullsAndEmptyStrings.value = true
        displayItems.value = ArrayList()
    }

    fun retrieveData() {
        // reduce the number of unnecessary calls to get the data by first checking to see if the list is empty or not
        if (items.isEmpty()) {
            CoroutineScope(IO).launch {
                items = fetchAPI.getItemListFromAPI()
                items = items.sortedWith(compareBy<Item> { it.listId }.thenBy { it.name })
                    .toCollection(ArrayList())

                withContext(Main) {
                    addItemsToDisplay()
                }
            }
        }
        else {
            addItemsToDisplay()
        }
    }

    private fun addItemsToDisplay() {
        // this function inserts special rows that are just for the listId, so that the items can be grouped on the UI by listId

        val newDisplayItems = ArrayList<DisplayItem>()
        newDisplayItems.clear()
        var listId: Long? = null
        for (item in items) {
            // will not add null or blanks names if the filtering is on
            if (!withNullsAndEmptyStrings.value!! && item.name.isNullOrBlank()) {
                continue
            }
            if (listId == null || listId != item.listId) {
                listId = item.listId
                newDisplayItems.add(DisplayItem(listId, null))
            }
            newDisplayItems.add(DisplayItem(null, item))
        }

        displayItems.value = newDisplayItems
    }

    fun toggleWithNullsAndEmptyStrings() {
        withNullsAndEmptyStrings.value = !withNullsAndEmptyStrings.value!!
        retrieveData()
    }
}