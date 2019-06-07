/*
 * Copyright (C) 2017 Google, Inc.
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
package com.oshimamasara.myrssappad;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MENU_ITEM_VIEW_TYPE = 0;

    private final Context mContext;

    private final List<Object> mRecyclerViewItems;

    public RecyclerViewAdapter(Context context, List<Object> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView menuItemTitle;
        private TextView menuItemPubDate;
        private TextView menuItemDescription;
        private TextView menuItemLink;

        MenuItemViewHolder(View view) {
            super(view);
            menuItemTitle = (TextView) view.findViewById(R.id.menu_item_title);
            menuItemPubDate = (TextView) view.findViewById(R.id.menu_item_pubDate);
            menuItemDescription = (TextView) view.findViewById(R.id.menu_item_description);
        }
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return MENU_ITEM_VIEW_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.menu_item_container, viewGroup, false);
        return new MenuItemViewHolder(menuItemLayoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
        MenuItem menuItem = (MenuItem) mRecyclerViewItems.get(position);

        menuItemHolder.menuItemTitle.setText(menuItem.getTitle());
        menuItemHolder.menuItemPubDate.setText(menuItem.getPubDate());
        menuItemHolder.menuItemDescription.setText(menuItem.getDescription());
    }
}
