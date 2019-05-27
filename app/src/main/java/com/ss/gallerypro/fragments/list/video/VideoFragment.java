package com.ss.gallerypro.fragments.list.video;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.ss.gallerypro.R;
import com.ss.gallerypro.adapter.VideoAdapter;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.view.GridSpacingItemDecoration;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    private static final String TAG = "VideoFragment";
    private static int NUM_COLUMNS;
    private ArrayList<MediaItem> mVideoList;
    private VideoAdapter videoAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mSwipeRefreshListener;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GridSpacingItemDecoration mGridSpacingItemDecoration;
    private DesertPlaceholder desertPlaceholder;

    private ActionMode mActionMode;

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public VideoFragment() {
        mVideoList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_setting, container, false);
//        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
//        desertPlaceholder = v.findViewById(R.id.placeholder);
//        mSwipeRefreshListener = () -> {
//            Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
//            // This method performs the actual data-refresh operation.
//            // The method calls setRefreshing(false) when it's finished.
//            new GetVideosTask().execute();
//            videoAdapter.notifyDataSetChanged();
//        };
//
//        mSwipeRefreshLayout.setOnRefreshListener(mSwipeRefreshListener);
//            mSwipeRefreshLayout.post(() -> {
//                mSwipeRefreshLayout.setRefreshing(true);
//                mSwipeRefreshListener.onRefresh();
//            });
//
//        initAlbumRecycleView(v);
//        implementRecyclerViewClickListeners();
        return v;
    }

    private void initAlbumRecycleView(View v) {
//        recyclerView = v.findViewById(R.id.albumRecycleView);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            NUM_COLUMNS = VideoHelper.getNumbColumnPort(getActivity());
//        } else {
//            NUM_COLUMNS = VideoHelper.getNumbColumnLand(getActivity());
//        }
//        mGridSpacingItemDecoration = new GridSpacingItemDecoration(NUM_COLUMNS, 4, false);
//        recyclerView.setHasFixedSize(true);
//        videoAdapter = new VideoAdapter(getActivity(), mVideoList);
//        recyclerView.addItemDecoration(mGridSpacingItemDecoration);
//        mLayoutManager = new GridlayoutManagerFixed(getContext(), NUM_COLUMNS);
//        recyclerView.setLayoutManager(mLayoutManager);
//        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
//        recyclerView.setLayoutAnimation(animation);
//        recyclerView.setItemAnimator(new LandingAnimator());
//        recyclerView.setAdapter(videoAdapter);
    }

    private void implementRecyclerViewClickListeners() {

    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    //Set enable SwipeRefreshLayout
    public void setEnableSwipeRefresh(boolean isEnable) {
        if(isEnable) {
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setDistanceToTriggerSync(0);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setDistanceToTriggerSync(999999);
        }
    }

    private class GetVideosTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
//            mBuckets = CPHelper.getAlbums(getActivity(), mapFilterToArray());
//            return mBuckets.size();
            return 1;
        }

        @Override
        protected void onPostExecute(Integer size) {
//            albumAdapter.updateDataToView(mBuckets);
//            albumAdapter.notifyDataSetChanged();
//            // se set lai sau
//            albumAdapter.changeSortingMode(getSortingMode());
//            mSwipeRefreshLayout.setRefreshing(false);
//            if(mBuckets.size()==0) {
//                recyclerView.setVisibility(View.GONE);
//                desertPlaceholder.setVisibility(View.VISIBLE);
//            } else {
//                recyclerView.setVisibility(View.VISIBLE);
//                desertPlaceholder.setVisibility(View.GONE);
//            }
            super.onPostExecute(size);
        }
    }


}
