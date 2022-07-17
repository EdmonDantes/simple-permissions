package io.github.edmondantes.simple.permissions.exception

@Suppress("unused")
class NotValidPermissionFormatException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) :
            super(message, cause, enableSuppression, writableStackTrace)

    companion object {
        fun notValidPermission(permission: String): NotValidPermissionFormatException =
                NotValidPermissionFormatException("Not valid permission format for '$permission'")
    }
}