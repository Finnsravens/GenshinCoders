package ani.saikou.others

import com.google.firebase.crashlytics.FirebaseCrashlytics

object DisableFirebase {
    var disabled = true
    fun handle(){
        if(disabled) FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
    }
}