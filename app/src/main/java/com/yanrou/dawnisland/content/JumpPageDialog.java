package com.yanrou.dawnisland.content;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.yanrou.dawnisland.R;

import java.util.Objects;

/**
 * @author suche
 */
public class JumpPageDialog extends DialogFragment {
    SeriesContentViewModel viewModel;
    private TextView pageTextView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this.getContext()));

        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View view = inflater.inflate(R.layout.jump_page_dialog, null);
        pageTextView = view.findViewById(R.id.total_page_count);
        EditText editText = view.findViewById(R.id.jump_to_page);
        builder.setTitle("跳页");
        builder.setView(view);
        builder.setPositiveButton("跳页", (dialog, which) -> {
            int page = Integer.parseInt(editText.getText().toString());
            dialog.dismiss();
        });
        return builder.create();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(Objects.requireNonNull(this.getActivity())).get(SeriesContentViewModel.class);
        String page = viewModel.getNowPage() + "/" + viewModel.getTotalPage();
        pageTextView.setText(page);
    }
}
