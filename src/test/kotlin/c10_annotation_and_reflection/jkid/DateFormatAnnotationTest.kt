package c10_annotation_and_reflection.jkid

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import с10_annotation_and_reflection.jkid.exercise.DateFormat
import с10_annotation_and_reflection.jkid.serialization.serialize
import java.text.SimpleDateFormat
import java.util.*

data class PersonWithBithDate(
    val name: String,
    @DateFormat("dd-MM-yyyy") val birthDate: Date
)

class DateFormatTest {
    private val value = PersonWithBithDate("Alice", SimpleDateFormat("dd-MM-yyyy").parse("13-02-1987"))
    private val json = """{"birthDate": "13-02-1987", "name": "Alice"}"""

    @Test
    fun testSerialization() {
        assertEquals(json, serialize(value))
    }

//    @Test fun testDeserialization() {
//        assertEquals(value, deserialize(json))
//    }
}