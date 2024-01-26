package с10_annotation_and_reflection.jkid.exercise

@Target(AnnotationTarget.PROPERTY)
annotation class DateFormat(val format: String)