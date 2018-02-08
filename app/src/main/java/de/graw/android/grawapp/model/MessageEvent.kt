package de.graw.android.grawapp.model

class MessageEvent(var messageEventType: MessageEventType,var message: String, var userObject:Any?)

enum class MessageEventType {
    STATION_CLICK,CHART_LOADER
}
