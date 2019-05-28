package com.ss.gallerypro.fragments.list.albums.album.presenter;

import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.fragments.list.albums.album.OnAlbumDataNotify;
import com.ss.gallerypro.fragments.list.albums.album.model.IAlbumRepository;
import com.ss.gallerypro.fragments.list.albums.album.view.IAlbumsView;

import java.util.ArrayList;

public class AlbumsPresenterImpl implements IAlbumsPresenter {
    private IAlbumsView mView;
    private IAlbumRepository mRepository;

    public AlbumsPresenterImpl(IAlbumsView view, IAlbumRepository repository) {
        this.mView = view;
        this.mRepository = repository;
    }

    @Override
    public void getAlbums() {
        mRepository.getAlbums(getAlbumCallback);
    }

    @Override
    public void deleteAlbums(ArrayList<Bucket> buckets) {
        mRepository.deleteAlbums(buckets, deleteAlbumCallback);
    }

    @Override
    public boolean isListAlbumEmpty(ArrayList<Bucket> buckets) {
        return buckets == null || buckets.size() == 0;
    }

    // callback mechanism , onResponse will be triggered with response

    // by Repo(or your network or database process) and pass the response to view
    private final OnAlbumDataNotify.GetAlbum getAlbumCallback = new OnAlbumDataNotify.GetAlbum() {
        @Override
        public void onResponse(ArrayList<Bucket> buckets) {
            mView.onGetAlbumSuccess(buckets);
        }

        @Override
        public void onError(String errMsg) {

        }
    };

    private final OnAlbumDataNotify.DeleteAlbum deleteAlbumCallback = new OnAlbumDataNotify.DeleteAlbum() {
        @Override
        public void onResponse() {
            mView.onDeleteAlbumSuccess();
        }

        @Override
        public void onError(String errMsg) {

        }
    };
}
