package com.example.recipes

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.DragEvent
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.recipes.placeholder.PlaceholderContent
import com.example.recipes.RecipeLoader
import com.example.recipes.databinding.FragmentItemDetailBinding

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The placeholder content this fragment is presenting.
     */
    private var item: Recipe? = null
    lateinit private var dbHelper: RecipesDbHelper

    lateinit var itemDetailTextView: TextView
    private var itemDetailIngredientsTextView: TextView? = null
    private var itemDetailTitleTextView: TextView? = null
    private var toolbarLayout: CollapsingToolbarLayout? = null

    private var _binding: FragmentItemDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dragListener = View.OnDragListener { v, event ->
        if (event.action == DragEvent.ACTION_DROP) {
            val clipDataItem: ClipData.Item = event.clipData.getItemAt(0)
            val id = Integer.parseInt(clipDataItem.text.toString())
            item = getRecipe(id)
            updateContent()
        }
        true
    }

    fun getRecipe(id: Int) : Recipe? {
        item = Recipe(id);
        val context: Context? = getActivity()?.getApplicationContext()
        if (context != null) {
            val dbHelper: RecipesDbHelper = RecipesDbHelper(context);
            println("Asking for recipes")
            item = dbHelper.getRecipe(id)
        }
        return item
    }

    fun stringToArray(string: String) : List<String> {
        return string.split(Regex("\\['|'\\]|', '"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //supportLoaderManager.initLoader(0, null, this)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID) && it.getString(ARG_ITEM_ID) != null && it.getString(ARG_ITEM_ID) != "") {
                item = getRecipe(it.getString(ARG_ITEM_ID)!!.toInt())
            }
        }
    }

//    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
//        // This is called when a new Loader needs to be created.  This
//        // sample only has one Loader, so we don't care about the ID.
//        // First, pick the base URI to use depending on whether we are
//        // currently filtering.
//        val baseUri: Uri = ContactsContract.Contacts.CONTENT_URI
//
//        // Now create and return a CursorLoader that will take care of
//        // creating a Cursor for the data being displayed.
//        val select: String = "((${Contacts.DISPLAY_NAME} NOTNULL) AND (" +
//                "${Contacts.HAS_PHONE_NUMBER}=1) AND (" +
//                "${Contacts.DISPLAY_NAME} != ''))"
//        return (activity as? Context)?.let { context ->
//            CursorLoader(
//                    context,
//                    baseUri,
//                    CONTACTS_SUMMARY_PROJECTION,
//                    select,
//                    null,
//                    "${Contacts.DISPLAY_NAME} COLLATE LOCALIZED ASC"
//            )
//        } ?: throw Exception("Activity cannot be null")
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        toolbarLayout = binding.toolbarLayout
        itemDetailTextView = binding.itemDetail
        itemDetailIngredientsTextView = binding.itemDetailIngredients
        itemDetailTitleTextView = binding.itemDetailTitle

        updateContent()
        rootView.setOnDragListener(dragListener)

        return rootView
    }

    private fun updateContent() {
        toolbarLayout?.title = item?.title

        // Show the placeholder content as text in a TextView.
        item?.let {
            itemDetailTextView.text = stringToArray(it.directions).joinToString("\n")
            itemDetailIngredientsTextView?.text = stringToArray(it.ingredients).joinToString("\n")
            itemDetailTitleTextView?.text = item?.title
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}