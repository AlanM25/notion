package upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;

public class NotebookActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Componentes de UI
    private EditText markdownEditor;
    private Button previewButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // Markdown
    private Markwon markwon;
    private boolean isPreviewMode = false;
    private String originalMarkdown;

    // Datos
    private DatabaseRepository databaseRepository;
    private String currentNotebookId;
    private String currentPageId;
    private boolean isNewPage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        try {
            // 1. Inicializar vistas
            initViews();

            // 2. Configurar repositorio y obtener datos iniciales
            databaseRepository = new DatabaseRepository(this);
            currentNotebookId = getIntent().getStringExtra("notebook_id");

            // 3. Manejar estado guardado
            if (savedInstanceState != null) {
                restoreState(savedInstanceState);
            } else {
                initNewNotebookState();
            }

            // 4. Configurar Markwon
            setupMarkwon();

            // 5. Cargar la página inicial
            loadInitialPage();

            // 6. Configurar toolbar y navegación
            initToolbar(getIntent().getStringExtra("notebook_title"));
            initNavigationDrawer();

        } catch (Exception e) {
            Log.e("NotebookActivity", "Error en onCreate", e);
            showErrorAndFinish("Error crítico al iniciar: " + e.getMessage());
        }
    }

    private void initViews() {
        markdownEditor = findViewById(R.id.markdownEditor);
        previewButton = findViewById(R.id.previewButton);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        previewButton.setOnClickListener(v -> togglePreview());
    }

    private void restoreState(Bundle savedInstanceState) {
        currentPageId = savedInstanceState.getString("currentPageId");
        isPreviewMode = savedInstanceState.getBoolean("isPreviewMode");
        originalMarkdown = savedInstanceState.getString("originalMarkdown");
    }

    private void initNewNotebookState() {
        Intent intent = getIntent();
        String notebookTitle = intent.getStringExtra("notebook_title");
        originalMarkdown = "# " + notebookTitle + "\n\n";
    }

    private void setupMarkwon() {
        markwon = Markwon.builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TaskListPlugin.create(this))
                .build();
    }

    private void loadInitialPage() {
        new Thread(() -> {
            List<Page> pages = databaseRepository.getPagesByNotebook(currentNotebookId);
            runOnUiThread(() -> {
                if (pages.isEmpty()) {
                    createFirstPage();
                } else {
                    loadPage(pages.get(0));
                }
            });
        }).start();
    }

    private void createFirstPage() {
        Page newPage = new Page(getIntent().getStringExtra("notebook_title"), currentNotebookId);
        databaseRepository.addPage(newPage);
        currentPageId = newPage.getId();
        markdownEditor.setText(newPage.getContent());
    }

    private void loadPage(Page page) {
        currentPageId = page.getId();
        isNewPage = false;

        if (isPreviewMode) {
            originalMarkdown = page.getContent();
            String processed = prepareForPreview(originalMarkdown);
            markdownEditor.setText(markwon.toMarkdown(processed));
        } else {
            markdownEditor.setText(page.getContent());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentNotebookId", currentNotebookId);
        outState.putString("currentPageId", currentPageId);
        outState.putBoolean("isPreviewMode", isPreviewMode);
        outState.putString("originalMarkdown", originalMarkdown);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCurrentPage(null);
    }

    private void initToolbar(String title) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void initNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        loadPagesIntoNavigationDrawer();

        ImageButton menuButton = findViewById(R.id.menuButton);
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        }
    }

    private void loadPagesIntoNavigationDrawer() {
        new Thread(() -> {
            List<Page> pages = databaseRepository.getPagesByNotebook(currentNotebookId);
            runOnUiThread(() -> {
                Menu menu = navigationView.getMenu();

                // 1. Limpiar SOLO el grupo de páginas dinámicas
                MenuItem dynamicGroup = menu.findItem(R.id.dynamic_pages);
                if (dynamicGroup != null) {
                    menu.removeGroup(R.id.dynamic_pages);
                } else {
                    // Si no existe el grupo, limpiar cualquier ítem dinámico previo
                    for (int i = menu.size() - 1; i >= 0; i--) {
                        MenuItem item = menu.getItem(i);
                        if (item.getGroupId() != R.id.fixed_menu) {
                            menu.removeItem(item.getItemId());
                        }
                    }
                }

                // 2. Agregar páginas con IDs únicos basados en su posición
                for (int i = 0; i < pages.size(); i++) {
                    menu.add(R.id.dynamic_pages, i, Menu.NONE, pages.get(i).getTitle())
                            .setIcon(R.drawable.ic_page)
                            .setCheckable(true);
                }

                // 3. Marcar la página actual como seleccionada
                if (currentPageId != null) {
                    for (int i = 0; i < pages.size(); i++) {
                        if (pages.get(i).getId().equals(currentPageId)) {
                            menu.getItem(i + 3).setChecked(true); // +3 por los ítems fijos
                            break;
                        }
                    }
                }
            });
        }).start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Manejar ítems fijos
        if (id == R.id.new_page) {
            createNewPage();
            drawerLayout.closeDrawers();
            return true;
        } else if (id == R.id.export_pdf) {
            exportCurrentPageToPdf();
            drawerLayout.closeDrawers();
            return true;
        } else if (id == R.id.back_to_notebooks) {
            finish();
            return true;
        }

        // Manejar páginas dinámicas
        saveCurrentPage(() -> {
            int position = item.getItemId(); // Usamos el ID como posición
            loadPageById(position);
        });

        drawerLayout.closeDrawers();
        return true;
    }

    private void loadDynamicPage(int position) {
        new Thread(() -> {
            List<Page> pages = databaseRepository.getPagesByNotebook(currentNotebookId);
            if (position >= 0 && position < pages.size()) {
                Page page = pages.get(position);
                runOnUiThread(() -> {
                    currentPageId = page.getId();
                    if (isPreviewMode) {
                        originalMarkdown = page.getContent();
                        markdownEditor.setText(markwon.toMarkdown(prepareForPreview(originalMarkdown)));
                    } else {
                        markdownEditor.setText(page.getContent());
                    }
                });
            }
        }).start();
    }

    private void createNewPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva página");
        View view = getLayoutInflater().inflate(R.layout.dialog_create_notebook, null);
        TextInputEditText titleInput = view.findViewById(R.id.titleInput);
        builder.setView(view);

        builder.setPositiveButton("Crear", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            if (!title.isEmpty()) {
                new Thread(() -> {
                    Page newPage = new Page(title, currentNotebookId);
                    databaseRepository.addPage(newPage);

                    runOnUiThread(() -> {
                        currentPageId = newPage.getId();
                        markdownEditor.setText(newPage.getContent());
                        loadPagesIntoNavigationDrawer(); // Actualiza el menú
                        Toast.makeText(this, "Página creada", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            }
        });
        builder.show();
    }

    private void loadPageById(int position) {
        new Thread(() -> {
            List<Page> pages = databaseRepository.getPagesByNotebook(currentNotebookId);
            if (position >= 0 && position < pages.size()) {
                Page page = pages.get(position);
                runOnUiThread(() -> {
                    currentPageId = page.getId();

                    if (isPreviewMode) {
                        originalMarkdown = page.getContent();
                        markdownEditor.setText(markwon.toMarkdown(prepareForPreview(originalMarkdown)));
                    } else {
                        markdownEditor.setText(page.getContent());
                    }

                    // Actualizar selección en el menú
                    Menu menu = navigationView.getMenu();
                    for (int i = 0; i < menu.size(); i++) {
                        menu.getItem(i).setChecked(menu.getItem(i).getItemId() == position);
                    }
                });
            }
        }).start();
    }

    private void saveCurrentPage(Runnable onComplete) {
        if (currentPageId == null || markdownEditor == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        new Thread(() -> {
            String content = isPreviewMode ? originalMarkdown : markdownEditor.getText().toString();
            Page page = new Page();
            page.setId(currentPageId);
            page.setContent(content);
            page.setTitle(content.split("\n")[0].replace("#", "").trim());
            page.setNotebookId(currentNotebookId);

            databaseRepository.updatePage(page);
            if (onComplete != null) runOnUiThread(onComplete);
        }).start();
    }

    private void togglePreview() {
        isPreviewMode = !isPreviewMode;

        if (isPreviewMode) {
            originalMarkdown = markdownEditor.getText().toString();
            String processed = prepareForPreview(originalMarkdown);
            markdownEditor.setText(markwon.toMarkdown(processed));
            previewButton.setText(R.string.edit);
        } else {
            markdownEditor.setText(originalMarkdown);
            previewButton.setText(R.string.preview);
        }
        enableEditing(!isPreviewMode);
    }

    private String prepareForPreview(String markdown) {
        return markdown.replaceAll("(?<=\\S)\n", "\n\n");
    }

    private void enableEditing(boolean enable) {
        markdownEditor.setFocusable(enable);
        markdownEditor.setFocusableInTouchMode(enable);
        markdownEditor.setCursorVisible(enable);
        if (enable) {
            markdownEditor.requestFocus();
            markdownEditor.setSelection(markdownEditor.getText().length());
        }
    }

    private void showErrorAndFinish(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(navigationView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportCurrentPageToPdf() {
        String contentToExport = isPreviewMode ? originalMarkdown : markdownEditor.getText().toString();
        String title = getIntent().getStringExtra("notebook_title");
        PdfExporter.exportToPdf(this, title, contentToExport);
    }
}