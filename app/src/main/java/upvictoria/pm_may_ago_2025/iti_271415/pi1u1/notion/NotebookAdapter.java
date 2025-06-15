package upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotebookAdapter extends RecyclerView.Adapter<NotebookAdapter.NotebookViewHolder> {

    private List<Notebook> notebooks;
    private OnNotebookClickListener listener;

    public interface OnNotebookClickListener {
        void onNotebookClick(Notebook notebook);
    }

    public NotebookAdapter(List<Notebook> notebooks, OnNotebookClickListener listener) {
        this.notebooks = notebooks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotebookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notebook, parent, false);
        return new NotebookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotebookViewHolder holder, int position) {
        Notebook notebook = notebooks.get(position);
        holder.titleTextView.setText(notebook.getTitle());

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotebookClick(notebook);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notebooks.size();
    }

    public void addNotebook(Notebook notebook) {
        notebooks.add(notebook);
        notifyItemInserted(notebooks.size() - 1);
    }

    public void updateNotebooks(List<Notebook> newNotebooks) {
        notebooks.clear();
        notebooks.addAll(newNotebooks);
        notifyDataSetChanged();
    }

    static class NotebookViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView;

        public NotebookViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.notebookCard);
            titleTextView = itemView.findViewById(R.id.notebookTitle);
        }
    }
}