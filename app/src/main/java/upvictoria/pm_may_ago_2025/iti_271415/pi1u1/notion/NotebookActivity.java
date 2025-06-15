package upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;

public class NotebookActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText markdownEditor;
    private Markwon markwon;
    private MarkwonEditor markwonEditor;
    private boolean isShiftPressed = false;
    private boolean isPreviewMode = false;
    private Button previewButton;
    private int lastRenderPosition = 0;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String originalMarkdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_notebook);

            Intent intent = getIntent();
            if (intent == null || !intent.hasExtra("notebook_id") || !intent.hasExtra("notebook_title")) {
                Log.e("NotebookActivity", "Datos del cuaderno no recibidos");
                showErrorAndFinish("Datos del cuaderno no recibidos");
                return;
            }

            initMarkdownEditor();

            String notebookId = intent.getStringExtra("notebook_id");
            String notebookTitle = intent.getStringExtra("notebook_title");
            originalMarkdown = "# " + notebookTitle + "\n\n";
            markdownEditor.setText(originalMarkdown);

            if (notebookTitle == null || notebookTitle.isEmpty()) {
                Log.e("NotebookActivity", "Título del cuaderno vacío");
                showErrorAndFinish("Título del cuaderno no válido");
                return;
            }

            initToolbar(notebookTitle);
            initNavigationDrawer();
            setupPreviewButton();

        } catch (Exception e) {
            Log.e("NotebookActivity", "Error en onCreate", e);
            showErrorAndFinish("Error crítico al iniciar: " + e.getMessage());
        }
    }

    private void showErrorAndFinish(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void initToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            showErrorAndFinish("Toolbar no encontrada");
            return;
        }

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        if (drawerLayout == null || navigationView == null) {
            Log.e("NotebookActivity", "Componentes de navegación no encontrados");
            return;
        }

        navigationView.setNavigationItemSelectedListener(this);

        ImageButton menuButton = findViewById(R.id.menuButton);
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        }
    }

    private void initMarkdownEditor() {
        markdownEditor = findViewById(R.id.markdownEditor);
        if (markdownEditor == null) {
            showErrorAndFinish("Editor no encontrado");
            return;
        }

        try {
            markwon = Markwon.builder(this)
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(TaskListPlugin.create(this))
                    .build();

            //markwonEditor = MarkwonEditor.create(markwon);

            String notebookTitle = getIntent().getStringExtra("notebook_title");
            markdownEditor.setText("# " + notebookTitle + "\n\n");

            //setupEditorListeners();
        } catch (Exception e) {
            Log.e("NotebookActivity", "Error al inicializar Markwon", e);
            showErrorAndFinish("Error al configurar el editor");
        }
    }

//    private void setupEditorListeners() {
//        markdownEditor.setOnKeyListener((v, keyCode, event) -> {
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
//                processCurrentBlock();
//                return true;
//            }
//            return false;
//        });
//
//        markdownEditor.addTextChangedListener(new TextWatcher() {
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (editable.toString().endsWith("```\n")) {
//                    processCurrentBlock();
//                }
//            }
//        });
//    }

    private void setupPreviewButton() {
        previewButton = findViewById(R.id.previewButton);
        if (previewButton == null) {
            return;
        }

        previewButton.setOnClickListener(v -> togglePreview());
    }

    private void togglePreview() {
        if (isPreviewMode) {
            // Volver a modo edición
            markdownEditor.setText(originalMarkdown);
            previewButton.setText(R.string.preview);
            enableEditing(true);
        } else {
            // Entrar en modo vista previa
            originalMarkdown = cleanMarkdownContent(markdownEditor.getText().toString());
            String processedMarkdown = prepareForPreview(originalMarkdown);

            Spanned markdownSpanned = markwon.toMarkdown(processedMarkdown);
            markdownEditor.setText(markdownSpanned);
            previewButton.setText(R.string.edit);
            enableEditing(false);
        }
        isPreviewMode = !isPreviewMode;
    }

    private String cleanMarkdownContent(String content) {
        // 1. Normalizar espacios alrededor de formatos
        content = content.replaceAll("\\*\\*(\\s+)", "**$1")
                .replaceAll("(\\s+)\\*\\*", "$1**")
                .replaceAll("\\*(\\s+)", "*$1")
                .replaceAll("(\\s+)\\*", "$1*");

        // 2. Limpiar espacios en saltos de línea
        content = content.replaceAll("\\s+\\n", "\n")
                .replaceAll("\n\\s+", "\n");

        // 3. Asegurar doble espacio para saltos de línea simples
        return content.replaceAll("(?<=\\S)\n", "  \n");
    }

    private String prepareForPreview(String markdown) {
        // Convertir saltos de línea simples a dobles para mejor renderizado
        return markdown.replaceAll("(?<=\\S)\n", "\n\n");
    }

    private void enableEditing(boolean enable) {
        markdownEditor.setFocusable(enable);
        markdownEditor.setFocusableInTouchMode(enable);
        markdownEditor.setCursorVisible(enable);
        if (enable) {
            markdownEditor.requestFocus();
            // Colocar cursor al final
            markdownEditor.setSelection(markdownEditor.getText().length());
        }
    }

//    private void processCurrentBlock() {
//        try {
//            String fullText = markdownEditor.getText().toString();
//            int cursorPos = markdownEditor.getSelectionStart();
//
//            String renderedText = fullText.substring(0, lastRenderPosition);
//            String newText = fullText.substring(lastRenderPosition);
//
//            Spanned newMarkdown = markwon.toMarkdown(newText);
//
//            SpannableStringBuilder finalText = new SpannableStringBuilder();
//            finalText.append(renderedText);
//            finalText.append(newMarkdown);
//
//            markwon.setParsedMarkdown(markdownEditor, finalText);
//
//            lastRenderPosition = cursorPos;
//            markdownEditor.getText().insert(cursorPos, "\n");
//            markdownEditor.setSelection(cursorPos + 1);
//
//        } catch (Exception e) {
//            Log.e("MARKDOWN", "Error en bloques", e);
//        }
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.new_page) {
            createNewPage();
            return true;
        } else if (id == R.id.export_pdf) {
            exportCurrentPageToPdf();
            return true;
        } else if (id == R.id.back_to_notebooks) {
            finish();
            return true;
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void createNewPage() {
        Toast.makeText(this, "Nueva página creada", Toast.LENGTH_SHORT).show();
    }

    private void exportCurrentPageToPdf() {
        String contentToExport;
        if (isPreviewMode) {
            // Si estamos en vista previa, usamos el original guardado
            contentToExport = originalMarkdown;
        } else {
            // Si estamos editando, usamos el texto actual
            contentToExport = markdownEditor.getText().toString();
        }

        String title = getIntent().getStringExtra("notebook_title");
        PdfExporter.exportToPdf(this, title, contentToExport);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(navigationView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}