package com.example.pickmeup.CustomObjects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.Account.Admin.AdminCourtAdapter;
import com.example.pickmeup.R;

import org.jetbrains.annotations.NotNull;

public class SwipeToDeleteCourt extends ItemTouchHelper.SimpleCallback {
    private AdminCourtAdapter mAdapter;
    private Drawable icon;
    private ColorDrawable background;
    private Context mCtx;

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.deleteItem(position, direction);
    }

    public SwipeToDeleteCourt(AdminCourtAdapter adapter, Context mCtx) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mCtx = mCtx;
        mAdapter = adapter;
        icon = ContextCompat.getDrawable(mCtx, R.drawable.ic_check_white_24dp);
        background = new ColorDrawable(Color.WHITE);
    }

    @Override
    public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) { // Swiping to the right
            icon = ContextCompat.getDrawable(mCtx, R.drawable.ic_check_white_24dp);
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            background = new ColorDrawable(Color.rgb(0, 137, 0));

            int iconLeft = 40;
            int iconRight = 95;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop() + 6,
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom() - 6);

            Vibrator v = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert v != null;
                v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                assert v != null;
                v.vibrate(1000);
            }
        } else if (dX < 0) { // Swiping to the left
            icon = ContextCompat.getDrawable(mCtx, R.drawable.ic_delete_white_24dp);
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            background = new ColorDrawable(Color.rgb(255, 0, 0));

            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop() + 6, itemView.getRight(), itemView.getBottom() - 6);

            Vibrator v = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert v != null;
                v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                assert v != null;
                v.vibrate(1000);
            }
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }

}
