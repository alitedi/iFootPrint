package com.example.alireza.empreints;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 16/1/6.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ListItemViewHolder> {

    private List<Records> mDataset;
    private SparseBooleanArray selectedItems;

    CustomAdapter(List<Records> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        mDataset = modelData;
        selectedItems = new SparseBooleanArray();
    }

    /**
     * Adds and item into the underlying data set
     * at the position passed into the method.
     *
     * @param newModelData The item to add to the data set.
     * @param position     The index of the item to add.
     */
    public void add(Records newModelData, int position) {
        mDataset.add(position, newModelData);
        notifyItemInserted(position);
    }

    /**
     * Removes the item that currently is at the passed in position from the
     * underlying data set.
     *
     * @param position The index of the item to remove.
     */
    public void removeData(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
//
//    public Records getItem(int position) {
//        return mDataset.get(position);
//    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, int position) {
        final Records model = mDataset.get(position);
        viewHolder.title.setText(model.getRecordsName());
        viewHolder.comment.setText(model.getCommnets());
        viewHolder.ranking.setText(model.getRanking());
        viewHolder.itemView.setActivated(selectedItems.get(position, false));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                final Intent intent = new Intent(viewHolder.itemView.getContext(), MapsActivity.class);
                intent.putExtra("Record", model);
                viewHolder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView comment;
        TextView ranking;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titleW);
            comment = (TextView) itemView.findViewById(R.id.commentW);
            ranking = (TextView) itemView.findViewById(R.id.rankingW);
        }
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
//
//    public void swap(List<Records> data){
//        mDataset.clear();
//        mDataset.addAll(data);
//    }
}