package com.training.apps.makeup.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.training.apps.makeup.R;

public class SignUpDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setTitle(R.string.client_or_provider)
                .setItems(R.array.sign_in_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent1 = new Intent(getContext(), SignUpActivity.class);
                                intent1.putExtra("new", "client");
                                startActivity(intent1);
                                break;
                            case 1:
                                Intent intent2 = new Intent(getContext(), SignUpActivity.class);
                                intent2.putExtra("new", "provider");
                                startActivity(intent2);
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        ListView listView = dialog.getListView();
        listView.setDivider(new ColorDrawable(Color.BLACK)); // set color
        listView.setDividerHeight(1);
        return dialog;
    }
}
