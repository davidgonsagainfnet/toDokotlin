package exemple.com.todo.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object Analytics {

    fun sendAnalytics(firebaseAnalytics: FirebaseAnalytics, id: String, flag: String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, flag)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

}