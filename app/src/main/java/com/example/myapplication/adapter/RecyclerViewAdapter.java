package com.example.myapplication.adapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter<T extends ViewService> extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private ArrayList<T> list;

    public RecyclerViewAdapter() {
        list = new ArrayList<>();
    }

    public T get(int position) {
        return list.get(position);
    }

    public void add(T item) {
        if (item != null)
            list.add(item);
    }

    public void clear() {
        for (T item : list) {
            item.onViewDestroyed();
        }
        list.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return RecyclerViewHolder.newInstance(parent, viewType, list.get(viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        T item = list.get(position);
        if (item != null) {
            item.onViewCreated(holder.itemView);
            item.initView();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private RecyclerViewHolder(View view) {
            super(view);
        }

        private static <T extends ViewService> RecyclerViewHolder newInstance(
                ViewGroup parent, int position, T itemUi) {
            return new RecyclerViewHolder(itemUi.makeView(parent));
        }
    }

    public static abstract class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;

        public RecyclerItemClickListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent motionEvent) {
            if (gestureDetector.onTouchEvent(motionEvent)) {
                ViewGroup childView = (ViewGroup) view.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (childView != null) {
                    final List<View> viewHierarchy = new ArrayList<>();
                    getViewHierarchyUnderChild(childView, motionEvent.getRawX(), motionEvent.getRawY(), viewHierarchy);

                    View touchedView = childView;
                    if (viewHierarchy.size() > 0) {
                        touchedView = viewHierarchy.get(0);
                    }
                    onItemClick(childView, touchedView, view.getChildAdapterPosition(childView));
                    return true;
                }
            }
            return false;
        }

        private static void getViewHierarchyUnderChild(ViewGroup root, float x, float y, List<View> viewHierarchy) {
            int[] location = new int[2];
            int childCount = root.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                View child = root.getChildAt(i);
                child.getLocationOnScreen(location);
                int childLeft = location[0], childRight = childLeft + child.getWidth();
                int childTop = location[1], childBottom = childTop + child.getHeight();
                if (child.isShown() && x >= childLeft && x <= childRight && y >= childTop && y <= childBottom) {
                    viewHierarchy.add(0, child);
                }
                if (child instanceof ViewGroup) {
                    getViewHierarchyUnderChild((ViewGroup) child, x, y, viewHierarchy);
                }
            }
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        public abstract void onItemClick(View item, View childViewClicked, int position);
    }
}