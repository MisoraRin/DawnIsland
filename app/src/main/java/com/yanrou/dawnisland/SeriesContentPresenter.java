package com.yanrou.dawnisland;

import java.util.List;

public class SeriesContentPresenter {
    private SeriesContentModel model;
    private SeriesContentView view;

    public SeriesContentPresenter(String id, SeriesContentView view) {
        this.view = view;
        this.model = new SeriesContentModel(id, this);
    }

    public void loadMore() {
        model.loadNextPage();
    }

    public void loadMoreSuccess() {
        view.loadMoreSuccess();
    }

    public void loadMoreFail() {

    }

    public void refresh() {
        model.loadFrontPage();
    }

    public void refreshSuccess(int itemCount) {
        view.refreshSuccess(itemCount);
    }

    public void refreshFail() {

    }

    public void loadFirstPage() {
        model.loadFirst();
    }

    public void loadFirstPageSuccess(List<Object> items) {
        view.setFirstPage(items);
    }

    public void loadFirstPageFail() {

    }

    public void jumpPage(int page) {
        model.jumpPage(page);
    }

    public void jumpSuccess() {
        view.jumpSuccess();
    }
}
