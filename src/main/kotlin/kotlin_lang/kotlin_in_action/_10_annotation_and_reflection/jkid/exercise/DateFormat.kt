package kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.exercise

import kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.ValueSerializer
import java.text.SimpleDateFormat
import java.util.Date

@Target(AnnotationTarget.PROPERTY)
annotation class DateFormat(val format: String)

class DateSerializer(format: String): ValueSerializer<Date> {
    private val simpleDateFormat = SimpleDateFormat(format)

    override fun toJsonValue(value: Date): Any? = simpleDateFormat.format(value)

    override fun fromJsonValue(jsonValue: Any?): Date = simpleDateFormat.parse(jsonValue as String)

}