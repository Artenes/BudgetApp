package xyz.artenes.budget.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ChildFriendly
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.EscalatorWarning
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.Gite
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.OtherHouses
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.RoomPreferences
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsBar
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Villa
import androidx.compose.ui.graphics.vector.ImageVector

object IconsMap {

    fun getIcon(name: String): ImageVector {

        return when (name) {
            "Filled.Storefront" -> Icons.Filled.Storefront
            "Filled.Apartment" -> Icons.Filled.Apartment
            "Filled.FitnessCenter" -> Icons.Filled.FitnessCenter
            "Filled.BusinessCenter" -> Icons.Filled.BusinessCenter
            "Filled.Spa" -> Icons.Filled.Spa
            "Filled.MeetingRoom" -> Icons.Filled.MeetingRoom
            "Filled.House" -> Icons.Filled.House
            "Filled.CorporateFare" -> Icons.Filled.CorporateFare
            "Filled.AcUnit" -> Icons.Filled.AcUnit
            "Filled.Cottage" -> Icons.Filled.Cottage
            "Filled.FamilyRestroom" -> Icons.Filled.FamilyRestroom
            "Filled.Checkroom" -> Icons.Filled.Checkroom
            "Filled.OtherHouses" -> Icons.Filled.OtherHouses
            "Filled.AllInclusive" -> Icons.Filled.AllInclusive
            "Filled.Grass" -> Icons.Filled.Grass
            "Filled.AirportShuttle" -> Icons.Filled.AirportShuttle
            "Filled.ChildCare" -> Icons.Filled.ChildCare
            "Filled.BeachAccess" -> Icons.Filled.BeachAccess
            "Filled.Pool" -> Icons.Filled.Pool
            "Filled.Kitchen" -> Icons.Filled.Kitchen
            "Filled.Casino" -> Icons.Filled.Casino
            "Filled.HolidayVillage" -> Icons.Filled.HolidayVillage
            "Filled.RoomService" -> Icons.Filled.RoomService
            "Filled.Roofing" -> Icons.Filled.Roofing
            "Filled.RoomPreferences" -> Icons.Filled.RoomPreferences
            "Filled.SportsBar" -> Icons.Filled.SportsBar
            "Filled.FreeBreakfast" -> Icons.Filled.FreeBreakfast
            "Filled.Bathtub" -> Icons.Filled.Bathtub
            "Filled.EscalatorWarning" -> Icons.Filled.EscalatorWarning
            "Filled.ChildFriendly" -> Icons.Filled.ChildFriendly
            "Filled.Foundation" -> Icons.Filled.Foundation
            "Filled.FoodBank" -> Icons.Filled.FoodBank
            "Filled.Gite" -> Icons.Filled.Gite
            "Filled.Villa" -> Icons.Filled.Villa
            "Filled.NightShelter" -> Icons.Filled.NightShelter
            "Filled.DirectionsBus" -> Icons.Filled.DirectionsBus
            "Filled.MonitorHeart" -> Icons.Filled.MonitorHeart
            "Filled.LocalGroceryStore" -> Icons.Filled.LocalGroceryStore
            "Filled.Book" -> Icons.Filled.Book
            "Filled.Home" -> Icons.Filled.Home
            "Filled.QuestionMark" -> Icons.Filled.QuestionMark
            "Filled.MonetizationOn" -> Icons.Filled.MonetizationOn
            "Filled.Money" -> Icons.Filled.Money
            else -> throw RuntimeException("Invalid icon $name")
        }

    }

}