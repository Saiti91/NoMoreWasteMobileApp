package fr.esgi.pa.nomorewasteapp.api

import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    val Recipes_ID: Int,
    val Name: String,
    val Instructions: String,
    val Ingredients: List<Ingredient>,
    val imageUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Ingredient.CREATOR) ?: listOf(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(Recipes_ID)
        parcel.writeString(Name)
        parcel.writeString(Instructions)
        parcel.writeTypedList(Ingredients)
        parcel.writeString(imageUrl)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}

data class Ingredient(
    val Product_ID: Int,
    val Name: String,
    val Quantity: String?,
    val Unit: String?,
    val Description: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(Product_ID)
        parcel.writeString(Name)
        parcel.writeString(Quantity)
        parcel.writeString(Unit)
        parcel.writeString(Description)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient {
            return Ingredient(parcel)
        }

        override fun newArray(size: Int): Array<Ingredient?> {
            return arrayOfNulls(size)
        }
    }
}
