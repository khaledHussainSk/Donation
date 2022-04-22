package com.khaled.donation.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.khaled.donation.databinding.FragmentDialogSignOutBinding;

public class DialogSignOutFragment extends DialogFragment {
    FragmentDialogSignOutBinding binding;

    public DialogSignOutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDialogSignOutBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }
}