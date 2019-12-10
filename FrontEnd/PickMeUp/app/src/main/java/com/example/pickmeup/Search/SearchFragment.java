package com.example.pickmeup.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pickmeup.R;

public class SearchFragment extends Fragment {

    /**
     * @param inflater inflates the search tab
     * @param container holds the fragment
     * @param savedInstanceState returns the last possible saved state of the fragment if possible
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //once the class is called it will fetch the search fragment for viewing
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}
