package com.onlytherecipe

data class GroceryItem(
    val id: Long,
    val text: String,
    val checked: Boolean = false,
    // Recipe this item came from. recipeSavedAt links back to the recipe row
    // (null = manual add → lives under "Other"). recipeName is snapshotted at
    // add time so deleting or renaming the recipe doesn't break the grocery
    // group heading.
    val recipeSavedAt: Long? = null,
    val recipeName: String = ""
)
