package xyz.artenes.budget.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.TypeConverter
import xyz.artenes.budget.core.models.Money
import xyz.artenes.budget.core.IconsMap
import xyz.artenes.budget.core.serializer.DateSerializer
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class Converters {

    private val dateSerializer = DateSerializer()

    @TypeConverter
    fun fromLocalDate(localDate: LocalDate?): String? {
        if (localDate == null) {
            return null
        }
        return dateSerializer.serializeDate(localDate)
    }

    @TypeConverter
    fun stringToLocalDate(string: String?): LocalDate? {
        if (string == null) {
            return null
        }
        return dateSerializer.deserializeDate(string)
    }

    @TypeConverter
    fun fromOffset(offset: OffsetDateTime?): Long? {
        return offset?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun longToOffset(long: Long?): OffsetDateTime? {
        if (long == null) {
            return null
        }
        return Instant.ofEpochMilli(long).atOffset(OffsetDateTime.now().offset)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(value: String?): UUID? {
        if (value == null) {
            return null
        }
        return UUID.fromString(value)
    }

    @TypeConverter
    fun fromImageVector(value: ImageVector?): String? {
        return value?.name
    }

    @TypeConverter
    fun toImageVector(value: String?): ImageVector? {
        if (value == null) {
            return null
        }
        return IconsMap.getIcon(value)
    }

    @TypeConverter
    fun fromMoney(value: Money?): Int? {
        return value?.value
    }

    @TypeConverter
    fun toMoney(value: Int?): Money? {
        if (value == null) {
            return null
        }
        return Money(value)
    }

}