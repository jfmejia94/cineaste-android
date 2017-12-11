package de.cineaste.android.controllFlow;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import de.cineaste.android.R;
import de.cineaste.android.adapter.MovieListAdapter;
import de.cineaste.android.viewholder.MovieViewHolder;

public abstract class BaseItemTouchHelperCallback extends ItemTouchHelper.Callback {

    final private Resources resources;
    final LinearLayoutManager linearLayoutManager;
    final MovieListAdapter movieListAdapter;
    final RecyclerView recyclerView;

    BaseItemTouchHelperCallback(LinearLayoutManager linearLayoutManager, MovieListAdapter movieListAdapter, RecyclerView recyclerView, Resources resources) {
        this.linearLayoutManager = linearLayoutManager;
        this.movieListAdapter = movieListAdapter;
        this.recyclerView = recyclerView;
        this.resources = resources;
    }

    abstract BaseSnackBar getSnackBar();

    abstract int getRightSwipeMessage();

    abstract int getIcon();

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        movieListAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.LEFT) {
            getSnackBar().getSnackBarLeftSwipe(position);
        } else {
            getSnackBar().getSnackBarRightSwipe(position, getRightSwipeMessage());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;

            Drawable icon;
            Drawable background;

            int iconMargin = 48;
            int iconLeft;
            int iconRight;
            int intrinsicWidth;
            if (dX > 0) {
                icon = resources.getDrawable(getIcon());
                background = new ColorDrawable(resources.getColor(R.color.colorAccent));

                intrinsicWidth = icon.getIntrinsicWidth();

                iconLeft = itemView.getLeft() + iconMargin;
                iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
                icon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            } else {
                icon = resources.getDrawable(R.drawable.ic_delete_white);
                background = new ColorDrawable(resources.getColor(R.color.colorPrimary));

                intrinsicWidth = icon.getIntrinsicWidth();

                iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                iconRight = itemView.getRight() - iconMargin;
                icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }

            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicHeight = icon.getIntrinsicHeight();
            int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int iconBottom = iconTop + intrinsicHeight;

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());

            background.draw(c);
            icon.draw(c);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            MovieViewHolder movieViewHolder = (MovieViewHolder) viewHolder;
            movieViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        MovieViewHolder movieViewHolder = (MovieViewHolder) viewHolder;
        movieViewHolder.onItemClear();

        movieListAdapter.updatePositionsInDb();
    }
}
