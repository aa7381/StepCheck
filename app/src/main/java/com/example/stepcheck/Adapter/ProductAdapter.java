package com.example.stepcheck.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stepcheck.R;
import com.example.stepcheck.models.Product;
import com.example.stepcheck.utils.FBRef;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the RecyclerView that displays a list of products.
 * This adapter manages the data and creates the views for each item in the list.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    
    /**
     * The master list of all products.
     */
    private final List<Product> masterList = new ArrayList<>();
    
    /**
     * The list of products after filtering.
     */
    private final List<Product> filteredList = new ArrayList<>();
    
    /**
     * Listener for item click events.
     */
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
     * Sets the data for the adapter and updates the filtered list.
     * @param products The list of products to display.
     */
    public void setData(List<Product> products) {
        masterList.clear();
        masterList.addAll(products);
        filter("");
    }

    /**
     * Filters the list of products based on a query string.
     * Searches in both name and ID (barcode).
     * @param query The query to filter the list by.
     */
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(masterList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Product product : masterList) {
                // Search in both name and ID (barcode)
                if (product.getName().toLowerCase().contains(lowerCaseQuery) ||
                    product.getId().toLowerCase().contains(lowerCaseQuery)) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Product product = filteredList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format(Locale.getDefault(), "Price: $%.2f", product.getPrice()));

        // Clear previous image to avoid flickering
        holder.productImage.setImageResource(R.mipmap.ic_launcher);

        // Load image from Firebase Storage
        StorageReference shoeFolderRef = FBRef.refStorage.child("shoes").child(product.getId());
        
        shoeFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (!listResult.getItems().isEmpty()) {
                    listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(holder.itemView.getContext())
                                    .load(uri)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.mipmap.ic_launcher)
                                    .centerCrop()
                                    .into(holder.productImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            holder.productImage.setImageResource(R.mipmap.ic_launcher);
                        }
                    });
                } else {
                    holder.productImage.setImageResource(R.mipmap.ic_launcher);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.productImage.setImageResource(R.mipmap.ic_launcher);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(product);
                }
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
        /**
         * TextView for displaying the product name.
         */
        public TextView productName;
        
        /**
         * TextView for displaying the product price.
         */
        public TextView productPrice;
        
        /**
         * ImageView for displaying the product image.
         */
        public ImageView productImage;

        /**
         * Constructs a new ViewHolder and initializes its views.
         * @param view The view that this ViewHolder will hold.
         */
        public ViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.tvProductName);
            productPrice = view.findViewById(R.id.tvProductPrice);
            productImage = view.findViewById(R.id.ivProduct);
        }
    }
}
