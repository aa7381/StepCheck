package com.example.stepcheck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the RecyclerView that displays a list of products.
 * This adapter manages the data and creates the views for each item in the list.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> masterList = new ArrayList<>();
    private final List<Product> filteredList = new ArrayList<>();
    private OnItemClickListener listener;

    /**
     * Interface for handling click events on items in the RecyclerView.
     */
    public interface OnItemClickListener {
        /**
         * Called when an item in the RecyclerView is clicked.
         * @param product The product that was clicked.
         */
        void onItemClick(Product product);
    }

    /**
     * Sets the listener for item click events.
     * @param listener The listener to set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the data for the adapter.
     * @param products The list of products to display.
     */
    public void setData(List<Product> products) {
        masterList.clear();
        masterList.addAll(products);
        filter("");
    }

    /**
     * Filters the list of products based on a query string.
     * @param query The query to filter the list by.
     */
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(masterList);
        } else {
            for (Product product : masterList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = filteredList.get(position);
        holder.productName.setText(product.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;

        /**
         * Constructs a new ViewHolder.
         * @param view The view that this ViewHolder will hold.
         */
        public ViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.product_name);
        }
    }
}
