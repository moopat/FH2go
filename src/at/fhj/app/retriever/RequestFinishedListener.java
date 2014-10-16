package at.fhj.app.retriever;

import java.util.ArrayList;

/**
 * Created by markus on 25.03.2014.
 */
public interface RequestFinishedListener {
    void onSuccess(ArrayList resultList);

    void onFail(String status);
}
