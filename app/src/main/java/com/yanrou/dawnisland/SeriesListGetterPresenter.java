package com.yanrou.dawnisland;

import java.util.List;

public class SeriesListGetterPresenter {


    private SeriesListView view;
    private SeriesListGetterModel model;

    SeriesListGetterPresenter(SeriesListView seriesListView) {
        this.view = seriesListView;
        model = new SeriesListGetterModel(this);
    }

    void getFirstPage() {
        view.setStartGetFirstPage();
        model.getNextPage(SeriesListGetterModel.FirstPage);
    }

    void getFirstPageSuccess(List<Object> items) {
        view.setFirstPage(items);
    }

    void getNextPage() {
        view.setGetNextPage();
        model.getNextPage(SeriesListGetterModel.NextPage);
    }

    void getNextPageSuccess() {
        view.setNextPage();
    }

    void startRefresh() {
        model.refresh();
    }

    void refreshSuccess() {
        view.setRefreshSuccess();
    }

    void changeForum(int fid) {
        model.changeForum(fid);
    }

}
