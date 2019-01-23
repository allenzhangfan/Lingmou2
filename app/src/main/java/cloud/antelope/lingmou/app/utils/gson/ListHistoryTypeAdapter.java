package cloud.antelope.lingmou.app.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.lingdanet.safeguard.common.utils.LogUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.HistoryKVStoreRequest;

public class ListHistoryTypeAdapter extends TypeAdapter<List<HistoryKVStoreRequest.History>> {

    private static final String TAG = "ListHistoryTypeAdapter";

    public static final Type TYPE = new TypeToken<List<HistoryKVStoreRequest.History>>() {
    }.getType();

    private static class ListHistoryTypeAdapterHolder {
        private static final ListHistoryTypeAdapter INSTANCE = new ListHistoryTypeAdapter();
    }

    public static ListHistoryTypeAdapter getInstance() {
        return ListHistoryTypeAdapterHolder.INSTANCE;
    }

    private ListHistoryTypeAdapter() {
    }

    @Override
    public void write(JsonWriter out, List<HistoryKVStoreRequest.History> value) throws IOException {
        out.beginArray();
        for (HistoryKVStoreRequest.History history : value) {
            out.value(history.toString());
        }
        out.endArray();
    }

    @Override
    public List<HistoryKVStoreRequest.History> read(JsonReader in) throws IOException {
        List<HistoryKVStoreRequest.History> histories = new ArrayList<>();
        HistoryKVStoreRequest.History history;
        in.beginArray();
        while (in.hasNext()) {
            try {
                history = readHistory(in);
                if (history != null) {
                    histories.add(history);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        in.endArray();
        return histories;
    }


    private HistoryKVStoreRequest.History readHistory(JsonReader in) throws IOException {
        String value = in.nextString();
        String[] values = value.split("_");
        if (values.length == 4) {
            return new HistoryKVStoreRequest.History(valueOf(values[0]), values[1],
                    valueOf(values[2]), valueOf(values[3]));
        }
        LogUtils.e(TAG, "history data format error: " + value);
        return null;
    }

    private static long valueOf(String s) {
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
