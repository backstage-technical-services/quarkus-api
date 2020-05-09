package org.backstage.error

interface ErrorCode {
    companion object {
        const val NOT_NULL = "NotNull"
        const val NOT_MISSING = "NotMissing"
        const val NOT_BLANK = "NotBlank"
        const val INVALID_FORMAT = "InvalidFormat"
        const val INVALID_DATETIME_FORMAT = "InvalidDateTime"
        const val INVALID_DATE_RANGE = "InvalidDateRange"
        const val INVALID_ENUM_VALUE = "UnknownValue"
        const val UNKNOWN_ENUM_VALUE = INVALID_ENUM_VALUE
        const val INVALID_JSON_TYPE = "InvalidTypeId"
        const val INCORRECT_TYPE = "IncorrectType"
    }
}
