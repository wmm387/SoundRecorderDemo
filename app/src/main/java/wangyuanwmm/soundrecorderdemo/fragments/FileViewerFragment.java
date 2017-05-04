package wangyuanwmm.soundrecorderdemo.fragments;

import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wangyuanwmm.soundrecorderdemo.R;
import wangyuanwmm.soundrecorderdemo.adapter.FileViewerAdapter;

/**
 * 文件列表界面
 * Created by Administrator on 2017/5/2.
 */

public class FileViewerFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String TAG = "FileViewerFragment";

    private int position;
    private FileViewerAdapter mFileViewerAdapter;

    public static FileViewerFragment newInstance(int position) {
        FileViewerFragment f = new FileViewerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        observer.startWatching();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //文件列表按数据库中最新的到最旧的排列
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFileViewerAdapter = new FileViewerAdapter(getActivity(), llm);
        mRecyclerView.setAdapter(mFileViewerAdapter);

        return v;
    }

    //FileObserver类是一个用于监听文件访问、创建、修改、删除、移动等操作的监听器
    FileObserver observer = new FileObserver(android.os.Environment
            .getExternalStorageDirectory()
            .toString() + "/SoundRecorder") {
        @Override
        public void onEvent(int event, String file) {
            if (event == FileObserver.DELETE) {
                //用户在此应用之外删除了录音文件
                String filePath = android.os.Environment
                        .getExternalStorageDirectory()
                        .toString() + "/SoundRecorder" + file + "]";
                Log.d(TAG, "File deleted [" + android.os.Environment
                        .getExternalStorageDirectory().toString()
                        + "/SoundRecorder" + file + "]");

                //从数据库中删除录音文件
                mFileViewerAdapter.removeOutOfApp(filePath);
            }
        }
    };
}
