/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.elements;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by willi on 21.12.14.
 */
public class DAdapter {

    public interface DView {

        void onBindViewHolder(RecyclerView.ViewHolder viewHolder);

        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup);

        String getTitle();

        BaseFragment getFragment();

    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public final List<DView> DViews;
        private OnItemClickListener onItemClickListener;
        private int selectedItem;
        private boolean itemOnly;

        public Adapter(List<DView> DViews) {
            this.DViews = DViews;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DViews.get(position).onBindViewHolder(holder);
        }

        @Override
        public int getItemCount() {
            return DViews.size();
        }

        public void setItemChecked(int position, boolean checked) {
            try {
                ((Item) DViews.get(position)).setChecked(checked);
                ((Item) DViews.get(selectedItem)).setChecked(false);
            } catch (ClassCastException ignored) {
            }
            selectedItem = position;
        }

        public void setItemOnly(boolean itemOnly) {
            this.itemOnly = itemOnly;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = DViews.get(viewType).onCreateViewHolder(parent);
            setOnClickListener(DViews.get(viewType), viewHolder.itemView);
            return viewHolder;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        private void setOnClickListener(final DView dView, View view) {
            boolean onClick = false;
            if (itemOnly)
                onClick = dView instanceof Item;
            else if (onItemClickListener != null) onClick = true;
            if (onClick) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(v, DViews.indexOf(dView));
                    }
                });
            }
        }

    }

    public static class Item implements DView {

        private final String title;
        private final BaseFragment fragment;
        private View view;
        private TextView text;
        private boolean checked;
        private int defaultTextColor;
        private int checkedTextColor;
        private int defaultBackgroundColor;
        private int checkedBackgroundColor;

        public Item(String title, BaseFragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public BaseFragment getFragment() {
            return fragment;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
            checkedTextColor = view.getResources().getColor(R.color.color_primary);
            defaultTextColor = view.getResources().getColor(Utils.DARKTHEME ? R.color.white : R.color.black);
            defaultBackgroundColor = view.getResources().getColor(android.R.color.transparent);
            checkedBackgroundColor = view.getResources().getColor(Utils.DARKTHEME ?
                    R.color.navigationdrawer_selected_background_dark : R.color.navigationdrawer_selected_background_light);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            view = viewHolder.itemView;
            if (Utils.isTV(view.getContext())) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
            }
            text = (TextView) view.findViewById(R.id.text);
            text.setText(title);
            view.setBackgroundColor(checked ? checkedBackgroundColor : defaultBackgroundColor);
            text.setTextColor(checked ? checkedTextColor : defaultTextColor);
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            if (view != null && text != null) {
                view.setBackgroundColor(checked ? checkedBackgroundColor : defaultBackgroundColor);
                text.setTextColor(checked ? checkedTextColor : defaultTextColor);
            }
        }

    }

    public static class Header implements DView {

        private final String title;

        public Header(String title) {
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_header, viewGroup, false)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            ((TextView) viewHolder.itemView.findViewById(R.id.text)).setText(title.toUpperCase());
            if (Utils.DARKTHEME)
                viewHolder.itemView.findViewById(R.id.divider_view).setBackgroundColor(viewHolder.itemView.getResources()
                        .getColor(R.color.divider_background_dark));
        }

    }

}