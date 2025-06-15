package upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotebookAdapter.OnNotebookClickListener {

    private RecyclerView notebooksRecyclerView;
    private NotebookAdapter notebookAdapter;
    private List<Notebook> notebooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notebooksRecyclerView = findViewById(R.id.notebooksRecyclerView);
        notebooksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        notebookAdapter = new NotebookAdapter(notebooks, this);
        notebooksRecyclerView.setAdapter(notebookAdapter);

        Button createNotebookButton = findViewById(R.id.createNotebookButton);
        createNotebookButton.setOnClickListener(v -> showCreateNotebookDialog());

        Button exportPdfButton = findViewById(R.id.exportPdfButton);
        exportPdfButton.setOnClickListener(v -> exportAllToPdf());
    }

    private void showCreateNotebookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_notebook);

        View view = getLayoutInflater().inflate(R.layout.dialog_create_notebook, null);
        TextInputEditText titleInput = view.findViewById(R.id.titleInput);

        builder.setView(view);
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            if (!title.isEmpty()) {
                Notebook notebook = new Notebook(title);
                notebookAdapter.addNotebook(notebook);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void exportAllToPdf() {
        // exportar a pdf
        PdfExporter.exportNotebooksToPdf(this, notebooks);
    }

    @Override
    public void onNotebookClick(Notebook notebook) {
        try {
            Log.d("MainActivity", "Abriendo cuaderno: " + notebook.getTitle());

            // perros idiotas de los logs
            if (notebook == null) {
                Toast.makeText(this, "Error: Cuaderno no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, NotebookActivity.class);
            intent.putExtra("notebook_id", notebook.getId());
            intent.putExtra("notebook_title", notebook.getTitle());
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.e("MainActivity", "Error al iniciar NotebookActivity", e);
                Toast.makeText(this, "Error al abrir el cuaderno", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error en onNotebookClick", e);
        }
    }
}