package com.lubsolution.store.libraries.searchview;

import com.ferfalk.simplesearchview.SimpleSearchView;

/**
 * @author Fernando A. H. Falkiewicz
 */
public abstract class SimpleSearchViewListener implements SimpleSearchView.SearchViewListener {
    @Override
    public void onSearchViewShown() {
        // No action
    }

    @Override
    public void onSearchViewClosed() {
        // No action
    }

    @Override
    public void onSearchViewShownAnimation() {
        // No action
    }

    @Override
    public void onSearchViewClosedAnimation() {
        // No action
    }
}
