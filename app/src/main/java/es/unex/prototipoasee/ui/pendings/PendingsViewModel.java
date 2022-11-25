package es.unex.prototipoasee.ui.pendings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PendingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PendingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Pendings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}